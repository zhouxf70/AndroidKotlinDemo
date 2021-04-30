package com.example.androidkotlindemo.network

import com.example.androidkotlindemo.network.bean.UserInfo
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by zxf on 2021/3/14
 */
interface ApiService {

    @GET("/users/{userId}")
    suspend fun queryUserInfo(@Path("userId") userId: String): UserInfo

    @HEAD
    @GET("/users/{userId}")
    fun queryUserInfo2(@Path("userId") userId: String): Observable<UserInfo>
}