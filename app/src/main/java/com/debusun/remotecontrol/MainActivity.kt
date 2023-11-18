package com.debusun.remotecontrol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.debusun.remotecontrol.databinding.ActivityMainBinding
import com.debusun.remotecontrol.module.air.AirManager
import com.debusun.remotecontrol.module.air.AirManagerImpl
import com.fastdds.remotechannel.RemoteControlLib
import com.fastdds.remotechannel.RequestCallBackManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val TAG = "MainActivity"

    val callBack = object : RequestCallBackManager.RequestCallBack {
        override fun onRequestCallBack(action: String): String {
            Log.d(TAG, "RequestCallBackManager onRequestCallBack : $action")
            val result = AirManagerImpl.dispatchAirAction(applicationContext, action)
            val resultJson = Gson().toJson(result)
            Log.d(TAG, "RequestCallBackManager resultJson : $resultJson")
            return Gson().toJson(result)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RequestCallBackManager.removeRequestCallBack(callBack)
        RemoteControlLib.stopRemoteControlServer()
        RemoteControlLib.releaseRequestClient()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.btnStartServer.setOnClickListener {
            if (RemoteControlLib.startRemoteControlServer()) {
                val text = binding.sampleText.text.toString()
                binding.sampleText.text = "$text \nServer start success"
            }
        }

        binding.btnOpenAir.setOnClickListener {
            binding.sampleText.text = "${binding.sampleText.text} \nrequest openAir"
            val result = AirManager.openAir()
            binding.sampleText.text = "${binding.sampleText.text} \nrequest return $result"
        }

        binding.btnCloseAir.setOnClickListener {
            binding.sampleText.text = "${binding.sampleText.text} \nrequest closeAir"
            val result = AirManager.closeAir()
            binding.sampleText.text = "${binding.sampleText.text} \nrequest return $result"
        }

        binding.btnClear.setOnClickListener {
            binding.sampleText.text = ""
        }

        RequestCallBackManager.addRequestCallBack(callBack)
        //接收请求端发送的消息进行显示
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                RemoteControlLib.requestAction.observe(this@MainActivity) {
                    Log.d(TAG, "collect requestAction : $it")
                    binding.sampleText.text = "${binding.sampleText.text} \n $it"
                }
            }
        }
    }
}