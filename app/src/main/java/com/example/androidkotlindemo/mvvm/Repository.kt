package com.example.androidkotlindemo.mvvm

import androidx.lifecycle.MutableLiveData
import com.example.androidkotlindemo.common.KLog
import com.example.androidkotlindemo.network.RetrofitManager
import com.example.androidkotlindemo.network.bean.UserInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by zxf on 2021/3/13
 */
object Repository {

    private val api = RetrofitManager.apiService

    fun queryUserInfo(liveData: MutableLiveData<String>, userId: String) {

    }

    fun testGitBranch(){

    }
}