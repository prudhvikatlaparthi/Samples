package com.pru.mar21_2024.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pru.mar21_2024.presentation.ui.main.DataItem

@Composable
fun HousieGrid(data: List<DataItem>, itemSize: Dp) {

    var position = 0
    /*Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        for (i in 0..2) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                for (j in 0..8) {
                    GridItem(itemSize, data.getOrNull(position))
                    position++
                }
            }
        }
    }*/

    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 0..8) {
            Column(verticalArrangement = Arrangement.Center) {
                for (j in 0..2) {
                    GridItem(itemSize, data.getOrNull(position), Color(0xFFFFB6C1))
                    position++
                }
            }
        }
    }
}

@Composable
fun GridItem(itemSize: Dp, dataItem: DataItem?, color: Color) {
    Box(
        modifier = Modifier
            .size(itemSize)
            .padding(1.dp)
            .background(color)
            .border(1.dp, Color.Black, shape = RectangleShape), contentAlignment = Alignment.Center
    ) {
        dataItem?.number?.let {
            Text(
                text = it.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun HousiePrev() {
    HousieGrid(listOf(DataItem(number = 1)), 30.dp)
}