package com.pru.roomdbsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import com.pru.roomdbsample.Util.response
import com.pru.roomdbsample.db.entities.BasicDetailsEntity
import com.pru.roomdbsample.db.entities.ControlEntity
import com.pru.roomdbsample.db.entities.LayoutEntity
import com.pru.roomdbsample.model.Report
import com.pru.roomdbsample.ui.theme.RoomDBSampleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoomDBSampleTheme {
                // A surface container using the 'background' color from the theme
                val scope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier) {
                        Button(onClick = {
                            val report = Gson().fromJson(response, Report::class.java)
                            scope.launch {
                                for (basicDetails in report.report) {
                                    val basicDetailsEntity = BasicDetailsEntity(
                                        name = basicDetails.name,
                                        reportId = basicDetails.iD.toLong()
                                    )
                                    val basicDetailsId = MyApp.database.basicDetailsDao()
                                        .insertBasicDetails(basicDetailsEntity)

                                    for (layout in basicDetails.layouts) {
                                        val layoutEntity = LayoutEntity(
                                            mergeWithAboveLayout = layout.mergewithabovelayout,
                                            sortOrder = layout.sortOrder,
                                            basicDetailsId = basicDetailsId,
                                            reportId = basicDetails.iD.toLong()
                                        )
                                        val layoutId =
                                            MyApp.database.layoutDao().insertLayout(layoutEntity)

                                        for (control in layout.controls) {
                                            val controlEntity = ControlEntity(
                                                numberOfColumns = control.noOfColumns,
                                                value = control.value,
                                                hint = control.hint,
                                                inputType = control.inputType,
                                                inputData = control.inputData.toString(),
                                                dataType = control.dataType,
                                                colour = control.colour,
                                                isInfoIcon = control.isInfoIcon,
                                                infoText = control.infoText,
                                                layoutId = layoutId,
                                                mergeWithAboveLayout = layout.mergewithabovelayout
                                            )
                                            MyApp.database.controlDao().insertControl(controlEntity)
                                        }
                                    }
                                }
                            }
                        }) {
                            Text(text = "Insert")
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
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoomDBSampleTheme {
        Greeting("Android")
    }
}