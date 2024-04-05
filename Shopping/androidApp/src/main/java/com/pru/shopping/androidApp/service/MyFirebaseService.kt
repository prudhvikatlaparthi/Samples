package com.pru.shopping.androidApp.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseService : FirebaseMessagingService() {
    private val TAG = "FireBase"
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i(TAG, "onNewToken: $p0")
    }
}