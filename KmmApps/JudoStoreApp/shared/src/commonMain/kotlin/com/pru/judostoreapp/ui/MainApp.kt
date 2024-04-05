package com.pru.judostoreapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.pru.judostoreapp.controller.AppController
import com.pru.judostoreapp.ui.auth.LoginScreen
import kotlinx.coroutines.launch

@Composable
fun MainApp() {
    val state by AppController.navChannel.collectAsState(AppController.ControlIntent.None)
    var showProgress by remember { mutableStateOf(false) }
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    when (state) {
        AppController.ControlIntent.DismissLoader -> {
            showProgress = false
        }

        is AppController.ControlIntent.Navigate -> {
            val navigator = LocalNavigator.currentOrThrow
            navigator.push((state as AppController.ControlIntent.Navigate).screen)
        }

        AppController.ControlIntent.None -> Unit
        AppController.ControlIntent.PopBackStack -> {
            val navigator = LocalNavigator.currentOrThrow
            navigator.pop()
        }

        is AppController.ControlIntent.ShowAlertDialog -> {

        }

        is AppController.ControlIntent.ShowLoader -> {
            showProgress = true
        }

        is AppController.ControlIntent.ShowSnackBar -> {
            val msg = (state as AppController.ControlIntent.ShowSnackBar).message!!
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = msg,
                )
            }
        }
    }
    Scaffold(modifier = Modifier, scaffoldState = scaffoldState) {
        Box(modifier = Modifier.padding(it).fillMaxSize()) {
            if (showProgress) {
                CircularProgressIndicator()
            }
            Navigator(LoginScreen)
        }
    }
}