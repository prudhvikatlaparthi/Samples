package com.pru.composeplayground

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun Receipt() {
    // Get local density from composable
    val localDensity = LocalDensity.current

    // Create element height in pixel state
    var columnHeightPx by remember {
        mutableStateOf(0f)
    }

    // Create element height in dp state
    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            WaterMark(columnHeightDp)
        }
        Column(modifier = Modifier
            .onGloballyPositioned { coordinates ->
                // Set column height using the LayoutCoordinates
                columnHeightPx = coordinates.size.height.toFloat()
                columnHeightDp = with(localDensity) { coordinates.size.height.toDp() }
            }
            .fillMaxWidth()
            .padding(10.dp)) {
            repeat(30) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Header $it", modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Value $it", modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun WaterMark(
    height: Dp,
    url: String = "https://citytaxobjectstore.sycotax.bf/ObjectStoreTemp/ce77711c-1e54-4c04-a1a5-6b6a7524aa6a.png"
) {
    Log.i("Prudhvi Log", "WaterMark: $height")
    val eachItem = 180.dp
    val repeatCount = (height / eachItem).toInt()
    Column(modifier = Modifier.padding(top = eachItem / 4)) {
        repeat(repeatCount) {
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .size(eachItem)
                    .alpha(0.5f)
            )
        }
    }
}