package com.example.androidkotlindemo.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import okio.BufferedSource
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * Created by zxf on 2021/3/14
 */
class LoggerInterceptor : Interceptor {

    private val tag = "Network-mvp"

    private val utf_8 = Charset.forName("UTF-8")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        return logResponse(response)
    }

    private fun logResponse(response: Response): Response {
        val request = response.request()
        Log.d(tag, "--> url:${request.url()}")

        request.body()?.apply {
            val buffer = Buffer()
            writeTo(buffer)
            Log.d(tag, "--> request:${buffer.clone().readString(utf_8)}")
        }
        response.body()?.apply {
            val source: BufferedSource = source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            logJson(source.buffer.clone().readString(utf_8))
        }
        return response
    }

    private val separator: String = System.getProperty("line.separator") ?: "\n"

    private fun logJson(msg: String) {
        var json = ""
        try {
            json = when {
                msg.startsWith("{") -> JSONObject(msg).toString(4)
                msg.startsWith("[") -> JSONArray(msg).toString(4)
                else -> "Empty or Not json content"
            }
        } catch (e: Exception) {
            Log.e(tag, e.toString())
        }

        val prefix =
            "--> response$separator╔════════════════════════════════════════════════════════════════════════$separator║ "
        val postfix =
            "$separator╚════════════════════════════════════════════════════════════════════════"
        json = json.split(separator).joinToString("$separator║ ", prefix, postfix)

        json.apply {
            if (length > 3200) {
                val chunkCount = length / 3200
                for (i in 0..chunkCount) {
                    val max = 3200 * (i + 1)
                    if (max >= length) Log.d(tag, substring(3200 * i))
                    else Log.d(tag, substring(3200 * i, max))
                }
            } else {
                Log.d(tag, json)
            }
        }
    }


}