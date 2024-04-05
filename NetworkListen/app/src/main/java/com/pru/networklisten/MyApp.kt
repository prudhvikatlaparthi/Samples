package com.pru.networklisten

import android.app.Application
import android.content.Context

class MyApp : Application() {
    companion object {
        private var application: MyApp? = null

        fun getContext(): Context {
            return application!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}