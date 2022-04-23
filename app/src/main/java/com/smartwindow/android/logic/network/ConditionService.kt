package com.smartwindow.android.logic.network

import com.smartwindow.android.logic.model.AskData
import com.smartwindow.android.logic.model.RealtimeResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ConditionService {

    @GET("http://124.223.64:50051")  //http://124.223.64:50051
    fun getRealtimeCondition():
            Call<RealtimeResponse>

    @POST("post_data.txt")
    fun askForChange(@Body askData: AskData):
            Call<ResponseBody>

}