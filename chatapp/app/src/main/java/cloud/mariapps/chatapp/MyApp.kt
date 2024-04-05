package cloud.mariapps.chatapp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import dagger.hilt.android.HiltAndroidApp


private var appContext_: Context? = null
val appContext: Context
    get() = appContext_
        ?: throw IllegalStateException(
            "Application context not initialized yet."
        )
@HiltAndroidApp
class MyApp : Application(), CameraXConfig.Provider {
    override fun onCreate() {
        super.onCreate()
        appContext_ = applicationContext
    }

    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.ERROR).build()
    }
}