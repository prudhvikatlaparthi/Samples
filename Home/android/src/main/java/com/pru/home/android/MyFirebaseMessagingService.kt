package com.pru.home.android

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FireBase"
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i(TAG, "onNewToken: $p0")
    }
}