package com.pru.pocs

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        // Unregister listeners
        // Stop notifications
        // Other cleanup tasks
        Log.i("Prudhvi Log", "onTaskRemoved: ")
        stopSelf() // Stop the service
    }
}
