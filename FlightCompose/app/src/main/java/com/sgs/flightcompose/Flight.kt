package com.sgs.flightcompose

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Flight() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val endPos = (screenWidth - 86).dp
    var isIdle by remember { mutableStateOf(true) }
    val offsetXAnimation: Dp by animateDpAsState(
        if (isIdle) 20.dp else endPos,
        infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val offsetYAnimation: Dp by animateDpAsState(
        0.dp,
        infiniteRepeatable(
            animation = tween(easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    LaunchedEffect(Unit) {
        isIdle = !isIdle
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(BorderStroke(6.dp, Color(0xFFEC6E69)), shape = CircleShape),
            )
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                items(100) {
                    Spacer(
                        modifier = Modifier
                            .width(8.dp)
                            .height(1.dp)
                            .padding(horizontal = 2.dp)
                            .background(Color.LightGray)
                            .alpha(0.5f)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(BorderStroke(6.dp, Color(0xFF322934)), shape = CircleShape)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.airplane),
                contentDescription = "Flight",
                modifier = Modifier
                    .size(35.dp)
                    .absoluteOffset(x = offsetXAnimation, y = offsetYAnimation)
            )
        }
    }
}