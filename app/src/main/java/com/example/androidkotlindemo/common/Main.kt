package com.example.androidkotlindemo.common

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.androidkotlindemo.network.bean.Person
import com.example.androidkotlindemo.test.rx.plThread


/**
 * Created by zxf on 2021/3/15
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
fun main() {
}

fun testParser() {
    val src = "{\"name\":\"10\",\"like\":\"dance\"}"
    println(src.length)
    val per = JsonParser.parseObject(src, Person::class.java)
    print(per)
}

fun exceptionReturn2(): ReturnTest {

    val i = ReturnTest(0)
    try {
        throw RuntimeException("hah")
    } catch (e: Exception) {
        println("catch")
        i.int = 1
        return i
    } finally {
        i.int = 2
//        return returnT(i)
    }
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
        println(i)
//        return returnT(i)
    }
}

data class ReturnTest(var int: Int)

private fun returnT(int: Int): ReturnTest {
    println("returnT:$int")
    return ReturnTest(int)
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
