package com.pru.composeapp.presentation.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.pru.composeapp.presentation.custom_widgets.MyAppBar
import com.pru.composeapp.presentation.navigation.ScreenRoute
import com.pru.composeapp.presentation.ui.drawer.DrawerContent
import com.pru.composeapp.utils.CommonUtils.getMainActivity
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navHostController: NavHostController) {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    val activity = getMainActivity()
    var showLogoutDialog by remember {
        mutableStateOf(false)
    }
    BackHandler(true) {
        if (scaffoldState.drawerState.isOpen) {
            scope.launch {
                scaffoldState.drawerState.close()
            }
        } else {
            showLogoutDialog = true
        }
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    activity?.finish()
                })
                { Text(text = "Yes") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                })
                { Text(text = "No") }
            },
            title = {
                Text(text = "Are you sure to close?")
            }
        )
    }
    Surface(color = MaterialTheme.colors.background) {
        Scaffold(scaffoldState = scaffoldState, topBar = {
            MyAppBar(title = "Compose App") {
                IconButton(onClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }) {
                    Icon(Icons.Filled.Menu, "")
                }
            }
        }, drawerBackgroundColor = Color.White, drawerContent = {
            DrawerContent(navHostController, scope, scaffoldState)
        }, drawerContentColor = Color.Blue) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Home Screen", modifier = Modifier
                        .align(Alignment.Center)
                        .clickable {
                            navHostController.navigate(ScreenRoute.SearchScreen.route)
                        }
                )
            }
        }

    }
}

