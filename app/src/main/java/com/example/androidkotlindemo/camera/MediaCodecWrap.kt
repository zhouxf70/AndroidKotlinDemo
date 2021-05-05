package com.example.androidkotlindemo.camera

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Build
import android.view.Surface
import androidx.annotation.RequiresApi
import com.example.androidkotlindemo.common.KLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

@RequiresApi(Build.VERSION_CODES.N)
class MediaCodecWrap(private val type: String, private val encoder: Boolean = true, private val fps: Int = 25) {

    private val mediaCodec: MediaCodec = getCodecByType(type, encoder)
    private var job: Job? = null
    private var state = State.PREPARE

    fun getEncoderSurface(width: Int, height: Int): Surface {
        val mediaFormat = MediaFormat.createVideoFormat(type, width, height).apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            setInteger(MediaFormat.KEY_BIT_RATE, 1200 * 1024) // 1200 kbps
            setInteger(MediaFormat.KEY_FRAME_RATE, fps)  //25
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)  //关键帧间隔时间 1s
        }
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        return mediaCodec.createInputSurface()
    }

    fun start(listener: OnOutputBufferAvailableListener) {
        mediaCodec.start()
        state = State.RUNNING
        job = GlobalScope.launch {
            while (true) {
                delay(10)
                if (state == State.STOPPING) {
                    realStop()
                    break
                }
                val bufferInfo = MediaCodec.BufferInfo()
                val index = mediaCodec.dequeueOutputBuffer(bufferInfo, 0)
//                KLog.d("encoder:${encoder}, index:$index")
                if (index >= 0) {
                    val outputBuffer = mediaCodec.getOutputBuffer(index)
                    if (outputBuffer != null)
                        listener.outputBufferAvailable(outputBuffer, bufferInfo)
                    mediaCodec.releaseOutputBuffer(index, false)
                }
            }
        }
    }

    fun setDecoderSurface(width: Int, height: Int, surface: Surface?): InputDataHandler {
        val mediaFormat = MediaFormat.createVideoFormat(type, width, height)
        mediaCodec.configure(mediaFormat, surface, null, 0)
        // 使用surface直接展示解码数据失败，dequeueInputBuffer获得的index一直是-1，原因未知
        if (surface != null) {
            mediaCodec.start()
            state = State.RUNNING
        }
        return object : InputDataHandler {
            override fun inputData(input: ByteBuffer) {
                job = GlobalScope.launch {
                    if (state == State.STOPPING) realStop()
                    val index = mediaCodec.dequeueInputBuffer(30)
                    KLog.d("index:$index")
                    if (index >= 0) {
                        job = GlobalScope.launch {
                            val inputBuffer = mediaCodec.getInputBuffer(index)
                            inputBuffer!!.put(input)
                            mediaCodec.queueInputBuffer(index, 0, inputBuffer.remaining(), System.nanoTime(), 0)
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        state = State.STOPPING
    }

    private fun realStop() {
        if (state == State.STOPPING) {
            state = State.STOPPED
            mediaCodec.stop()
            mediaCodec.release()
            job?.run {
                if (isActive) cancel().also { KLog.d("job is canceled") }
            }
        }
    }

    // configure 之后立马获取surface报错
    private fun getSurface(): Surface {
        var surface: Surface? = null
        var tryTimes = 0
        while (tryTimes < 10) {
            try {
                Thread.sleep(10)
                surface = mediaCodec.createInputSurface()
                tryTimes += 10
            } catch (e: Exception) {
                tryTimes++
                KLog.e("time:$tryTimes ,$e")
            }
        }
        return surface!!
    }

    private fun getCodecByType(type: String, encoder: Boolean): MediaCodec {
        val codecInfos = MediaCodecList(MediaCodecList.REGULAR_CODECS).codecInfos
        var codec: MediaCodec? = null
        for (codecInfo in codecInfos) {
            codecInfo.run {
                if (isEncoder == encoder)
                    supportedTypes.forEach {
                        if (it == type) {
                            codec = if (isEncoder) MediaCodec.createEncoderByType(type)
                            else MediaCodec.createDecoderByType(type)
                        }
                    }
            }
        }
        return codec ?: throw Exception("cant find type : $type")
    }

    public interface OnOutputBufferAvailableListener {
        fun outputBufferAvailable(output: ByteBuffer, bufferInfo: MediaCodec.BufferInfo)
    }

    public interface InputDataHandler {
        fun inputData(input: ByteBuffer)
    }

    enum class State {
        PREPARE, RUNNING, STOPPING, STOPPED
    }

    companion object {
        const val AVC = "video/avc"
        const val HEVC = "video/hevc"
    }
}