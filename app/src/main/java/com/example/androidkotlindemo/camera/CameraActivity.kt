package com.example.androidkotlindemo.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.androidkotlindemo.R
import com.example.androidkotlindemo.camera.flv.FlvPackage
import com.example.androidkotlindemo.common.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer

@RequiresApi(Build.VERSION_CODES.N)
class CameraActivity : AppCompatActivity(), View.OnClickListener {

    private var state = CaptureState.PREVIEW
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null
    private lateinit var mTextureView: TextureView
    private var mImageReader: ImageReader? = null
    private var mCameraDevice: CameraDevice? = null
    private var mCameraSession: CameraCaptureSession? = null
    private var mMediaEncoder: MediaCodecWrap? = null
    private var mMediaDecoder: MediaCodecWrap? = null
    private var mFlvPackage: FlvPackage? = null
    private lateinit var mPreviewRequestBuilder: CaptureRequest.Builder
    private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureStarted(session: CameraCaptureSession, request: CaptureRequest, timestamp: Long, frameNumber: Long) = Unit
        override fun onCaptureProgressed(session: CameraCaptureSession, request: CaptureRequest, partialResult: CaptureResult) = Unit
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
//            KLog.t("onCaptureCompleted $request")
//            nextGifCapture()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera)

        requestPermission()
        mTextureView = findViewById(R.id.texture)
        findViewById<Button>(R.id.bt_photo).setOnClickListener(this)
        findViewById<Button>(R.id.bt_gif).setOnClickListener(this)
        findViewById<Button>(R.id.bt_push).setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (mTextureView.isAvailable) initCamera()
        else
            mTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) = Unit
                override fun onSurfaceTextureDestroyed(texture: SurfaceTexture) = true
                override fun onSurfaceTextureUpdated(texture: SurfaceTexture) = Unit
                override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) = initCamera()
            }
    }

    override fun onPause() {
        KLog.d("onPause")
        imgJob?.run {
            if (isActive) cancel()
        }
        releaseCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun initCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) return
        val cameraManager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = "" + CameraCharacteristics.LENS_FACING_FRONT
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                KLog.t(camera)
                mCameraDevice = camera
                initSession()
            }

            override fun onDisconnected(camera: CameraDevice) = KLog.d("onDisconnected")
            override fun onError(camera: CameraDevice, error: Int) = KLog.e(error)
        }, mBackgroundHandler)
    }

    private fun initSession() {
        // preview surface
        val texture = mTextureView.surfaceTexture
        texture.setDefaultBufferSize(1920, 1080)
        val surface = Surface(texture)

        // photo surface
        mImageReader = ImageReader.newInstance(1920, 1080, ImageFormat.YUV_420_888, 2)
        mImageReader!!.setOnImageAvailableListener({ processImage(it) }, mBackgroundHandler)

        // h.265 encode surface
        mMediaEncoder = MediaCodecWrap(MediaCodecWrap.AVC, true)
        val encoderSurface = mMediaEncoder!!.getEncoderSurface(1920, 1080)
        val file = File(application.getExternalFilesDir(null), "my_flv.flv")
        mFlvPackage = FlvPackage().also { it.start(file) }
        mMediaEncoder!!.start(object : MediaCodecWrap.OnOutputBufferAvailableListener {
            override fun outputBufferAvailable(output: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
                KLog.d("onEncodedDataAvailable ${output.remaining()}")
                mFlvPackage?.writeVideoFrame(output, bufferInfo)
            }
        })
//        MediaRecorder

        mCameraDevice?.apply {
            createCaptureSession(arrayListOf(surface, mImageReader!!.surface, encoderSurface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) = Unit
                override fun onActive(session: CameraCaptureSession) = KLog.d("onActive")
                override fun onClosed(session: CameraCaptureSession) = KLog.d("onClosed")
                override fun onReady(session: CameraCaptureSession) = KLog.d("onReady")
                override fun onSurfacePrepared(session: CameraCaptureSession, surface: Surface) = KLog.d("onSurfacePrepared")
                override fun onCaptureQueueEmpty(session: CameraCaptureSession) = KLog.d("onCaptureQueueEmpty")
                override fun onConfigured(session: CameraCaptureSession) {
                    mCameraSession = session
                    mPreviewRequestBuilder = createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                        set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                        addTarget(surface)
                        addTarget(mImageReader!!.surface)
                        addTarget(encoderSurface)
                    }
                    state = CaptureState.PREVIEW
                    mCameraSession?.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler)
                }
            }, mBackgroundHandler)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_photo -> state = CaptureState.PHOTO
            R.id.bt_gif -> state = CaptureState.GIF
            R.id.bt_push -> Unit
        }
    }

    private var imgJob: Job? = null
    private var lastCaptureTime = 0L
    private val gif = ArrayList<ByteArray>(GIF_PICTURE_SIZE)
    private fun processImage(reader: ImageReader) {
//        KLog.d("processImage")
        val image = reader.acquireLatestImage()
        when (state) {
            CaptureState.PREVIEW -> Unit
            CaptureState.PHOTO -> {
                state = CaptureState.PREVIEW
                val nv21 = ImageUtils.getDataFromYUV(image)
                imgJob = GlobalScope.launch(Dispatchers.IO) {
                    val file = File(application.getExternalFilesDir(null), "pic.jpg").also { KLog.d(it.absolutePath) }
                    ImageUtils.toPicture(nv21, file)
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, "save picture:${file.absolutePath}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            CaptureState.GIF -> {
                if (System.currentTimeMillis() - lastCaptureTime > 100) {
                    lastCaptureTime = System.currentTimeMillis()
                    gif.add(ImageUtils.getDataFromYUV(image))
                }
                image.close()
                if (gif.size == GIF_PICTURE_SIZE) {
                    state = CaptureState.PREVIEW
                    imgJob = GlobalScope.launch(Dispatchers.IO) {
                        val file = File(application.getExternalFilesDir(null), "my_gif.gif").also { KLog.d(it.absolutePath) }
                        ImageUtils.toGif(gif, file)
                        gif.clear()
                        runOnUiThread {
                            Toast.makeText(this@CameraActivity, "save gif:${file.absolutePath}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        image.close()
    }

    private fun getInputEncodedDataHandler(surface: Surface? = null): MediaCodecWrap.InputDataHandler {
        mMediaDecoder = MediaCodecWrap(MediaCodecWrap.AVC, false)
        val inputHandler = mMediaDecoder!!.setDecoderSurface(1920, 1080, surface)
        mMediaDecoder!!.start(object : MediaCodecWrap.OnOutputBufferAvailableListener {
            override fun outputBufferAvailable(output: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
                val size = output.remaining()
                KLog.d("onDecodedDataAvailable $size")
                val file = File(application.getExternalFilesDir(null), "pic_${System.currentTimeMillis()}.jpg").also { KLog.d(it.absolutePath) }
                val byteArray = ByteArray(size)
                output.get(byteArray)
                ImageUtils.toPicture(byteArray, file)
            }
        })
        return inputHandler
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("camera")
        mBackgroundThread?.let {
            it.start()
            mBackgroundHandler = Handler(it.looper)
        }
    }

    private fun stopBackgroundThread() {
        mBackgroundThread?.run {
            quit()
            try {
                mBackgroundThread?.join()
                mBackgroundThread = null
                mBackgroundHandler = null
            } catch (e: InterruptedException) {
                KLog.e(e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        KLog.d("onDestroy")
    }

    private fun requestPermission() {
        val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
        )
        val list = ArrayList<String>()
        for (i in permissions.indices) {
            if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED)
                list.add(permissions[i])
            else
                KLog.d("has permission: ${permissions[i]}")
        }
        if (list.size > 0)
            requestPermissions(list.toArray(emptyArray()), 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in permissions.indices) {
            KLog.d("request ${permissions[i]} ,result: ${grantResults[i] == PackageManager.PERMISSION_GRANTED}")
        }
    }

    private fun releaseCamera() {
        mCameraDevice?.close()
        mCameraDevice = null
        mCameraSession?.close()
        mCameraSession = null
        mImageReader?.close()
        mImageReader = null
        mMediaEncoder?.stop()
        mMediaEncoder = null
        mMediaDecoder?.stop()
        mMediaDecoder = null
        mFlvPackage?.stop()
        mFlvPackage = null
        imgJob?.run {
            if (isActive) cancel()
        }
    }

    companion object {
        const val GIF_PICTURE_SIZE = 10
    }

}