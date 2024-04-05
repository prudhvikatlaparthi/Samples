package com.pru.bgserviceapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

private var appContext_: Context? = null
val appContext: Context
    get() = appContext_ ?: throw IllegalStateException(
        "Application context not initialized yet."
    )

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext_ = applicationContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "dataSync", "Data Sync", NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}