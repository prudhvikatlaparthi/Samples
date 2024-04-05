package com.pru.composeapp.presentation.ui.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginScreen(navHost: NavHostController) {
    val focusManager = LocalFocusManager.current
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    var showBackDialog by remember {
        mutableStateOf(false)
    }
    val mainColor = Color(0xFF18171F)
    BackHandler(true) {
        showBackDialog = true
    }
    if (showBackDialog) {
        ShowCloseDialog(confirmButton = {
            showBackDialog = false
            navHost.popBackStack()
        }, dismissButton = {
            showBackDialog = false
        }, title = "Are you sure to close?")
    }
    Scaffold(backgroundColor = mainColor, bottomBar = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(text = "Don't have an account? ", color = Color.White)
                Text(
                    text = "Register",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.clickable {

                    })
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {}, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = mainColor
                ), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sign in", modifier = Modifier.padding(4.dp), fontSize = 16.sp)
            }
        }
    }) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = Icons.Default.ArrowBack.toString(),
                tint = Color.White,
                modifier = Modifier.clickable {
                    showBackDialog = true
                }
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.05f))
            Text(
                text = "Let's sign you in.",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Welcome back.\nYou've been missed!",
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.12f))
            TextField(
                value = email,
                onValueChange = {
                    email = it
                },
                singleLine = true,
                placeholder = {
                    Text(text = "Phone, Email or Username")
                },
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    disabledTextColor = Color.Transparent,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                trailingIcon = {
                    if (email.isNotEmpty()) {
                        Icon(
                            Icons.Filled.Cancel,
                            contentDescription = "",
                            modifier = Modifier.clickable {
                                email = ""
                            })
                    }
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = password,
                onValueChange = {
                    password = it
                },
                placeholder = {
                    Text(text = "Password")
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    disabledTextColor = Color.Transparent,
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Row (modifier = Modifier.padding(vertical = 10.dp)){
                        if (password.isNotEmpty()) {
                            Icon(
                                Icons.Filled.Cancel,
                                contentDescription = "",
                                modifier = Modifier.clickable {
                                    password = ""
                                })
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "",
                            modifier = Modifier.clickable {
                                passwordVisible = !passwordVisible
                            })
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )

        }
    }
}

@Composable
private fun ShowCloseDialog(
    confirmButton: () -> Unit,
    dismissButton: () -> Unit,
    title: String
) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = confirmButton)
            { Text(text = "Yes") }
        },
        dismissButton = {
            TextButton(onClick = dismissButton)
            { Text(text = "No") }
        },
        title = {
            Text(text = title)
        }
    )
}

@Composable
@Preview
fun Show() {
    LoginScreen(navHost = rememberNavController())
}