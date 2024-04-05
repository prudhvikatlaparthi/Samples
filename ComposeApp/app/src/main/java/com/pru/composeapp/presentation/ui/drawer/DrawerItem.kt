package com.pru.composeapp.presentation.ui.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pru.composeapp.R
import com.pru.composeapp.presentation.navigation.ScreenRoute

@Composable
fun DrawerItem(screenRoute: ScreenRoute, onItemClick: (String) -> Unit) {
    val background = android.R.color.transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(screenRoute.route) })
            .height(45.dp)
            .background(colorResource(id = background))
            .padding(start = 10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = screenRoute.title,
            colorFilter = ColorFilter.tint(colorResource(id = R.color.dark)),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(24.dp)
                .width(24.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = screenRoute.title,
            fontSize = 18.sp,
            color = colorResource(id = R.color.dark)
        )
    }
}