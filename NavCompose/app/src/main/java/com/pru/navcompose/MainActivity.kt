package com.pru.navcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pru.navcompose.ui.theme.NavComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavComposeTheme {
                NavigationUI()
            }
        }
    }
}

@Composable
private fun MainActivity.NavigationUI() {
    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                appController.navChannel.collect {
                    when (it) {
                        is AppController.NavigationIntent.Navigate -> {
                            navController.navigate(it.route, it.navOptions)
                        }
                        AppController.NavigationIntent.PopBackStack -> {
                            if (!navController.popBackStack()) {
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
    MyNavHost(navController = navController)
}

@Composable
fun MyNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Profile.routeName) {
        composable(Routes.Profile.routeName) { Profile() }
        composable(Routes.FriendsList.routeName) { FriendsList() }
        composable(Routes.FinalList.routeName) { FinalList() }
    }
}

@Composable
fun Profile() {
    TextButton(onClick = {
        appController.navigate(Routes.FriendsList.routeName)
    }) {
        Text(text = "profile")
    }
}

@Composable
fun FriendsList(viewModel: MainViewModel = viewModel()) {
    TextButton(onClick = {
        viewModel.counter = 1
        val navOptions = NavOptions.Builder().setPopUpTo(Routes.Profile.routeName, true).build()
        appController.navigate(Routes.FinalList.routeName, navOptions)
    }) {
        Text(text = "FriendsList")
    }
}

@Composable
fun FinalList(viewModel: MainViewModel = viewModel()) {
    TextButton(onClick = {
        appController.popBack()
    }) {
        Text(text = "Final List ${viewModel.counter}")
    }
}