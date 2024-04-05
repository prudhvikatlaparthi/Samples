package com.pru.jetinsta.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar() {
    BottomNavigation() {
        BottomNavigationItem(selected = true, onClick = { }, icon = {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                modifier = Modifier.size(24.dp)
            )
        }, alwaysShowLabel = false)
        BottomNavigationItem(selected = false, onClick = { }, icon = {
            Icon(
                imageVector = Icons.Filled.Search, contentDescription = "Search",
                modifier = Modifier.size(24.dp)
            )
        }, alwaysShowLabel = false)
        BottomNavigationItem(selected = false, onClick = { }, icon = {
            Icon(
                imageVector = Icons.Filled.Add, contentDescription = "Add",
                modifier = Modifier.size(24.dp)
            )
        }, alwaysShowLabel = false)
        BottomNavigationItem(selected = false, onClick = { }, icon = {
            Icon(
                imageVector = Icons.Filled.Favorite, contentDescription = "Favorite",
                modifier = Modifier.size(24.dp)
            )
        }, alwaysShowLabel = false)
        BottomNavigationItem(selected = false, onClick = { }, icon = {
            Icon(
                imageVector = Icons.Filled.Person, contentDescription = "Person",
                modifier = Modifier.size(24.dp)
            )
        }, alwaysShowLabel = false)

    }
}