package com.pru.backgroundservice.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.pru.backgroundservice.MainActivity
import com.pru.backgroundservice.R
import com.pru.backgroundservice.utils.Constants.ACTION_PAUSE_SERVICE
import com.pru.backgroundservice.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.pru.backgroundservice.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.pru.backgroundservice.utils.Constants.ACTION_STOP_SERVICE
import com.pru.backgroundservice.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.pru.backgroundservice.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.pru.backgroundservice.utils.Constants.NOTIFICATION_ID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class SyncService : LifecycleService() {
    private var job: Job? = null
    var isFirstRun = true
    var isServiceRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    isServiceRunning = true
                    if (isFirstRun) {
                        Timber.d("Timber started service...")
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Timber Resuming service...")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Timber Paused service")
                }
                ACTION_STOP_SERVICE -> {
                    isServiceRunning = false
                    Timber.d("Timber Stopped service")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startSync() {
        job = lifecycleScope.launch {
            printLog()
        }
    }

    private suspend fun printLog() {
        Timber.d("Timber print log")
        delay(2000)
        printLog()
    }

    private fun killService() {
        isFirstRun = true
        job?.cancel()
        stopForeground(true)
        stopSelf()
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_sync)
            .setContentTitle("Sync is running")
            .setContentText("In progress")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        startSync()
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Timber Service destroy")
    }
}