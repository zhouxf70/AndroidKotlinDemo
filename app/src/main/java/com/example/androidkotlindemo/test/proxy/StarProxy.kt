package com.example.androidkotlindemo.test.proxy

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by zxf on 2021/3/22
 */
class StarProxy(private val target: Star) : InvocationHandler {

    fun createStarObj(): Star {
        return Proxy.newProxyInstance(
            target.javaClass.classLoader,
            target.javaClass.interfaces,
            this
        ) as Star
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        println("收钱")
        return method?.invoke(target, *args.orEmpty())
    }
}

fun main() {
    val ldh = StarProxy(LiuDeHua()).createStarObj()

    val sing = ldh.sing("11")
    val dance = ldh.dance("22")

    println("$sing::$dance")
}