package com.example.androidkotlindemo.test.rx

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by zxf on 2021/3/23
 */
fun main() {


//    test1()
    test2()

    Thread.sleep(10000)
}

fun test3() {
    val defer = Observable.defer<String> {
        ObservableSource {
            it.onNext("hah")
        }
    }.map {
        it.length
    }.subscribe {
        println(it)
    }
}

fun test2() {
    val dis = Observable.fromArray(1, 2, 3)
        .observeOn(Schedulers.io())
        .flatMap<String> {

            Observable.just("$it:network:1", "$it:network:2", "$it:network:3")
        }
//        .concatMap<String> {
//            Observable.just("$it:network:1", "$it:network:2", "$it:network:3")
//                .delay(1, TimeUnit.SECONDS)
//        }
        .observeOn(Schedulers.newThread())
//        .subscribeOn(Schedulers.newThread())
        .subscribe {
            plThread(it)
        }
}

fun test1() {
    val observable = Observable.create<String> {
        plThread("ccc")
        it.onNext("first")
        it.onNext("second")
        it.onNext("third")
        it.onComplete()
    }
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.newThread())
        .filter {
            it.length > 3
        }.flatMap<String> {
            ObservableSource { observer ->
                observer.onNext("$it,flat")
            }
        }.map {
            it
        }

//    val sleep = Observable.create<String>() {
//        plThread("int")
//        for (i in 0..5) {
//            it.onNext(i.toString())
//            Thread.sleep(1000)
//        }
//        it.onComplete()
//    }
//
//    val merge = Observable.merge(sleep, sleep)

    val observer = object : Observer<String> {

        override fun onSubscribe(d: Disposable) {
            plThread("onSubscribe:$d")
        }

        override fun onNext(t: String) {
            plThread("onNext:$t")
        }

        override fun onError(e: Throwable) {
            println("onError:$e")
        }

        override fun onComplete() {
            plThread("onComplete")
        }
    }

    observable.subscribe(observer)
//    merge.subscribe(observer)
}

fun plThread(msg: Any? = "hah") {
    println("[${Thread.currentThread().name}]:: $msg")
}