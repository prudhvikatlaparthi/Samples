package com.pru.firsthybridapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import dev.icerock.moko.resources.desc.desc

@Composable
fun SplashScreen() {
    var count by remember {
        mutableStateOf(5)
    }
    LaunchedEffect(Unit) {
        /*while (count > 0) {
            delay(1000)
            count--
        }*/
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(SharedRes.strings.splash))
        Text(count.toString(), fontSize = 30.sp)
    }

}