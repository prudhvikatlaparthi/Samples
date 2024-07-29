package com.pru.coroutines

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pru.coroutines.ui.theme.CoroutinesTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.measureTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoroutinesTheme {
                val scope = rememberCoroutineScope()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(
                        onClick = {
                            scope.launch {

                                /*val time = measureTime {
                                    var result = 0
                                    scope.launch {
                                        result += api1()
                                    }
                                    scope.launch {
                                        result += api2()
                                    }
                                    scope.launch {
                                        result += api3()
                                    }
                                    Log.i("Prudhvi Log", "onCreate: $result")
                                }*/

                                /*val time = measureTime {
                                    var result = 0
                                    result += api1()
                                    result += api2()
                                    result += api3()
                                    Log.i("Prudhvi Log", "onCreate: $result")
                                }*/


                                var result = 0
                                val time = measureTime {
                                    val call1 = scope.async {
                                        api1()
                                    }
                                    val call2 = scope.async {
                                        api2()
                                    }
                                    val call3 = scope.async {
                                        api3()
                                    }
                                    result += call1.await() + call2.await() + call3.await()
                                    Log.i("Prudhvi Log", "onCreate: $result")
                                }

                                Log.i("Prudhvi Log", "onCreate: $time")
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Text(text = "Start")
                    }
                }
            }
        }
    }

    private suspend fun api1(): Int {
        delay(2000)
        return 1
    }

    private suspend fun api2(): Int {
        delay(3000)
        return 1
    }

    private suspend fun api3(): Int {
        delay(5000)
        return 1
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CoroutinesTheme {
        Greeting("Android")
    }
}