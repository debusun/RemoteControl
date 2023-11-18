//
// Created by bcy on 2023/11/15.
//
#include <condition_variable>
#include <mutex>
#include <vector>

#include <fastdds/dds/domain/DomainParticipantFactory.hpp>
#include <fastdds/dds/domain/DomainParticipant.hpp>
#include <fastdds/dds/publisher/Publisher.hpp>
#include <fastdds/dds/publisher/DataWriter.hpp>
#include <fastdds/dds/subscriber/Subscriber.hpp>
#include <fastdds/dds/subscriber/DataReader.hpp>
#include <fastrtps/subscriber/SampleInfo.h>
#include "RemoteControlPubSubTypes.h"
#include <jni.h>
#include <android/log.h>

#define CLASS_REMOTE_CONTROL_SERVER "com/fastdds/remotechannel/RemoteControlLib"
JavaVM *g_vm;
jclass jClassLib = NULL;
jobject sCallbacksObj;
jmethodID method_onRequestCallBack;

const char *TAG = "RemoteControlClient";

void printLog(const char *message) {
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s", message);
};

class RemoteControlClient {
    //构建一个数据读取类，用来监听Client端的请求
    class ReplyListener : public eprosima::fastdds::dds::DataReaderListener {
    private:
    public:
        // 监听的writer
        eprosima::fastdds::dds::DataWriter *writer_;
        //记录请求参数
        eprosima::fastrtps::rtps::WriteParams write_params;
        std::mutex reception_mutex;
        std::condition_variable reception_cv;
        bool received_reply = false;
        std::string reply_message;

        std::string onReplyCallBack(const char *message) {
            JNIEnv *env = NULL;
            if (g_vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
                printLog("GetEnv failed ");
                return NULL;
            }
//    jobject newObj = env->NewGlobalRef(sCallbacksObj);
            if (sCallbacksObj == nullptr) {
                printLog("sCallbacksObj is null");
                return NULL;
            }
            if (method_onRequestCallBack == nullptr) {
                printLog("method_onRequestCallBack is null");
                return NULL;
            }
            if (env->ExceptionCheck()) {
                env->ExceptionDescribe();
            }
            //调用java的onRequestCallBack方法并接收其返回的String类型数据
            jstring jmessage = env->NewStringUTF(message);
            jobject result = env->CallObjectMethod(sCallbacksObj, method_onRequestCallBack,
                                                   jmessage);
            if (result == nullptr) {
                printLog("result is null");
                //如果接口为空也要删除掉jmessage的Ref，防止泄露
                env->DeleteLocalRef(jmessage);
                return NULL;
            }
            const char *str = env->GetStringUTFChars(static_cast<jstring>(result), nullptr);
            std::string cppString(str);
            //需要释放掉result
            env->ReleaseStringUTFChars(static_cast<jstring>(result), str);
            env->DeleteLocalRef(jmessage);
            return str;
        }

        void on_data_available(
                eprosima::fastdds::dds::DataReader *reader) override {
            printLog("client reply result data");
            // 获取请求类型和携带参数
            ReplyType replyType;
            eprosima::fastdds::dds::SampleInfo sample_info;
            //去读消息
            reader->take_next_sample(&replyType, &sample_info);
            if (eprosima::fastdds::dds::ALIVE_INSTANCE_STATE == sample_info.instance_state) {
                if (sample_info.related_sample_identity == write_params.sample_identity()) {
                    //身份标识一致，为请求的返回消息
                    std::unique_lock<std::mutex> lock(reception_mutex);
                    received_reply = true;
                    reply_message = replyType.reply();
                    printLog(reply_message.c_str());
                    //将请求结果抛到Java层，需要调整，Client端并没有结束，理应在client外部取出消息上抛
//                    onReplyCallBack(reply_message.c_str());
                }
                reception_cv.notify_one();
            }
        }
    };

    //定义RemoteControlServer，完成请求订阅
private:
    eprosima::fastdds::dds::DomainParticipant *participant_ = nullptr;

    eprosima::fastdds::dds::Publisher *publisher_ = nullptr;

    eprosima::fastdds::dds::Subscriber *subscriber_ = nullptr;

    eprosima::fastdds::dds::Topic *request_topic_ = nullptr;

    eprosima::fastdds::dds::Topic *reply_topic_ = nullptr;

    eprosima::fastdds::dds::DataWriter *request_writer_ = nullptr;

    eprosima::fastdds::dds::DataReader *reply_reader_ = nullptr;

    eprosima::fastdds::dds::TypeSupport request_type_;

    eprosima::fastdds::dds::TypeSupport reply_type_;

    ReplyListener listener_;


public:
    bool init() {
        printLog("init start");
        //创建发布者和订阅者
        participant_ =
                eprosima::fastdds::dds::DomainParticipantFactory::get_instance()->create_participant(
                        0, eprosima::fastdds::dds::PARTICIPANT_QOS_DEFAULT);

        if (nullptr == participant_) {
            printLog("create_participant failed");
            return false;
        }
        request_type_ = eprosima::fastdds::dds::TypeSupport(new RequestTypePubSubType());
        reply_type_ = eprosima::fastdds::dds::TypeSupport(new ReplyTypePubSubType());

        participant_->register_type(request_type_);
        participant_->register_type(reply_type_);

        publisher_ = participant_->create_publisher(eprosima::fastdds::dds::PUBLISHER_QOS_DEFAULT);

        if (nullptr == publisher_) {
            printLog("create_publisher failed");
            return false;
        }

        subscriber_ = participant_->create_subscriber(
                eprosima::fastdds::dds::SUBSCRIBER_QOS_DEFAULT);

        if (nullptr == subscriber_) {
            printLog("create_subscriber failed");
            return false;
        }

        //发布请求topic
        request_topic_ = participant_->create_topic("RemoteControlRequest", request_type_.get_type_name(),
                                                    eprosima::fastdds::dds::TOPIC_QOS_DEFAULT);
        if (nullptr == request_topic_) {
            printLog("create_request_topic failed");
            return false;
        }
        //发布回复topic
        reply_topic_ = participant_->create_topic("RemoteControlReply", reply_type_.get_type_name(),
                                                  eprosima::fastdds::dds::TOPIC_QOS_DEFAULT);

        if (nullptr == reply_topic_) {
            printLog("create_reply_topic failed");
            return false;
        }

//        eprosima::fastdds::dds::DataWriterQos writer_qos;
//        writer_qos.history().kind = eprosima::fastdds::dds::KEEP_ALL_HISTORY_QOS;
        request_writer_ = publisher_->create_datawriter(request_topic_, eprosima::fastdds::dds::DATAWRITER_QOS_DEFAULT);
        if (nullptr == request_writer_) {
            printLog("create_reply_writer failed");
            return false;
        }

        eprosima::fastdds::dds::DataReaderQos reader_qos;
        reader_qos.reliability().kind = eprosima::fastdds::dds::RELIABLE_RELIABILITY_QOS;
        reader_qos.history().kind = eprosima::fastdds::dds::KEEP_ALL_HISTORY_QOS;
        reply_reader_ = subscriber_->create_datareader(reply_topic_, reader_qos, &listener_);

        if (nullptr == reply_reader_) {
            printLog("create_request_reader failed");
            return false;
        }
        printLog("init success");
        return true;
    }

    void releaseRequest() {
        printLog("stopServer start");
        if (nullptr != participant_) {
            participant_->delete_contained_entities();
            eprosima::fastdds::dds::DomainParticipantFactory::get_instance()->delete_participant(
                    participant_);
        }
        printLog("stopServer end");
    }

    std::string request(std::string action) {
        printLog("request start");
        std::string result;
        //创建请求消息
        RequestType request;
        request.operation(SEND_ACTION);
        request.action(action);
        std::chrono::seconds timeout(5);
        // 清除上一次请求的结果
        listener_.reply_message.clear();
        //将请求消息写入请求topic
        if (request_writer_->write(static_cast<void *>(&request), listener_.write_params)) {
            //锁住请求，等待请求返回
            std::unique_lock<std::mutex> lock(listener_.reception_mutex);
            listener_.reception_cv.wait(lock, [&]() { return listener_.received_reply; });
            result = listener_.reply_message;
            printLog("request end");
            //每次请求完需要把回复接收标志位置为false用来接收下一次数据
            listener_.received_reply = false;
//            if (listener_.reception_cv.wait_for(lock, timeout, [&]() { return listener_.received_reply; })) {
//                // 如果收到了回复，返回结果
//                result = listener_.reply_message;
//                printLog("request end");
//                return result;
//            } else {
//                printLog("request timeout");
//            }
            return result;
        }
        printLog("write request failed");
        return result;
    }
};

RemoteControlClient remoteControlClient;

jboolean initRequestClient(JNIEnv *env, jobject obj /* this */) {
    sCallbacksObj = env->NewGlobalRef(obj);
    //获取回调方法,java的onRequestCallBack方法是个返回类型为String的方法
    jclass jClassLib = env->GetObjectClass(sCallbacksObj);
    if (jClassLib == nullptr) {
        printLog("jClassLib is null");
        return false;
    }
    remoteControlClient.init();
    return true;
}

jstring requestAction(JNIEnv *env, jobject /* this */, jstring action) {
    //打印方法参数action
    const char *actionStr = env->GetStringUTFChars(action, nullptr);
    std::string logMessage = "requestAction : ";
    logMessage += actionStr;
    printLog(logMessage.c_str());
    std::string result = remoteControlClient.request(actionStr);
    return env->NewStringUTF(result.c_str());
}

void releaseRequestClient(JNIEnv *env, jobject /* this */) {
    printLog("releaseRequestClient");
    remoteControlClient.releaseRequest();
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    g_vm = vm;
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    jclass clazz = env->FindClass(CLASS_REMOTE_CONTROL_SERVER);
    if (clazz == nullptr) {
        return -1;
    }
    jClassLib = clazz;
    JNINativeMethod methods[] = {
            {"initRequestClient", "()Z", (void *) initRequestClient},
            {"requestAction",  "(Ljava/lang/String;)Ljava/lang/String;", (void *) requestAction},
            {"releaseRequestClient",  "()V", (void *) releaseRequestClient},
    };
    jint ret = env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0]));
    if (ret < 0) {
        return -1;
    }
    return JNI_VERSION_1_6;
}