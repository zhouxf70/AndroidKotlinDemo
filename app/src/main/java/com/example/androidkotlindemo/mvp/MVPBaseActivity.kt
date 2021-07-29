package com.example.androidkotlindemo.mvp

import android.app.Dialog
import android.os.Bundle
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.androidkotlindemo.common.KLog

abstract class MVPBaseActivity<V : BaseView, P : BasePresenter<V>>(private val clazz: Class<P>) :
    AppCompatActivity() {

    open var presenter: P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMVP()
    }

    @Suppress("UNCHECKED_CAST")
    private fun initMVP() {
        try {
            presenter = clazz.newInstance()
            presenter?.attachView(this as V)
        } catch (e: Exception) {
            KLog.d(e)
        }
        KLog.d(presenter)
    }


    override fun onDestroy() {
        KLog.d("onDestroy")
        presenter?.detachView()
        super.onDestroy()
    }

}
