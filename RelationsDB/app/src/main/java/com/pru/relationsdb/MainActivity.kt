package com.pru.relationsdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pru.relationsdb.entities.Vehicle
import com.pru.relationsdb.entities.VehicleOwner
import com.pru.relationsdb.ui.theme.RelationsDBTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RelationsDBTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val scope = rememberCoroutineScope()
                    Box {
                        Column(modifier = Modifier) {
                            Button(onClick = {
                                scope.launch {
                                    var vehicle = Vehicle(
                                        vehicleNo = "AP 10 AE 1234",
                                        brand = "Honda",
                                        manufacturingYear = "2022"
                                    )
                                    MyApp.database.vehicleDao().upsert(vehicle)

                                    vehicle = Vehicle(
                                        vehicleNo = "AP 10 AE 1222",
                                        brand = "Tata",
                                        manufacturingYear = "2023"
                                    )
                                    MyApp.database.vehicleDao().upsert(vehicle)

                                    vehicle = Vehicle(
                                        vehicleNo = "AP 10 AE 1543",
                                        brand = "Swift",
                                        manufacturingYear = "2011"
                                    )
                                    MyApp.database.vehicleDao().upsert(vehicle)

                                    var owner = VehicleOwner(
                                        ownerName = "Raju",
                                        ownerMobile = "954544544",
                                        vehicleNo = "AP 10 AE 1234"
                                    )
                                    MyApp.database.vehicleOwnerDao().upsert(owner)

                                    owner = VehicleOwner(
                                        ownerName = "Ram",
                                        ownerMobile = "954544544",
                                        vehicleNo = "AP 10 AE 1234"
                                    )
                                    MyApp.database.vehicleOwnerDao().upsert(owner)

                                    owner = VehicleOwner(
                                        ownerName = "Prema",
                                        ownerMobile = "954544544",
                                        vehicleNo = "AP 10 AE 1543"
                                    )
                                    MyApp.database.vehicleOwnerDao().upsert(owner)
                                }
                            }) {
                                Text(text = "Tap")
                            }
                            val owners = MyApp.database.vehicleOwnerDao().getVehicles()
                                .collectAsState(initial = emptyList())
                            LazyColumn {
                                items(owners.value.size) {
                                    val owner = owners.value[it]
                                    ListItem(headlineText = {
                                        owner.ownerName
                                    }, supportingText = {
                                        owner.ownerMobile
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RelationsDBTheme {
        Greeting("Android")
    }
}