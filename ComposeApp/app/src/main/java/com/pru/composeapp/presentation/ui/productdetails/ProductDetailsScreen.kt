package com.pru.composeapp.presentation.ui.productdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pru.composeapp.presentation.custom_widgets.MyAppBar

@Composable
fun ProductDetailsScreen(navHostController: NavHostController) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    Scaffold(topBar = {
        MyAppBar(title = "ProductDetail Screen") {
            IconButton(onClick = {
                navHostController.popBackStack()
            }) {
                Icon(Icons.Filled.ArrowBack, "")
            }
        }
    }, scaffoldState = scaffoldState) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "ProductDetail Screen", modifier = Modifier
                .align(Alignment.Center)
                .clickable {
                    navHostController.previousBackStackEntry?.savedStateHandle?.set("Tap", 4)
                    navHostController.popBackStack()
                })
        }
    }
}