package com.example.androidkotlindemo.login

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import com.example.androidkotlindemo.R
import com.example.androidkotlindemo.camera.CameraActivity
import com.example.androidkotlindemo.common.KLog
import com.example.androidkotlindemo.mvp.MVPBaseActivity
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

    }

    override fun loginResult(loginResult: LoginResult) {
        KLog.d(loginResult)
//        val intent = Intent(this, MainActivity::class.java)
//        val intent = Intent(this, ViewPagerActivity::class.java)
        val intent = Intent(this, CameraActivity::class.java)
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