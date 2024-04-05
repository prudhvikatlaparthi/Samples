package com.pru.jetinsta.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

@Composable
fun Sample(viewModel: SampleVM) {
    Column {
        OutlinedTextField(value = viewModel.email.value, onValueChange = {
            viewModel.email.value = it
        }, label = {
            Text(text = "Email")
        })
        Button(onClick = {
            viewModel.doLogin()
        }) {
            Text(text = "Login")
        }
    }
}

class SampleVM : ViewModel() {
    val email = mutableStateOf("")
    fun doLogin() {

    }
}