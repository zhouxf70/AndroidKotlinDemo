package com.example.androidkotlindemo.login

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.MotionEvent
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.androidkotlindemo.R
import com.example.androidkotlindemo.animator.AnimatorActivity
import com.example.androidkotlindemo.common.JsonParser
import com.example.androidkotlindemo.common.KLog
import com.example.androidkotlindemo.database.RoomActivity
import com.example.androidkotlindemo.mvp.MVPBaseActivity
import com.example.androidkotlindemo.network.bean.Person
import com.example.androidkotlindemo.viewpage.ViewPagerActivity
import com.practice.aidl.server.IMyAidlInterface

/**
 * Created by zxf on 2021/3/11
 */
class LoginActivity : LoginContract.View,
    MVPBaseActivity<LoginContract.View, LoginPresenterImpl>(LoginPresenterImpl::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.username)
        val etPwd = findViewById<EditText>(R.id.password)
        findViewById<Button>(R.id.login).setOnClickListener {
            presenter?.login(etUsername?.text.toString(), etPwd?.text.toString())
        }


        findViewById<Button>(R.id.bt_bind_service).setOnClickListener {
            val intent = Intent("com.practice.aidl.server.IMyAidlInterface")
            intent.setPackage("com.practice.aidl")
            bindService(intent, conn, Context.BIND_AUTO_CREATE)
        }

        KLog.d("is main thread :${Looper.myLooper() == Looper.getMainLooper()}")

        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                KLog.d("Login : $event")
            }
        })

//        WebView(this).addJavascriptInterface()
    }

    private fun testParser() {
        val src = "{\"name\":\"10\",\"like\":\"dance\"}"
        KLog.d(src.length)
        val per = JsonParser.parseObject(src, Person::class.java)
        KLog.d(per)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        KLog.d("onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        KLog.d("onRestoreInstanceState")
    }


    override fun loginResult(loginResult: LoginResult) {
        KLog.d(loginResult)
//        val intent = Intent(this, MainActivity::class.java)
        val intent = Intent(this, ViewPagerActivity::class.java)
//        val intent = Intent(this, CameraActivity::class.java)
//        val intent = Intent(this, RoomActivity::class.java)
//        val intent = Intent(this, AnimatorActivity::class.java)
        startActivity(intent)
    }

    private val conn = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            KLog.d("onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            KLog.d("onServiceConnected")
            val binder = IMyAidlInterface.Stub.asInterface(service)
            binder.test()
            binder.basicTypes(1, 1L, false, 1.0f, 1.0, "hah")
        }

    }

}