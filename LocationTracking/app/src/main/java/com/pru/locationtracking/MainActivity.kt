package com.pru.locationtracking

import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.pru.locationtracking.location.LocationSdk
import com.pru.locationtracking.ui.theme.LocationTrackingTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : ComponentActivity() {
    private val locationSdk: LocationSdk by lazy {
        LocationSdk(
            context = applicationContext,
            client = FusedLocationProviderClient(applicationContext),
            coroutineScope = lifecycleScope
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationTrackingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current
                    val locations by remember {
                        mutableStateOf(mutableListOf<Location>())
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 10.dp, horizontal = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            locationSdk.getLocation(1000).catch {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }.onEach {
                                locations.add(it)
                            }.launchIn(locationScope)
                        }) {
                            Text(text = "Start")
                        }
                        Button(onClick = {
                            locationScope.cancel()
                        }) {
                            Text(text = "Stop")
                        }
                        LazyColumn() {
                            items(locations.size) { index ->
                                Text(text = "Location ${locations[index].latitude} - ${locations[index].longitude}")
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
    LocationTrackingTheme {
        Greeting("Android")
    }
}