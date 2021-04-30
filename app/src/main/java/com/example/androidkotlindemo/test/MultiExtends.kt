package com.example.androidkotlindemo.test

/**
 * Created by zxf on 2021/4/22
 */
fun main() {
    val thread = Thread()
//    thread.interrupted()
    Thread.interrupted()
}

interface Parent {
    fun test() {
        println("test parent")
    }
}

abstract class Parent2 {

    open fun test(){
        println("test parent2")
    }

    abstract fun test2()
}

class Son : Parent, Parent2() {
    override fun test() {

        println("test son")
        super<Parent>.test()
    }

    override fun test2() {

        println("test2 parent")
    }

}