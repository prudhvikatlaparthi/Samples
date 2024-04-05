package com.pru.composeapp.presentation.custom_widgets

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun MyAppBar(title: String, navigationIcon: @Composable (() -> Unit)) {
    TopAppBar(
        title = { Text(text = title, fontSize = 18.sp) },
        navigationIcon = navigationIcon,
        backgroundColor = Color.White,
        contentColor = Color.Blue
    )
}