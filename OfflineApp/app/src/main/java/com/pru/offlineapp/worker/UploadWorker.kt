package com.pru.offlineapp.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pru.offlineapp.MyApp
import com.pru.offlineapp.sync_server.AppSync
import com.pru.offlineapp.utils.NetworkUtils
import java.util.concurrent.TimeUnit

class UploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            if (NetworkUtils.isOnline()) {
                AppSync.getInstance().uploadData()
                Log.i("Prudhvi Log", "doWork: Online Done")
            } else {
                Log.i("Prudhvi Log", "doWork: Offline")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startUploadWorker()
        return Result.success()
    }
}

fun startUploadWorker() {
    val uploadRequest =
        OneTimeWorkRequestBuilder<UploadWorker>().setInitialDelay(30, TimeUnit.SECONDS).build()
    WorkManager.getInstance(MyApp.context.applicationContext).enqueue(uploadRequest)
}