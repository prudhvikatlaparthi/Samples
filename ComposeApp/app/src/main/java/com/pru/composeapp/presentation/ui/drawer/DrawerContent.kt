package com.pru.composeapp.presentation.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pru.composeapp.R
import com.pru.composeapp.presentation.navigation.ScreenRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    navHostController: NavHostController,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    val drawerItems =
        listOf(
            ScreenRoute.HomeScreen,
            ScreenRoute.ShopByCategoryScreen,
            ScreenRoute.WishSavedListScreen,
            ScreenRoute.MyOrdersScreen,
            ScreenRoute.MyAccountScreen,
            ScreenRoute.HelpScreen,
            ScreenRoute.LoginScreen,
        )
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.white))
    ) {
        Row (modifier = Modifier.padding(10.dp)){
            Icon(
                Icons.Filled.AcUnit,
                contentDescription = Icons.Filled.AcUnit.toString(),
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = "Happy")
                Text(text = "joyy@mail.com")
            }
        }
        Divider()
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )
        drawerItems.forEach { item ->
            DrawerItem(screenRoute = item, onItemClick = {
                navHostController.navigate(it) {
                    navHostController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                scope.launch {
                    scaffoldState.drawerState.close()
                }
            })

        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Developed by Prudhvi",
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}