package com.pru.composeapp.presentation.ui.search

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pru.composeapp.MainActivity
import com.pru.composeapp.presentation.custom_widgets.MyAppBar
import com.pru.composeapp.presentation.navigation.ScreenRoute

@Composable
fun SearchScreen() {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val context = LocalContext.current
    if (context is MainActivity){
        Toast.makeText(context, "alphaa", Toast.LENGTH_SHORT).show()
        context.displayMessage()
    }
    Scaffold(topBar = {
        MyAppBar(title = "Search Screen") {
            IconButton(onClick = {
                (context as MainActivity).navHost.popBackStack()
            }) {
                Icon(Icons.Filled.ArrowBack, "")
            }
        }
    }, scaffoldState = scaffoldState) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Search Screen", modifier = Modifier
                .align(Alignment.Center)
                .clickable {
                    (context as MainActivity).navHost.navigate(ScreenRoute.ProductDetailsScreen.route)
                })
        }
    }
}