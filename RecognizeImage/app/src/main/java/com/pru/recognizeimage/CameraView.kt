package com.pru.recognizeimage

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pru.recognizeimage.ui.theme.RecognizeImageTheme


@Composable
fun CameraView(viewModel: CameraViewModel, resultListener: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    Scaffold(containerColor = Color.Black) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AndroidView(
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        this.clipToOutline = true
                        implementationMode =
                            PreviewView.ImplementationMode.COMPATIBLE
                        post {
                            viewModel.startCamera(this.surfaceProvider, lifecycleOwner)
                        }
                    }
                }, modifier = Modifier
                    .width(dimensionResource(id = R.dimen.sc_width))
                    .height(
                        dimensionResource(id = R.dimen.sc_height)
                    )
                    .paint(
                        painterResource(id = R.drawable.bg),
                    )

            )
            Button(onClick = {
                viewModel.takePhoto { res, msg ->
                    if (!res) {
                        Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show()
                        return@takePhoto
                    }
                    resultListener.invoke()
                }
            }, modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "Capture")
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun CameraViewPreview() {
    RecognizeImageTheme {

    }
}