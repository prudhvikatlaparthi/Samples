package com.pru.tiktokcompose.presentation.home

import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import com.pru.tiktokcompose.presentation.feed.FeedScreen

@Composable
fun HomeScreen() {
    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState) {
        FeedScreen()
    }
}