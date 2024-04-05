package com.pru.bgserviceapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pru.bgserviceapp.domain.repository.AppRepository
import com.pru.bgserviceapp.service.DataSyncService
import com.pru.bgserviceapp.ui.theme.BGServiceAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var appRepository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BGServiceAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(modifier = Modifier) {
                            Button(onClick = {
                                Intent(applicationContext, DataSyncService::class.java).apply {
                                    action = DataSyncService.ACTION_START
                                    startService(this)
                                }
                            }) {
                                Text(text = "Start")
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = {
                                Intent(applicationContext, DataSyncService::class.java).apply {
                                    action = DataSyncService.ACTION_STOP
                                    startService(this)
                                }
                            }) {
                                Text(text = "Stop")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BGServiceAppTheme {
        Greeting("Android")
    }
}