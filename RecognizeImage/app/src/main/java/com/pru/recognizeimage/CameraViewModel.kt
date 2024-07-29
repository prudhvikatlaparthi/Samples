package com.pru.recognizeimage

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.pru.recognizeimage.Global.dpToPx
import java.io.File

class CameraViewModel : ViewModel() {

    private val imageCapture = ImageCapture.Builder()
        .setTargetResolution(Size(dpToPx(320), dpToPx(200)))
        .build()
    private val imageAnalyzer = ImageAnalysis.Builder()
        .build()
    private val preview = Preview.Builder()
        .build()

    var resultPath: String? = null

    fun startCamera(surfaceProvider: Preview.SurfaceProvider, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(appContext)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview.setSurfaceProvider(surfaceProvider)


            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(appContext))
    }

    fun takePhoto(result : (Boolean, String) -> Unit) {
        val photoFile = File(
            Global.getOutputDirectory(), Global.getFileName() + ".png"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(appContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
//                    exc.printStackTrace()
                    result.invoke(false,exc.message ?: "")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    resultPath = photoFile.path
                    result.invoke(true, "")
                }
            })
    }
}