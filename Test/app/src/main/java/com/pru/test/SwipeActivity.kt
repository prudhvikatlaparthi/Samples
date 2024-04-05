package com.pru.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pru.test.ui.theme.TestTheme

class SwipeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CallerView(name = "Prudhvi")
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SwipeToDismiss() {
    val dismissState = rememberDismissState(initialValue = DismissValue.Default)


    SwipeToDismiss(
        state = dismissState,


        background = {
            val color = when (dismissState.dismissDirection) {
                DismissDirection.StartToEnd -> Color.Green
                DismissDirection.EndToStart -> Color.Red
                null -> Color.Transparent
            }
            val direction = dismissState.dismissDirection

            if (direction == DismissDirection.StartToEnd) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = "Move to Archive", fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }

                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.heightIn(5.dp))
                        Text(
                            text = "Move to Bin",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )

                    }
                }
            }
        },
        /**** Dismiss Content */
        /**** Dismiss Content */
        dismissContent = {
            Text("dismiss", modifier = Modifier.fillMaxSize())
        },
        /*** Set Direction to dismiss */
        /*** Set Direction to dismiss */
        directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
    )
}

@Composable
fun CallerView(name: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141414)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(.1f))
        Text(
            text = name,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFFEFEFE)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "iPhone",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFFEFEFE)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Alarm,
                    contentDescription = "Remind Me",
                    tint = Color(0xFFFEFEFE)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Remind Me", color = Color(0xFFFEFEFE))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.Message,
                    contentDescription = "Message",
                    tint = Color(0xFFFEFEFE)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Message", color = Color(0xFFFEFEFE))
            }
        }
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(60.dp)
                        .background(Color(0xFFEB4E3D)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = Icons.Filled.Call,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color(0xFFFEFEFE)),
                        modifier = Modifier.rotate(135f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Decline", color = Color(0xFFFEFEFE))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(60.dp)
                        .background(Color(0xFF76D672)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = Icons.Filled.Call,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color(0xFFFEFEFE))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Accept", color = Color(0xFFFEFEFE))
            }
        }
        Spacer(modifier = Modifier.fillMaxHeight(0.22f))
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun DefaultPreview() {
    TestTheme {
        CallerView(name = "Prudhvi")
    }
}