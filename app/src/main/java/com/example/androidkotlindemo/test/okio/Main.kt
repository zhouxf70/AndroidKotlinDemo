package com.example.androidkotlindemo.test.okio

import com.example.androidkotlindemo.network.LoggerInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by zxf on 2021/4/14
 */
fun main() {

    val client = OkHttpClient.Builder()
//        .addInterceptor(LoggerInterceptor())
        .build()

    val request = Request.Builder()
        .get()
        .url("https://api.github.com/users/zhouxf70")
        .build()

    val newCall = client.newCall(request)
    val response = newCall.execute()

//    RequestBody.create()

    val string = response.body()?.string()
    println(string)

}