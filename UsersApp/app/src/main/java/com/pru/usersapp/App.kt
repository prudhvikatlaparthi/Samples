package com.pru.usersapp

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        val preference: Preference by lazy {
            Preference(
                sharedPref = app.getSharedPreferences(
                    "settings-pref",
                    MODE_PRIVATE
                )
            )
        }
        lateinit var app: App

        fun getContext(): Context {
            return app.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}