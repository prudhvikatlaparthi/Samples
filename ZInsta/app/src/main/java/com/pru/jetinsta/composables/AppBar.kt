package com.pru.jetinsta.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pru.jetinsta.R

@Composable
fun AppBar() {
    TopAppBar(elevation = 0.dp) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Instagram",
                modifier = Modifier.weight(1f),
                fontSize = 26.sp,
                color = Color.Black,
                letterSpacing = 1.sp,
                fontFamily = FontFamily(Font(R.font.insta))
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.FavoriteBorder,
                contentDescription = "FavoriteBorder", tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Filled.Message,
                contentDescription = "Message",
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}