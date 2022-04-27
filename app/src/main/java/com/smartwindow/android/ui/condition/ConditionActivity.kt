package com.smartwindow.android.ui.condition

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.smartwindow.android.R
import com.smartwindow.android.logic.Repository
import com.smartwindow.android.logic.model.AskData
import com.smartwindow.android.logic.model.RealtimeResponse
import kotlinx.android.synthetic.main.activity_condition.*
import java.net.Socket
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.UnknownHostException
import java.io.IOException
import android.util.Log
import java.io.PrintWriter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import androidx.core.content.ContextCompat.getSystemService




class ConditionActivity : AppCompatActivity() {

    var winState: Int? = null
    lateinit var realtimeResponse: RealtimeResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_condition)

        val handlerReceive: Handler = object: Handler(mainLooper){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == 0) {
                    showConditionData(realtimeResponse)
                }
            }
        }

        val handlerAsk: Handler = object: Handler(mainLooper){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == 0) {
                    if (winState == 0) {
                        Toast.makeText(baseContext, "正在打开窗户", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(baseContext, "正在关闭窗户", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


//        viewModel.conditionLiveData.observe(this, Observer { result ->
//            val realtimeResponse = result.getOrNull()
//            if (realtimeResponse != null) {
//                showConditionData(realtimeResponse)
//            }else {
//                Toast.makeText(this, "无法获取信息", Toast.LENGTH_SHORT).show()
//                result.exceptionOrNull()?.printStackTrace()
//            }
//        })


        Thread{
            kotlin.run {
                //接收数据并展示
                var read: BufferedReader? = null
                var write: PrintWriter? = null
                var socket: Socket? = null
                var ask: String = "keep"
                try {
                    if (socket == null) {
                        socket = Socket("124.223.64.148", 50051)
                    }
                    while (!isFinishing()) {
                        //接收服务器端的消息
                        if (read == null) {
                            read = BufferedReader(InputStreamReader(socket.getInputStream()))
                        }
                        val msg: String = read.readLine()
                        println("客户端接收信息：$msg")
                        val json = JsonParser().parse(msg).getAsJsonObject()
                        realtimeResponse = Gson().fromJson(json, RealtimeResponse::class.java)
                        handlerReceive.sendEmptyMessage(0)
                        //SystemClock.sleep(2000)

                        //发送开关窗指令
                        if (write == null) {
                            write = PrintWriter(
                                BufferedWriter(OutputStreamWriter(socket.getOutputStream())),
                                true
                            ) //发送数据
                        }
                        windowControl.setOnClickListener {
                            if (winState == 0) {
                                handlerAsk.sendEmptyMessage(0)
                                ask = "open"
                            }else {
                                handlerAsk.sendEmptyMessage(0)
                                ask = "close"
                            }
                        }
                        write.println(ask)
                        //SystemClock.sleep(1000)
                    }
                    println("客户端关闭")
                } catch (e: Exception) {
                    println(e.message)
                } finally {
                    read?.close()
                    write?.close()
                    socket?.close()
                }

                //发送开关窗指令
//                windowControl.setOnClickListener {
//                    if (winState == 0) {
//                        handlerAsk.sendEmptyMessage(0)
//                        sendAsk("open")
//                    }else {
//                        handlerAsk.sendEmptyMessage(0)
//                        sendAsk("close")
//                    }
//                }
            }
        }.start()
    }

//    override fun onStart() {
//        super.onStart()
//        viewModel.refreshCondition()
//    }

//    override fun onStop() {
//        super.onStop()
//        //可能需要停止计时器
//
//    }

    private fun sendAsk(openOrClose: String) {
        var write: PrintWriter? = null
        var socket: Socket? = null
        try {
            if (socket == null) {
                socket = Socket("124.223.64.148", 50051)
            }
            while (!isFinishing()) {
                if (write == null) {
                    write = PrintWriter(
                        BufferedWriter(OutputStreamWriter(socket.getOutputStream())),
                        true
                    ) //发送数据
                }
                write.println(openOrClose)
            }
        }catch (e: Exception) {
            println(e.message)
        } finally {
            write?.close()
            socket?.close()
        }
    }

    @SuppressLint("SetTextI18n", "ServiceCast")
    private fun showConditionData(realtimeResponse: RealtimeResponse) {

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val tem = realtimeResponse.wendu
        val hum = realtimeResponse.shidu
        val rain = realtimeResponse.yushui
        val air = realtimeResponse.kongqi
        val sun = realtimeResponse.guangzhao
        val win = realtimeResponse.chuangzi
        val per = realtimeResponse.personnear

        temperature.text = "温度：${tem} °C"
        humidity.text = "湿度：${hum}"
        airCondition.text = "空气：${air}"
        sunshine.text = "光照：${sun}"

        if (rain.toInt() == 1) {
            rainCondition.text = "无雨"
        }else if(rain.toInt() == 2) {
            rainCondition.text = "微雨"
        }else if(rain.toInt() == 3) {
            rainCondition.text = "小雨"
        }else if(rain.toInt() == 4) {
            rainCondition.text = "中雨"
        }else if(rain.toInt() == 5) {
            rainCondition.text = "大雨"
        }else {
            rainCondition.text = "暴雨"
        }


        winState = win.toInt()

        if (win.toInt() == 0) {
//            windowState.text = "窗户状态关"
            windowControl.text = "目前窗户关闭，点击开启窗户"
        }else {
//            windowState.text = "窗户状态开"
            windowControl.text = "目前窗户开启，点击关闭窗户"
        }
        if (per.toInt() == 1) {
            person.text = "附近有人!"
            vibrator.vibrate(3000)
        }else {
            person.text = "附近无人"
            vibrator.cancel()
        }
//        windowState.text = "窗户状态：${win.toInt()}"
//        person.text = "是否有人：${per.toInt()}"

    }

}
