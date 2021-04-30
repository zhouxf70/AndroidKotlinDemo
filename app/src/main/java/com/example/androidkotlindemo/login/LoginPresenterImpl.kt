package com.example.androidkotlindemo.login

import com.example.androidkotlindemo.mvp.BasePresenterImpl
import com.example.androidkotlindemo.mvvm.Repository

/**
 * Created by zxf on 2021/3/11
 */
class LoginPresenterImpl : LoginContract.Presenter, BasePresenterImpl<LoginContract.View>() {

//    val repository = Repository

    override fun login(name: String, pwd: String) {
        view?.loginResult(LoginResult(name + "___" + pwd))
    }


}