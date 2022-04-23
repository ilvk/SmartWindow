package com.smartwindow.android.logic

import androidx.lifecycle.liveData
import com.smartwindow.android.logic.model.AskData
import com.smartwindow.android.logic.network.SmartWindowNetwork
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object Repository {

    fun refreshCondition() = fire(Dispatchers.IO) {
        val realtimeResponse = SmartWindowNetwork.getRealtimeCondition()
        Result.success(realtimeResponse)
    }

    fun postAskData(askData: AskData) = fire(Dispatchers.IO) {
        val askForData = SmartWindowNetwork.askForChange(askData)
        Result.success(askForData)
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
}