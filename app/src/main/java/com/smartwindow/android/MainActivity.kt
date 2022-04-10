package com.smartwindow.android

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.PrintWriter
import java.lang.Exception
import java.net.InetAddress
import kotlin.concurrent.thread

//import androidx.core.app.ComponentActivity
//import androidx.core.app.ComponentActivity.ExtraData
//import android.icu.lang.UCharacter.GraphemeClusterBreak.T
//import androidx.core.content.ContextCompat.getSystemService
//import androidx.core.app.ComponentActivity
//import androidx.core.app.ComponentActivity.ExtraData
//import android.icu.lang.UCharacter.GraphemeClusterBreak.T
//import androidx.core.content.ContextCompat.getSystemService


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        get_data.setOnClickListener {
            thread {
                try {
                    acceptServer()
                }catch (e: IOException) {
                    e.printStackTrace()
                }
//                acceptServer()
            }
        }
    }

    private fun acceptServer() {
        try {
            val socket = Socket("124.223.64.148", 50051)
            val os = socket.getOutputStream()
            val pw = PrintWriter(os)
            val address = InetAddress.getLocalHost()
            val ip = address.hostAddress
            pw.write("客户端" + ip + "接入服务器")
            pw.flush()
            socket.shutdownOutput()
            socket.close()
        }catch (e:Exception) {
            print(e.message)
        }
    }

}
