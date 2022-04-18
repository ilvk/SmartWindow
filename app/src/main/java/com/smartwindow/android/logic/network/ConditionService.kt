package com.smartwindow.android.logic.network

import com.smartwindow.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET

interface ConditionService {

    @GET("http://124.223.64:50051")
    fun getRealtimeCondition():
            Call<RealtimeResponse>
}