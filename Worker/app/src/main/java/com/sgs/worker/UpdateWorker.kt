package com.sgs.worker

import android.content.Context
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return if (randomNumber() % 2 == 0) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Even", Toast.LENGTH_SHORT).show()
            }
            startWorker(context)
            Result.success()
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Odd", Toast.LENGTH_SHORT).show()
            }
            startWorker(context)
            Result.failure()
        }
    }
}

fun randomNumber(): Int {
    return (1..100).random()
}