package com.pru.languageapp

import android.app.Application
import android.content.Context

private var _appContext: Context? = null
val appContext: Context
    get() = _appContext ?: throw IllegalArgumentException("app context is not initialized")

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        _appContext = this
    }

}