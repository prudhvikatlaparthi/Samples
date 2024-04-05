package com.sgs.citytax.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sgs.citytax.util.LogHelper

class LogUploadWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        LogHelper.sendLogFiles()
        return Result.success()
    }
}