package com.debusun.remotecontrol.module.air

import android.util.Log
import com.debusun.remotecontrol.model.AirRequest
import com.debusun.remotecontrol.model.AirResult
import com.fastdds.remotechannel.RemoteControlLib
import com.google.gson.Gson

object AirManager {
    const val TAG = "AirManager"
    var isInitClient = false

    init {
        isInitClient = RemoteControlLib.initRequestClient()
    }

    fun releaseClient() {
        RemoteControlLib.releaseRequestClient()
    }
    fun openAir() : Boolean{
        Log.d(TAG, "openAir: ")
        //构建空调操作请求
        val request = AirRequest("air", AirConditionState.AC_POWER_SWITCH.name, AirConditionState.SWITCH_ON)
        if (isInitClient) {
            val result = RemoteControlLib.requestAction(Gson().toJson(request))
            Log.d(TAG, "openAir result : $result")
            result?.let {
                val airResult = Gson().fromJson(it, AirResult::class.java)
                RemoteControlLib.requestAction.postValue(airResult.message)
                return airResult.code == 0
            }
        }
        return false
    }

    fun closeAir() : Boolean{
        Log.d(TAG, "closeAir: ")
        //构建空调操作请求
        val request = AirRequest("air", AirConditionState.AC_POWER_SWITCH.name, AirConditionState.SWITCH_OFF)
        if (isInitClient) {
            val result = RemoteControlLib.requestAction(Gson().toJson(request))
            Log.d(TAG, "closeAir result : $result")
            result?.let {
                val airResult = Gson().fromJson(it, AirResult::class.java)
                RemoteControlLib.requestAction.postValue(airResult.message)
                return airResult.code == 0
            }
        }
        return false
    }
}