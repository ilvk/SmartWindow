package com.smartwindow.android.ui.condition

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.smartwindow.android.R
import com.smartwindow.android.logic.model.RealtimeResponse
import kotlinx.android.synthetic.main.dataShow.*

class ConditionActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProviders.of(this).get(ConditionViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_condition)
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
//
//    }

    @SuppressLint("SetTextI18n")
    private fun showConditionData(realtimeResponse: RealtimeResponse) {
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
        windowState.text = "窗户状态：${win.toInt()}"
        person.text = "是否有人：${per.toInt()}"

    }
}
