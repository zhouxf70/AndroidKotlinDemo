package com.example.androidkotlindemo.common

import com.example.androidkotlindemo.test.rx.plThread
import java.lang.RuntimeException


/**
 * Created by zxf on 2021/3/15
 */
fun main() {
    println(exceptionReturn())
}

fun exceptionReturn(): Int {

    var i = 0
    try {
        throw RuntimeException("hah")
    } catch (e: Exception) {
        println("catch")
        i = 1
        return i
    } finally {
        i = 2
        return i
    }

}

fun stringList() {
}

fun map() {
    val map: MutableMap<A, Any> = HashMap()
    val a1 = A()
    val a2 = A()
    map[a1] = "1"
    map[a2] = 2
    println(map.size)

    test(::test2)
    test(a1::hashCode)
    plThread(a1::name)
    (a1::equals)("")
}

//val o = object {
//
//}

fun test2(): Int {
    return 10
}

fun test(f: () -> Int): Int {
    return f()
}

internal class A {

    val name = "hah"

    override fun equals(obj: Any?): Boolean {
        println("判断equals")
        return true
    }

    override fun hashCode(): Int {
        println("判断hashcode")
        return 1
    }
}
