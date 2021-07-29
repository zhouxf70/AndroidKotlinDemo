package com.example.androidkotlindemo.network

import com.example.androidkotlindemo.common.KLog
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by zxf on 2021/3/14
 */
object RetrofitManager {

    val apiService: ApiService by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        KLog.d("retrofit init")
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(LoggerInterceptor())
            .build()
        val request = Request.Builder().build()
        val newCall = okHttpClient.newCall(request)
        newCall.execute()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

}