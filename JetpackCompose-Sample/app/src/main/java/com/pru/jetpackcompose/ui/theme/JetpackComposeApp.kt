package com.pru.jetpackcompose.ui.theme

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class JetpackComposeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(androidContext = this@JetpackComposeApp)
            modules(AppModule.appModule)
        }
    }
}