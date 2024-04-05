package com.sgs.flightcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.sgs.flightcompose.ui.theme.FlightComposeTheme
import com.sgs.flightcompose.ui.theme.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlightComposeTheme {
                Scaffold {
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        val progressState = viewModel.progressState.collectAsState()
                        val formState = viewModel.formData.collectAsState()
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = progressState.value / 100,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        /*LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(formState.value) { item ->
                                val state by remember {
                                    viewModel.registerMap[item.id]!!
                                }
                                OutlinedTextField(value = state, onValueChange = {
                                    viewModel.registerMap[item.id]!!.value = it
                                    viewModel.updateProgress()
                                }, label = {
                                    Text(text = item.hint)
                                }, modifier = Modifier.fillMaxWidth(), leadingIcon = {
                                    Icon(imageVector = item.icon, contentDescription = null)
                                })
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }*/
                        val singapore = LatLng(1.35, 103.87)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(singapore, 10f)
                        }
                        GoogleMap(
                            properties = MapProperties(
                                isMyLocationEnabled = true,
                                mapType = MapType.NORMAL
                            ), cameraPositionState = cameraPositionState
                        ) {
                            Marker(
                                state = MarkerState(position = singapore),
                                title = "Singapore",
                                snippet = "Marker in Singapore"
                            )
                        }
                    }
                }
            }
        }
    }
}