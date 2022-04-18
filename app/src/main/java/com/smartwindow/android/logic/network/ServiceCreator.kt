package com.smartwindow.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private const val BASE_URL = "124.223.64:50051"

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun <T> create(serverClass: Class<T>): T = retrofit.create(serverClass)

    inline fun <reified T> create(): T = create(T::class.java)
}