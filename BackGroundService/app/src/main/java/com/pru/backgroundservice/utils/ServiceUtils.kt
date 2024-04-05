package com.pru.backgroundservice.utils

import android.content.Context
import android.content.Intent
import com.pru.backgroundservice.service.SyncService

object ServiceUtils {
    fun Context.sendCommandToService(action: String) =
        Intent(this, SyncService::class.java).also {
            it.action = action
            startService(it)
        }
}