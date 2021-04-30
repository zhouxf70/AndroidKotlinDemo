package com.example.androidkotlindemo.test.looper

import android.app.IntentService
import android.os.Handler
import android.os.HandlerThread

/**
 * Created by zxf on 2021/3/22
 */
//class MyHandlerThread : Thread("handler thread") {
//
//    override fun run() {
//        Looper.prepare()
//        val myLooper = Looper.myLooper()
//        handler.post {
//
//        }
//        super.run()
//    }
//}
//@SuppressLint("HandlerLeak")
//val handler = object : Handler() {
//    override fun handleMessage(msg: Message) {
//        super.handleMessage(msg)
//    }
//}

fun main() {

//    IntentService()
//    HandlerThread()
    Handler().removeMessages(1)

    println(Thread.currentThread())

    val thread = Thread {
        println(Thread.currentThread())
    }
//    thread.run()
    thread.start()


//    Observer
}