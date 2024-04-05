package com.pru.bgserviceapp.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.pru.bgserviceapp.R
import com.pru.bgserviceapp.data.db.entities.TestUserSync
import com.pru.bgserviceapp.domain.repository.AppRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class DataSyncService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Inject
    lateinit var appRepository: AppRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification =
            NotificationCompat.Builder(this, "dataSync").setContentTitle("Data Syncing...")
                .setContentText("Will Start").setSmallIcon(R.drawable.ic_sync).setOngoing(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        serviceScope.launch {
            dataSyncing(notification, notificationManager)
        }

        startForeground(1, notification.build())
    }

    private suspend fun dataSyncing(
        notification: NotificationCompat.Builder, notificationManager: NotificationManager
    ) {
        updateNotification("Downloading...", notification, notificationManager)
        val data = List((0..30).random()) {
            TestUserSync(name = "Item ${System.currentTimeMillis()}", isSynced = "Y")
        }
        appRepository.cacheUsers(data)
        updateNotification("Uploading...", notification, notificationManager)
        val unSynced = appRepository.getUnSyncedUsers()
        for (item in unSynced) {
            delay((0..3000).random().toLong())
            item.isSynced = "Y"
        }
        appRepository.cacheUsers(unSynced)
        updateNotification("Completed...", notification, notificationManager)
        delay(10 * 1000)
        dataSyncing(notification, notificationManager)
    }

    private fun updateNotification(
        message: String,
        notification: NotificationCompat.Builder,
        notificationManager: NotificationManager
    ) {
        val updatedNotification = notification.setContentText(message).build()
        notificationManager.notify(1, updatedNotification)
    }

    private fun stop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceScope.coroutineContext.cancelChildren()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}