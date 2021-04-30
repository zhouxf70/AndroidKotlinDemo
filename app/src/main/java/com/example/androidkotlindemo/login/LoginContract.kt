package com.example.androidkotlindemo.login

import com.example.androidkotlindemo.mvp.BasePresenter
import com.example.androidkotlindemo.mvp.BaseView

/**
 * Created by zxf on 2021/3/11
 */
class LoginContract {
    interface View : BaseView {
        fun loginResult(loginResult: LoginResult)
    }

    interface Presenter : BasePresenter<View> {
        fun login(name: String, pwd: String)
    }
}