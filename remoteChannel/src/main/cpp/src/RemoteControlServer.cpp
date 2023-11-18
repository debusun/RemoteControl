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
static JavaVM* g_vm;
static jclass jClassLib = NULL;
static jobject sCallbacksObj;
static jmethodID method_onRequestCallBack;

const char *TAG = "RemoteControlServer";

void printLog(const char *message) {
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "%s", message);
};

class RemoteControlServer {
    //构建一个数据读取类，用来监听Client端的请求
    class RequestListener : public eprosima::fastdds::dds::DataReaderListener {
    private:
        // 监听的writer
        eprosima::fastdds::dds::DataWriter *writer_;
    public:
        RequestListener(eprosima::fastdds::dds::DataWriter *writer) : writer_(writer) {};

        std::string onRequestCallBack(const char *message) {
            JNIEnv* env = NULL;
            if (g_vm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
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
            jobject result =  env->CallObjectMethod(sCallbacksObj, method_onRequestCallBack, jmessage);
            if (result == nullptr) {
                printLog("result is null");
                //如果接口为空也要删除掉jmessage的Ref，防止泄露
//                env->DeleteLocalRef(jmessage);
            }
            const char *str = env->GetStringUTFChars(static_cast<jstring>(result), nullptr);
            std::string cppString(str);
            //需要释放掉result
            env->ReleaseStringUTFChars(static_cast<jstring>(result), str);
            env->DeleteLocalRef(jmessage);
            // 使用完毕后分离当前线程
            g_vm->DetachCurrentThread();
            return str;
        }

        void on_data_available(
                eprosima::fastdds::dds::DataReader *reader) override {
            printLog("on_data_available has data return");
            // 获取请求类型和携带参数
            RequestType requestType;
            eprosima::fastdds::dds::SampleInfo sample_info;
            //去读消息
            reader->take_next_sample(&requestType, &sample_info);
            if (eprosima::fastdds::dds::ALIVE_INSTANCE_STATE == sample_info.instance_state) {
                ReplyType reply;
                //直接将收到的数据抛到Java层
                std::string result = onRequestCallBack(requestType.action().c_str());
                if (result.empty()) {
                    printLog("result is null from java!!");
                }
                reply.reply(result);
                //Java层回复String后，将该消息回复到Client端
                //根据获取请求时拿到的sample_info来确认返回通道
                eprosima::fastrtps::rtps::WriteParams writeParams;
                writeParams.related_sample_identity().writer_guid(
                        sample_info.sample_identity.writer_guid());
                writeParams.related_sample_identity().sequence_number(
                        sample_info.sample_identity.sequence_number());
                //做下类型转换,作为为返回类型回调出去，结束此次请求（如果不接输而带参数是否意味着能想接口一样接收返回参数？）
                writer_->write(reinterpret_cast<void *>(&reply), writeParams);
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

    eprosima::fastdds::dds::DataWriter *reply_writer_ = nullptr;

    eprosima::fastdds::dds::DataReader *request_reader_ = nullptr;

    eprosima::fastdds::dds::TypeSupport request_type_;

    eprosima::fastdds::dds::TypeSupport reply_type_;

    RequestListener listener_ = {nullptr};


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

        eprosima::fastdds::dds::DataWriterQos writer_qos;
        writer_qos.history().kind = eprosima::fastdds::dds::KEEP_ALL_HISTORY_QOS;
        reply_writer_ = publisher_->create_datawriter(reply_topic_, writer_qos);
        if (nullptr == reply_writer_) {
            printLog("create_reply_writer failed");
            return false;
        }
        //实例化请求监听
        listener_ = { reply_writer_};

        eprosima::fastdds::dds::DataReaderQos reader_qos;
        reader_qos.reliability().kind = eprosima::fastdds::dds::RELIABLE_RELIABILITY_QOS;
        reader_qos.durability().kind = eprosima::fastdds::dds::TRANSIENT_LOCAL_DURABILITY_QOS;
        reader_qos.history().kind = eprosima::fastdds::dds::KEEP_ALL_HISTORY_QOS;
        request_reader_ = subscriber_->create_datareader(request_topic_, reader_qos, &listener_);

        if (nullptr == request_reader_) {
            printLog("create_request_reader failed");
            return false;
        }
        printLog("init success");
        return true;
    }

    void stopServer() {
        printLog("stopServer start");
        if (nullptr != participant_) {
            participant_->delete_contained_entities();
            eprosima::fastdds::dds::DomainParticipantFactory::get_instance()->delete_participant(
                    participant_);
        }
        printLog("stopServer end");
    }
};

RemoteControlServer remoteControlServer;

jboolean startRemoteControlServer(JNIEnv *env, jobject obj /* this */) {
    sCallbacksObj = env->NewGlobalRef(obj);
    //获取回调方法,java的onRequestCallBack方法是个返回类型为String的方法
    jclass jClassLib = env->GetObjectClass(sCallbacksObj);
    if (jClassLib == nullptr) {
        printLog("jClassLib is null");
        return false;
    }
    method_onRequestCallBack = env->GetMethodID(jClassLib, "onRequestCallBack",
                                                "(Ljava/lang/String;)Ljava/lang/String;");
//    method_onRequestCallBack = env->GetMethodID(jClassLib, "onRequestCallBack", "(Ljava/lang/String;)V");
    remoteControlServer.init();
    return true;
}

void stopRemoteControlServer(JNIEnv *env, jobject /* this */) {
    printLog("stopServer end");
    remoteControlServer.stopServer();
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
            {"startRemoteControlServer", "()Z", (void *) startRemoteControlServer},
            {"stopRemoteControlServer",  "()V", (void *) stopRemoteControlServer},
    };
    jint ret = env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0]));
    if (ret < 0) {
        return -1;
    }
    return JNI_VERSION_1_6;
}