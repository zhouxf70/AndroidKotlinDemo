package com.example.androidkotlindemo.mvp

open class BasePresenterImpl<V : BaseView> : BasePresenter<V> {

    var view: V? = null

//    val httpUtils = null

    override fun attachView(v: V) {
        view = v
    }

    override fun detachView() {
        cancelRequest()
        view = null
    }

    override fun cancelRequest() {
        println("cancelRequest")
    }

}
