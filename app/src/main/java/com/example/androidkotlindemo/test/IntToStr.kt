package com.example.androidkotlindemo.test

/**
 * Created by zxf on 2021/4/16
 */
fun main() {

    println(intToStr(12258))
    println(intToStr(0))
    println(intToStr(100000))

}

fun intToStr(int: Int): Int {

    if (int < 10) {
        return 1
    }

    if (int in 10..25) {
        return 2
    }

    if (int in 26..99) {
        return 1
    }

    return if (int % 100 in 10..25) {
        (intToStr(int / 10) + intToStr(int / 100)).also {
//            println("$int:1:$it")
        }
    } else {
        (intToStr(int / 10)).also {
//            println("$int:2:$it")
        }
    }

}