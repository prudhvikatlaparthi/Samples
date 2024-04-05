package com.sgs.worker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sgs.worker.ui.theme.WorkerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkerTheme {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier) {
                        Button(onClick = {
                            scope.launch {
                                startWorker(context)
                            }
                        }) {
                            Text(text = "Start Service")
                        }
                        Button(onClick = {
                            WorkManager.getInstance(context).cancelAllWork()
                        }) {
                            Text(text = "Stop Service")
                        }
                    }
                }
            }
        }
    }
}

suspend fun startWorker(context: Context) {
    delay(10_000)
    val backupWorkRequest = OneTimeWorkRequestBuilder<UpdateWorker>()
        .addTag(UpdateWorker::class.java.simpleName)
        .build()
    WorkManager.getInstance(context).enqueue(backupWorkRequest)
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WorkerTheme {
        Greeting("Android")
    }
}