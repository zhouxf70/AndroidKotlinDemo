
package com.example.androidkotlindemo.test.promise


/**
 * Created by zxf on 2021/3/24
 */
//fun main() {
////    sync()
//    async()
//}

private fun sync() {
    Promise { handler ->
//        handler.resolve("10")
        handler.reject("error")
    }.then {
        plThread(it)
        Promise { handler ->
            handler.resolve("$it::20")
//            handler.reject("$it::error")
        }
    }.then {
        plThread(it)
        Promise { handler ->
            handler.resolve("$it::30")
//            handler.reject("$it::error")
        }
    }.then {
        plThread(it)
    }.error {
        plThread(it)
    }

}

private fun async() {
    Promise { handler ->
        Thread {
            Thread.sleep(1000)
            handler.resolve("a10")
//            handler.reject("error")
        }.start()
    }.then {
        plThread(it)
        Promise { handler ->
            Thread {
                Thread.sleep(1000)
                handler.resolve("$it::20")
//                handler.reject("$it::error")
            }.start()
        }
    }.then {
        plThread(it)
        Promise { handler ->
            Thread {
                Thread.sleep(1000)
//                handler.resolve("$it::30")
                handler.reject("$it::error")
            }.start()
        }
    }.then {
        plThread(it)
    }.error {
        plThread(it)
    }

}

fun plThread(msg: Any? = "hah") {
    println("[${Thread.currentThread().name}]:: $msg")
}
