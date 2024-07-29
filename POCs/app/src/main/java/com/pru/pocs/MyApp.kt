package com.pru.pocs

import android.app.Application
import android.util.Log

class MyApp: Application() {
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                Log.i("Prudhvi Log", "onTaskRemoved: ")
                // Handle low memory situation
                // Perform necessary cleanup or save state
            }
            // Other memory levels can be handled as needed
            TRIM_MEMORY_BACKGROUND -> {
                Log.i("Prudhvi Log", "onTaskRemoved: ")
            }

            TRIM_MEMORY_COMPLETE -> {
                Log.i("Prudhvi Log", "onTaskRemoved: ")
            }

            TRIM_MEMORY_MODERATE -> {
                Log.i("Prudhvi Log", "onTaskRemoved: ")
            }

            TRIM_MEMORY_RUNNING_LOW -> {
                Log.i("Prudhvi Log", "onTaskRemoved: ")
            }

            TRIM_MEMORY_RUNNING_MODERATE -> {
                Log.i("Prudhvi Log", "onTaskRemoved: ")
            }

            TRIM_MEMORY_UI_HIDDEN -> {
                Log.i("Prudhvi Log", "onTaskRemoved: ")
            }
        }
    }

}