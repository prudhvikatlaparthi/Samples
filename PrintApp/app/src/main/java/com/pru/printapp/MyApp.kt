package com.pru.printapp

import android.app.Application
import com.pru.printlib.PrintLib

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PrintLib.init(this)
    }
}