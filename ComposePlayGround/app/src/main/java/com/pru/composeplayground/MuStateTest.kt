package com.pru.composeplayground

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun MuStateTest() {
    val state = remember{
        mutableStateOf(0)
    }
    Button(onClick = {
        state.value = 0
    }) {
        Text(text = "Tap ${state.value}")
    }
}