package com.example.androidkotlindemo.common

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Created by zxf on 2021/6/2
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class JsonParser private constructor(private val src: String) {

    private val ls = System.lineSeparator()
    private val len = src.length
    private var pos = 0

    companion object {
        fun <T> parseObject(src: String, cls: Class<T>): T = JsonParser(src).parseObject(cls)
    }

    private fun <T> parseObject(cls: Class<T>): T {
        if (src.isEmpty() || src[pos++] != '{' || src[len - 1] != '}') throw Exception("error src : { }")
        val obj: T = cls.getConstructor().newInstance()
        while (pos < src.length) {
            ignoreBlank()
            if (src[pos] == '"') {
                val key = findKey()
                try {
                    val field = cls.getDeclaredField(key)
                    val value: Any = when (src[pos]) {
                        '"' -> findStringValue()
                        '{' -> findObjectValue()
                        '[' -> findArrayValue()
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> findNumberValue()
                        't', 'f' -> findBooleanValue()
                        else -> throw Exception("error src : value start = ${src[pos]}")
                    }
                    field.isAccessible = true
                    field.set(obj, value)
                } catch (e: NoSuchFieldException) {
                    ignoreValue()
                }
            } else throw  Exception("error src : key start = ${src[pos]}")
        }
        return obj
    }

    private fun findBooleanValue(): Boolean {
        return true
    }

    private fun findNumberValue(): Number {
        return 0
    }

    private fun findArrayValue(): Any {
        return Any()
    }

    private fun findObjectValue(): Any {
        return Any()
    }

    private fun findStringValue(): String {
        val start = ++pos
        findChar('"')
        val value = src.substring(start, pos)
        pos++
        findComma()
        return value
    }

    private fun findComma() {
        findChar(',', true)
        pos++
    }

    private fun ignoreBlank() {
        while (src[pos] == ' ' || src[pos] == ls[0]) {
            if (src[pos] == ls[0]) {
                pos++
            } else {
                for (i in 1 until ls.length) {
                    if (src[pos + i] != ls[i]) throw Exception("error line separator")
                }
                pos += ls.length
            }
        }
    }

    private fun findKey(): String {
        val start = ++pos
        findChar('"')
        val key = src.substring(start, pos)
        pos++
        ignoreBlank()
        if (src[pos] != ':') throw Exception("can't find : ,${src[pos]}")
        pos++
        ignoreBlank()
        return key
    }

    private fun findChar(des: Char, canNotExist: Boolean = false) {
        while (src[pos] != des) {
            pos++
            if (pos == len) {
                if (canNotExist) break
                else throw Exception("can't find char : $des")
            }
        }
    }

    private fun ignoreValue() {

    }

}
