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

class ConditionActivity : AppCompatActivity() {

    var winState: Int? = null

    val viewModel by lazy { ViewModelProviders.of(this).get(ConditionViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_condition)

        windowControl.setOnClickListener {
            if (winState == 0) {
                Toast.makeText(this, "正在打开窗户", Toast.LENGTH_SHORT).show()
                Repository.postAskData(askData = AskData("open"))
            }else {
                Toast.makeText(this, "正在关闭窗户", Toast.LENGTH_SHORT).show()
                Repository.postAskData(askData = AskData("close"))
            }
        }

        viewModel.conditionLiveData.observe(this, Observer { result ->
            val realtimeResponse = result.getOrNull()
            if (realtimeResponse != null) {
                showConditionData(realtimeResponse)
            }else {
                Toast.makeText(this, "无法获取信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshCondition()
    }

//    override fun onStop() {
//        super.onStop()
//        //可能需要停止计时器
//
//    }

    @SuppressLint("SetTextI18n", "ServiceCast")
    private fun showConditionData(realtimeResponse: RealtimeResponse) {

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val tem = realtimeResponse.wendu
        val hum = realtimeResponse.shidu
        val rain = realtimeResponse.yuhsui
        val air = realtimeResponse.kongqi
        val sun = realtimeResponse.guangzhao
        val win = realtimeResponse.chuangzi
        val per = realtimeResponse.personnear

        temperature.text = "温度：${tem.toInt()} °C"
        humidity.text = "湿度：${hum.toInt()}"
        rainCondition.text = "雨水：${rain.toInt()}"
        airCondition.text = "空气：${air.toInt()}"
        sunshine.text = "光照：${sun.toInt()}"

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
