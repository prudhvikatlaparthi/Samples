package com.pru.usersapp.ui.login

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pru.usersapp.App
import com.pru.usersapp.ui.destinations.LoginScreenDestination
import com.pru.usersapp.ui.destinations.MainScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun LoginScreen(navigator: DestinationsNavigator) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
    ) {
        val scope = rememberCoroutineScope()
        var email by remember {
            mutableStateOf("")
        }
        OutlinedTextField(value = email, onValueChange = {
            email = it
        }, label = {
            Text(text = "Email")
        }, leadingIcon = {
            Icon(imageVector = Icons.Filled.Email, contentDescription = "email")
        })
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = {
            scope.launch {
                App.preference.isLoggedIn = true
                App.preference.email = email
            }
            navigator.navigate(MainScreenDestination.route, builder = {
                popUpTo(LoginScreenDestination.route) {
                    inclusive = true
                }
            })
        }) {
            Text(text = "Login in")
        }
    }
}