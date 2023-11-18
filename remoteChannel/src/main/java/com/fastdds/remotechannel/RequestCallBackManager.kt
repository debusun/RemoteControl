package com.fastdds.remotechannel

import android.util.Log

object RequestCallBackManager {
    const val TAG = "RequestCallBackManager"
    //这个类有一个接口，这个接口是用来处理请求的回调
    interface RequestCallBack {
        fun onRequestCallBack(action: String): String
    }
    //有一个List集合，用来存储所有的回调
    private val requestCallBackList = ArrayList<RequestCallBack>()
    fun addRequestCallBack(requestCallBack: RequestCallBack) {
        requestCallBackList.add(requestCallBack)
    }
    fun removeRequestCallBack(requestCallBack: RequestCallBack) {
        requestCallBackList.remove(requestCallBack)
    }
    //有一个方法，用来触发回调
    @Synchronized
    fun onRequestCallBack(action: String) : String{
        var result = ""
        for (requestCallBack in requestCallBackList) {
            //这里有点问题，理论上这个类可能不应该为List，而应该为Map，这样可以根据action来获取对应的回调和模块
            result = requestCallBack.onRequestCallBack(action)
            Log.d(TAG, "onRequestCallBack: $result")
        }
        return result
    }
}