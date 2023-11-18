package com.fastdds.remotechannel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

object RemoteControlLib {
    const val TAG = "RemoteControlLib"
    val requestAction = MutableLiveData<String>()
    @Synchronized
    fun onRequestCallBack(action: String): String {
        Log.d(TAG, "onRequestAction : $action")
        requestAction.postValue("Receive client request action : $action)}")
        val result = RequestCallBackManager.onRequestCallBack(action)
        Log.d(TAG, "onRequestAction result : $result")
        requestAction.postValue("return request result : $result)}")
        return result
    }

    init {
        System.loadLibrary("RemoteControlServer")
        System.loadLibrary("RemoteControlClient")
    }

    /**
     * A native method that is implemented by the 'remoteControl' native library,
     * which is packaged with this application.
     */
    external fun startRemoteControlServer(): Boolean
    external fun stopRemoteControlServer()
    external fun requestAction(action: String): String
    external fun initRequestClient(): Boolean
    external fun releaseRequestClient()

}