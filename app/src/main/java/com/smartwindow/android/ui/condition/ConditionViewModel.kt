package com.smartwindow.android.ui.condition

import androidx.lifecycle.ViewModel
import com.smartwindow.android.logic.Repository
import java.util.*

class ConditionViewModel: ViewModel() {

    val conditionLiveData = Repository.refreshCondition()

    fun refreshCondition() {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                val conditionLiveData = Repository.refreshCondition()
            }
        }
        timer.schedule(task, 2000, 1000)
    }
}