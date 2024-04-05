package com.pru.usersapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
fun MainScreen(navigator: DestinationsNavigator) {
    val scope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
    ) {
        var email by remember {
            mutableStateOf("")
        }
        LaunchedEffect(key1 = true) {
            email = App.preference.email
        }
        Text(email)
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = {
            scope.launch {
                App.preference.isLoggedIn = false
                App.preference.email = ""
            }
            navigator.navigate(LoginScreenDestination.route, builder = {
                popUpTo(MainScreenDestination.route) {
                    inclusive = true
                }
            })
        }) {
            Text(text = "Login Out")
        }
    }
}