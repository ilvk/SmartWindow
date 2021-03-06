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
                        Toast.makeText(baseContext, "??????????????????", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(baseContext, "??????????????????", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


//        viewModel.conditionLiveData.observe(this, Observer { result ->
//            val realtimeResponse = result.getOrNull()
//            if (realtimeResponse != null) {
//                showConditionData(realtimeResponse)
//            }else {
//                Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show()
//                result.exceptionOrNull()?.printStackTrace()
//            }
//        })


        Thread{
            kotlin.run {
                //?????????????????????
                var read: BufferedReader? = null
                var write: PrintWriter? = null
                var socket: Socket? = null
                var ask: String = "keep"
                try {
                    if (socket == null) {
                        socket = Socket("124.223.64.148", 50051)
                    }
                    while (!isFinishing()) {
                        //???????????????????????????
                        if (read == null) {
                            read = BufferedReader(InputStreamReader(socket.getInputStream()))
                        }
                        val msg: String = read.readLine()
                        println("????????????????????????$msg")
                        val json = JsonParser().parse(msg).getAsJsonObject()
                        realtimeResponse = Gson().fromJson(json, RealtimeResponse::class.java)
                        handlerReceive.sendEmptyMessage(0)
                        //SystemClock.sleep(2000)

                        //?????????????????????
                        if (write == null) {
                            write = PrintWriter(
                                BufferedWriter(OutputStreamWriter(socket.getOutputStream())),
                                true
                            ) //????????????
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
                    println("???????????????")
                } catch (e: Exception) {
                    println(e.message)
                } finally {
                    read?.close()
                    write?.close()
                    socket?.close()
                }

                //?????????????????????
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
//        //???????????????????????????
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
                    ) //????????????
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

        temperature.text = "?????????${tem} ??C"
        humidity.text = "?????????${hum}"
        airCondition.text = "?????????${air}"
        sunshine.text = "?????????${sun}"

        if (rain.toInt() == 1) {
            rainCondition.text = "??????"
        }else if(rain.toInt() == 2) {
            rainCondition.text = "??????"
        }else if(rain.toInt() == 3) {
            rainCondition.text = "??????"
        }else if(rain.toInt() == 4) {
            rainCondition.text = "??????"
        }else if(rain.toInt() == 5) {
            rainCondition.text = "??????"
        }else {
            rainCondition.text = "??????"
        }


        winState = win.toInt()

        if (win.toInt() == 0) {
//            windowState.text = "???????????????"
            windowControl.text = "???????????????????????????????????????"
        }else {
//            windowState.text = "???????????????"
            windowControl.text = "???????????????????????????????????????"
        }
        if (per.toInt() == 1) {
            person.text = "????????????!"
            vibrator.vibrate(3000)
        }else {
            person.text = "????????????"
            vibrator.cancel()
        }
//        windowState.text = "???????????????${win.toInt()}"
//        person.text = "???????????????${per.toInt()}"

    }

}
