package com.smartwindow.android.logic.network

import com.smartwindow.android.logic.model.AskData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Path
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object SmartWindowNetwork {
    private val conditionService = ServiceCreator.create(ConditionService::class.java)
    suspend fun getRealtimeCondition() = conditionService.getRealtimeCondition().await()
    suspend fun askForChange(askData: AskData) = conditionService.askForChange(askData).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}