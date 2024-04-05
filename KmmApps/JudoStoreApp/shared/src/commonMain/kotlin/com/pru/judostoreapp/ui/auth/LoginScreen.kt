package com.pru.judostoreapp.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen

object LoginScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { AuthModel() }
        Box(modifier = Modifier.padding(20.dp)) {
            Card(modifier = Modifier.padding(20.dp)) {
                Column(
                    modifier = Modifier.padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Judo Store")
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(value = screenModel.userName, onValueChange = {
                        screenModel.userName = it
                    }, label = {
                        Text("User name")
                    }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = screenModel.password,
                        onValueChange = {
                            screenModel.password = it
                        },
                        label = {
                            Text("Password")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = {
                        screenModel.authenticate()
                    }) {
                        Text("Login")
                    }
                }
            }
        }
    }
}