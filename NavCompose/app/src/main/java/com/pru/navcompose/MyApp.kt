package com.pru.navcompose

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

private var appContext_: Context? = null
val appContext: Context
    get() = appContext_
        ?: throw IllegalStateException(
            "Application context not initialized yet."
        )

private var appController_: AppController? = null
val appController: AppController
    get() = appController_ ?: throw IllegalStateException(
        "App Controller not initialized yet."
    )
@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext_ = applicationContext
        appController_ = AppController()
    }
}