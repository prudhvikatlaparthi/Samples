package com.pru.shopping.shared

import android.content.ContentValues.TAG
import android.util.Log

actual open class MyLogger {
    actual fun debugLogger(logMessage : String) {
        Log.d(TAG, "HttpLog: $logMessage")
    }
}