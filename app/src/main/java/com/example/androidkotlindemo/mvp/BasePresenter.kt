package com.example.androidkotlindemo.mvp

interface BasePresenter<V : BaseView> {

    fun attachView(v: V)

    fun detachView()

    fun cancelRequest()
}
