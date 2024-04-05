package com.pru.diceapp.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pru.diceapp.R
import com.pru.diceapp.ui.custom.DiceLoadingView
import com.pru.diceapp.ui.dice.DiceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    val item = homeViewModel.diceItems
    val scope = rememberCoroutineScope()
    val mainView = DiceLoadingView(context = LocalContext.current)
    Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        TopView(mainView, scope)
        Spacer(modifier = Modifier.height(10.dp))
        LazyVerticalGrid(
            cells = GridCells.Fixed(count = 3),
            content = {
                items(count = item.size) { index ->
                    DiceItem(dice = item[index])
                }
            },
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun Display() {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WalletNScoreView(
            icon = Icons.Default.AccountBalanceWallet,
            mainBG = Color(0xFFFFC914),
            bgBorder = Color(0xFFFF970F),
            secondBG = Color(0xFFF9D55B),
            iconTint = Color(0xFFE49202),
            name = "Wallet",
            value = 100
        )
        WalletNScoreView(
            icon = Icons.Default.MilitaryTech,
            mainBG = Color(0xFF92F716),
            bgBorder = Color(0xFFBAFB69),
            secondBG = Color(0xFFBAFB69),
            iconTint = Color(0xFFE49202),
            name = "Last Score",
            value = 0
        )
    }
}

@Composable
private fun TopView(
    mainView: DiceLoadingView,
    scope: CoroutineScope
) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
//        WalletNScoreView()

        Row(
            modifier = Modifier.background(
                color = Color(0xFF92F716),
                shape = RoundedCornerShape(8.dp)
            )
        ) {
            Image(painter = painterResource(id = R.drawable.prize), contentDescription = null)
            Text(text = "Last Score")
            Row(
                modifier = Modifier.background(
                    color = Color(0xFFBAFB69),
                    shape = RoundedCornerShape(8.dp)
                )
            ) {
                Text(text = "0")
                Image(painter = painterResource(id = R.drawable.rupee), contentDescription = null)
            }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.4f),
        backgroundColor = Color(0xFFE0F1CB), elevation = 8.dp, shape = RoundedCornerShape(8.dp),
    ) {
        Box(modifier = Modifier) {
            Column(modifier = Modifier) {
                AnimatedVisibility(visible = true) {
                    AndroidView(
                        factory = {
                            mainView
                        }, modifier = Modifier
                            .size(80.dp)
                            .fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        startDice(mainView, scope)
                    },
                    colors = ButtonDefaults.buttonColors(contentColor = Color(0xFF504396))
                ) {
                    Text("Start Game", color = Color.White)
                }
            }
            /*Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .height(220.dp),
//                    shape = CircleShape,
                    elevation = 8.dp,
                    backgroundColor = Color(0xFFDDEEFE)
                ) {
                    Card(
                        modifier = Modifier
                            .width(10.dp)
                            .height(10.dp),
//                        shape = CircleShape,
                        elevation = 8.dp,
                        backgroundColor = Color(0xFFFFFFFF)
                    ) {
                        Text("sdfsf")
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        startDice(mainView, scope)
                    },
                    colors = ButtonDefaults.buttonColors(contentColor = Color(0xFF504396))
                ) {
                    Text("Start Game", color = Color.White)
                }
            }*/
        }
    }
}

@Composable
private fun WalletNScoreView(
    icon: ImageVector,
    iconTint: Color,
    mainBG: Color,
    bgBorder: Color,
    secondBG: Color,
    name: String,
    value: Int
) {
    Row(
        modifier = Modifier
            .background(
                color = mainBG,
                shape = RoundedCornerShape(20.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = secondBG,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = bgBorder,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(top = 4.dp, start = 12.dp, end = 12.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.rotate(270f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Normal)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Row(
            modifier = Modifier.padding(top = 4.dp, start = 0.dp, end = 12.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = value.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Image(painter = painterResource(id = R.drawable.rupee), contentDescription = null)
        }
    }
}

fun startDice(
    mainView: DiceLoadingView,
    scope: CoroutineScope
) {
    mainView.buildViews()
    mainView.setupAnimator()
    scope.launch {
        delay(5000)
        mainView.release()
    }
}