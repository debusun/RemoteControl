package com.debusun.remotecontrol.module.air

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.debusun.remotecontrol.model.AirRequest
import com.debusun.remotecontrol.model.AirResult
import com.fastdds.remotechannel.RemoteControlLib
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AirManagerImpl {
    const val TAG = "AirManagerImpl"

    @Synchronized
    fun dispatchAirAction(context: Context, action: String): AirResult {
        val airRequest = Gson().fromJson(action, AirRequest::class.java)
        airRequest?.let {
            Log.d(TAG, "dispatchAirAction: ${airRequest.action}")
            when (airRequest.action) {
                AirConditionState.AC_POWER_SWITCH.name -> {
                    if (airRequest.data as Double == AirConditionState.SWITCH_ON.toDouble()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            RemoteControlLib.requestAction.postValue("远程打开空调")
                        }
                        return AirResult(0, "空调打开成功", 0)
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            RemoteControlLib.requestAction.postValue("远程关闭空调")
                        }
                        return AirResult(0, "空调关闭成功", 0)
                    }
                }

                AirConditionState.AC_FLOW_MODE.name -> {
                    return AirResult(0, "", 0)
                }

                else -> {
                    return AirResult(0, "", 0)
                }
            }
        }
        return AirResult(0, "", 0)
    }
}