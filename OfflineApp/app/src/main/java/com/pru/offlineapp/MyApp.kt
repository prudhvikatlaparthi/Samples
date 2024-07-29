package com.pru.offlineapp

import android.app.Application
import android.content.Context
import com.pru.offlineapp.worker.startUploadWorker

class MyApp : Application() {
    companion object {
        private var _application: MyApp? = null
        val context: Context
            get() = _application ?: throw IllegalStateException(
                "Application context not initialized yet."
            )
    }

    override fun onCreate() {
        super.onCreate()
        _application = this
        startUploadWorker()
    }
}