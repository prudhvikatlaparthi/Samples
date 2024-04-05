package com.pru.composenav.ui.presentation.user_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pru.composenav.ui.presentation.destinations.SettingsScreenDestination
import com.pru.composenav.ui.presentation.destinations.UserDetailScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.random.Random

@Composable
@Destination(start = true)
fun UserListScreen(navController: DestinationsNavigator) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "User List", modifier = Modifier
            .clickable {
                navController.navigate(
                    UserDetailScreenDestination(
                        userName = "${
                            Random(100).nextInt(
                                100
                            )
                        }"
                    )
                )
            })
        Text(text = "Settings", modifier = Modifier
            .clickable {
                navController.navigate(SettingsScreenDestination())
            })
    }
}