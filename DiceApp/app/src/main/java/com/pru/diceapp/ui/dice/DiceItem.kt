package com.pru.diceapp.ui.dice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.pru.diceapp.R
import com.pru.diceapp.model.Dice
import com.pru.diceapp.ui.custom.DiceView


@Composable
fun DiceItem(modifier: Modifier = Modifier, dice: Dice) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AndroidView(
            factory = {
                val diceView = DiceView(context = it)
                diceView.number = dice.number
                diceView
            },
            modifier
                .size(85.dp)
                .padding(8.dp)
        )
        Box(
            modifier = modifier
                .background(
                    color = colorResource(id = R.color.red),
                    shape = RoundedCornerShape(4.dp)
                )
                .size(70.dp)
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.rupee), contentDescription = null,
                )
                Spacer(modifier = modifier.height(5.dp))
                Text(text = dice.bet.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}