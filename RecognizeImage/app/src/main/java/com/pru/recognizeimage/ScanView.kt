package com.pru.recognizeimage

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pru.recognizeimage.Global.handleScanCameraImage
import com.pru.recognizeimage.ui.theme.RecognizeImageTheme
import java.io.File

@Composable
fun ScanView(viewModel: CameraViewModel, scanListener: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        scanListener.invoke()
    }

    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    val results = remember {
        mutableStateOf<List<Result>>(listOf())
    }
    LaunchedEffect(viewModel.resultPath) {
        if (!viewModel.resultPath.isNullOrBlank()) {
            val uri = Uri.fromFile(File(viewModel.resultPath!!))
            handleScanCameraImage(uri = uri, bitmapListener = {
                bitmap = it
            }) {
                results.value = it
            }
        }
    }

    Scaffold(containerColor = Color.White) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .width(320.dp)
                        .height(200.dp),
                )
            }
            Button(onClick = {
                launcher.launch(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                    )
                )
            }, modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "Scan Image")
            }
            LazyColumn {
                items(results.value) { res ->
                    androidx.compose.material3.ListItem(
                        headlineContent = {
                            Text(
                                text = res.resultValue,
                                modifier = Modifier.alpha(if (res.isSelected) 1f else 0.5f)
                            )
                        },
                        trailingContent = {
                            if (res.isSelected) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.Green
                                )
                            }
                        }, modifier = Modifier.clickable {
                            val prev = results.value
                            val s = prev.indexOf(res)
                            val rm = results.value.toMutableList()
                            rm.forEach { it.isSelected = false }
                            res.isSelected = true
                            rm[s] = res
                            results.value = emptyList()
                            results.value = rm
                        })
                }
            }
        }
    }
}

data class Result(var resultValue: String, var isSelected: Boolean)


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RecognizeImageTheme {

    }
}