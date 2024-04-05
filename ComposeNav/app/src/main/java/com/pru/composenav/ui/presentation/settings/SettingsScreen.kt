package com.pru.composenav.ui.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun SettingsScreen(navigator: DestinationsNavigator) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Settings", modifier = Modifier
            .align(Alignment.Center)
            .clickable {
                navigator.popBackStack()
            })
    }
}