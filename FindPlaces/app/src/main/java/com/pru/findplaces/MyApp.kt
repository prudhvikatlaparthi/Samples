package com.pru.findplaces

import android.app.Application
import com.google.android.libraries.places.api.Places

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(this.applicationContext, "AIzaSyCZlhaKwKjd66AdwCFE7AASDxG0R2Iv0iY")
        }
    }
}