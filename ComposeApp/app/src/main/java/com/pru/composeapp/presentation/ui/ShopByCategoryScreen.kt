package com.pru.composeapp.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pru.composeapp.presentation.custom_widgets.MyAppBar

@Composable
fun ShopByCategoryScreen(navHostController: NavHostController) {
    Scaffold(topBar = {
        MyAppBar(title = "Shop by Category") {
            IconButton(onClick = {
                navHostController.popBackStack()
            }) {
                Icon(Icons.Filled.ArrowBack, "")
            }
        }
    }) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "ShopByCategory Screen", modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}