package com.example.androidkotlindemo.mvvm

/**
 * Created by zxf on 2021/3/22
 */
class State<T> private constructor(val state: Int, val data: T?, val err: Exception?) {


    companion object {

        const val SUCCESS = 0
        const val ERROR = 1

        fun <T> success(t: T): State<T> {
            return State(SUCCESS, t, null)
        }

        fun <T> error(e: Exception): State<T> {
            return State(ERROR, null, e)
        }
    }


}