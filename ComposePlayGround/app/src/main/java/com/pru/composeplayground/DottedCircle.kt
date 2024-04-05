package com.pru.composeplayground

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DottedCircle(content: @Composable () -> Unit) {
    val stroke = Stroke(
        width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp
    val radius = screenWidth * 0.9f
    Box(modifier = Modifier, contentAlignment = Alignment.Center) {
        Canvas(
            Modifier.size(radius.dp)
        ) {

            drawCircle(
                color = Color.Black,
                style = stroke,
                radius = (convertPixelsToDp(context, radius)),
            )
        }
        Image(
            painter = painterResource(id = R.drawable.scissors),
            contentDescription = null,
            modifier = Modifier
                .padding(end = (radius).dp)
                .size(16.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.scissors),
            contentDescription = null,
            modifier = Modifier
                .padding(start = (radius).dp)
                .rotate(180f)
                .size(16.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.scissors),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = (radius).dp)
                .rotate(90f)
                .size(16.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.scissors),
            contentDescription = null,
            modifier = Modifier
                .padding(top = (radius).dp)
                .rotate(270f)
                .size(16.dp)
        )
        content()
    }
}

fun convertPixelsToDp(context: Context, pixels: Float): Float {
    val screenPixelDensity = context.resources.displayMetrics.density
    val dpValue = pixels / screenPixelDensity
    return dpValue
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun PrevCircle() {
    DottedCircle {
        Column {
            Text(text = "sdfsf")
            Text(text = "wewe4")
        }
    }
}