package com.example.androidkotlindemo.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidkotlindemo.common.KLog
import com.example.androidkotlindemo.network.RetrofitManager
import com.example.androidkotlindemo.network.bean.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by zxf on 2021/3/13
 */
class HomeViewModel : ViewModel() {

    //    private val repository = Repository
    private val apiService = RetrofitManager.apiService

    val userInfoLiveData: MutableLiveData<State<UserInfo>> = MutableLiveData()

    fun queryUserInfo(userId: String) {
        viewModelScope.launch {
            try {
                val userInfo = apiService.queryUserInfo(userId)
                KLog.d(userInfo)
                userInfoLiveData.value = State.success(userInfo)
            } catch (e: Exception) {
                KLog.d(e)
                userInfoLiveData.value = State.error(e)
            }

//            coroutine(this)
        }
    }
}

private suspend fun coroutine(coroutineScope: CoroutineScope) {
    KLog.t("coroutine")
    val doWork1 = coroutineScope.async { doWork1("111") }
    val doWork11 = coroutineScope.async { doWork1("111111") }
    KLog.t("wait")
    val doWork2 = doWork2(doWork1.await() + doWork11.await())
    KLog.t(doWork2)

}

private suspend fun doWork1(s: String): String {
    KLog.t("doWork1 start:$s")
    delay(1000)
    KLog.t("doWork1 over:$s")
    return "work1 result $s"
}

private suspend fun doWork2(s: String): String {
    KLog.t("doWork2 start:$s")
    delay(1000)
    KLog.t("doWork2 over:$s")
    return "work1 result $s"
}