package com.example.androidkotlindemo.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.androidkotlindemo.common.KLog
import java.io.*
import kotlin.system.measureTimeMillis

/**
 * Created by zxf on 2021/4/30
 */
@RequiresApi(Build.VERSION_CODES.N)
object ImageUtils {

    fun toByteArr(image: Image): ByteArray {
        val buffer = image.planes[0].buffer
        val byteArr = ByteArray(buffer.remaining()).also { KLog.d(it.size) }
        buffer.get(byteArr)
        image.close()
        return byteArr
    }

    fun toPicture(image: Image, file: File) {
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(file).apply {
                write(toByteArr(image))
            }
        } catch (e: IOException) {
            KLog.e(e.toString())
        } finally {
            close(output)
        }
    }

    fun toGif(images: ArrayList<ByteArray>, file: File) {
        val bitmaps = ArrayList<Bitmap>(images.size)
        val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.RGB_565
            inSampleSize = 2
        }
        val matrix = Matrix().apply { setRotate(90f) }
        images.forEach { byteArr ->
            BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size, options).run {
//                KLog.t("$width x $height")
                Bitmap.createBitmap(this, 0, 0, width, height, matrix, true).also {
//                    KLog.t("${it.width} x ${it.height}")
                    bitmaps.add(it)
                }
                recycle()
            }
        }
        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(file)
            output.write(generateGIF(bitmaps))
            output.close()
        } catch (e: Exception) {
            KLog.e(e)
        } finally {
            close(output)
        }
    }

    private fun generateGIF(bitmaps: ArrayList<Bitmap>): ByteArray? {
        val bos = ByteArrayOutputStream()
        val encoder = AnimatedGifEncoder()
        encoder.setDelay(100)
        encoder.start(bos)
        for (bitmap in bitmaps) {
            measureTimeMillis { encoder.addFrame(bitmap) }.also {
                KLog.d("time:$it")
                bitmap.recycle()
            }
        }
        encoder.finish()
        return bos.toByteArray()
    }

    private fun close(closeable: Closeable?) {
        closeable?.let {
            try {
                it.close()
            } catch (e: IOException) {
                KLog.e(e.toString())
            }
        }
    }

}