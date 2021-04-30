package com.example.androidkotlindemo.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.androidkotlindemo.R
import com.example.androidkotlindemo.common.KLog
import kotlinx.coroutines.*
import java.io.File

@RequiresApi(Build.VERSION_CODES.N)
class CameraActivity : AppCompatActivity(), View.OnClickListener {

    private var state = CaptureState.PREVIEW
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null
    private lateinit var mTextureView: TextureView
    private var mImageReader: ImageReader? = null
    private var mCameraDevice: CameraDevice? = null
    private var mSensorOrientation = 0
    private var mCameraSession: CameraCaptureSession? = null
    private var mPreviewRequest: CaptureRequest? = null

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
        if (mTextureView.isAvailable) {
            initCamera()
        } else {
            mTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
                    KLog.d("$width x $height")
                    initCamera()
                }

                override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) = Unit
                override fun onSurfaceTextureDestroyed(texture: SurfaceTexture) = true
                override fun onSurfaceTextureUpdated(texture: SurfaceTexture) = Unit
            }
        }
    }

    override fun onPause() {
        releaseCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun initCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) return
        val cameraManager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = "" + CameraCharacteristics.LENS_FACING_FRONT
        mSensorOrientation = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
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
        mImageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 2)
        mImageReader!!.setOnImageAvailableListener({ processImage(it) }, mBackgroundHandler)

        mCameraDevice?.apply {
            createCaptureSession(arrayListOf(surface, mImageReader!!.surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) = KLog.t("onConfigureFailed")
                override fun onConfigured(session: CameraCaptureSession) {
                    mCameraSession = session
                    mPreviewRequest = createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).run {
                        addTarget(surface)
                        set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                        build()
                    }
                    takePreview()
                }
            }, mBackgroundHandler)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_photo -> takePhoto()
            R.id.bt_gif -> takeGif()
            R.id.bt_push -> Unit
        }
    }

    private fun takePreview() {
        state = CaptureState.PREVIEW
        mCameraDevice?.apply {
            mCameraSession?.setRepeatingRequest(mPreviewRequest!!, null, mBackgroundHandler)
        }
    }

    private fun takePhoto() {
        state = CaptureState.PHOTO
        mCameraDevice?.apply {
            val captureRequest = createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).run {
                addTarget(mImageReader!!.surface)
                set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                set(CaptureRequest.JPEG_ORIENTATION, mSensorOrientation % 360)
                build()
            }
            mCameraSession?.capture(captureRequest, null, mBackgroundHandler)
        }
    }

    private var gifJob: Job? = null
    private fun takeGif() {
        KLog.d("takeGif")
        state = CaptureState.GIF
        mCameraDevice?.apply {
            val captureRequest = createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).run {
                addTarget(mImageReader!!.surface)
                set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                build()
            }
            gifJob = GlobalScope.launch {
                mCameraSession?.stopRepeating()
                mCameraSession?.abortCaptures()
                repeat(GIF_PICTURE_SIZE) {
                    delay(100)
                    mCameraSession?.capture(captureRequest, null, mBackgroundHandler)
                }
            }
        }
    }

    private val gif = ArrayList<ByteArray>(GIF_PICTURE_SIZE)
    private fun processImage(reader: ImageReader) {
        KLog.t("processImage,state = $state")
        when (state) {
            CaptureState.PREVIEW -> Unit
            CaptureState.PHOTO -> {
                val file = File(application.getExternalFilesDir(null), "pic.jpg").also { KLog.d(it.absolutePath) }
                mBackgroundHandler?.post { ImageUtils.toPicture(reader.acquireNextImage(), file) }
                state = CaptureState.PREVIEW
            }
            CaptureState.GIF -> {
                gif.add(ImageUtils.toByteArr(reader.acquireNextImage()))
                if (gif.size == GIF_PICTURE_SIZE) {
                    val file = File(application.getExternalFilesDir(null), "my_gif.gif").also { KLog.d(it.absolutePath) }
                    gifJob = GlobalScope.launch(Dispatchers.IO) {
                        ImageUtils.toGif(gif, file)
                        gif.clear()
                        takePreview()
                    }
                }
            }
        }
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
            quitSafely()
            try {
                mBackgroundThread?.join()
                mBackgroundThread = null
                mBackgroundHandler = null
            } catch (e: InterruptedException) {
                KLog.e(e)
            }
        }
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

        gifJob?.run {
            if (isActive) cancel()
        }
    }

    companion object {
        const val GIF_PICTURE_SIZE = 30
    }

}