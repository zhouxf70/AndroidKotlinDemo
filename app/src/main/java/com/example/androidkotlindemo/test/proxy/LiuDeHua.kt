package com.example.androidkotlindemo.test.proxy

/**
 * Created by zxf on 2021/3/22
 */
class LiuDeHua : Star {
    override fun sing(name: String): String {
        println("给我一杯忘情水")
        return "唱完$name"
    }

    override fun dance(name: String): String {
        println("dance")
        return "跳完$name"
    }
}