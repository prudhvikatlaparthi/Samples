package com.pru.usersapp.ui.settings

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pru.usersapp.R
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class)
@Destination(start = true)
@Composable
fun SettingsScreen() {
    var dataList by remember {
        mutableStateOf(List(2) {
            "Item $it"
        }.toMutableList())
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
            "Settings",
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "GROUPS")
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            val data = mutableListOf<String>()
            data.addAll(dataList)
            data.add("Item ${dataList.size + 1}")
            dataList = data
        }) {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier
                )

            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "dfgdg",
            modifier = Modifier.clickable {
                Log.i("Prudhvi Log", "SettingsScreen: ")
            }
        )
        var emailText by remember {
            mutableStateOf("")
        }
        OutlinedTextField(value = emailText, onValueChange = {
            emailText = it
        }, label = {
            Text(text = "Enter Email")
        })
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(dataList.size) { i ->
                Card(
                    backgroundColor = Color.Gray,
                    elevation = 10.dp,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = {
                        val data = mutableListOf<String>()
                        data.addAll(dataList)
                        data.removeAt(i)
                        dataList = data
                    }
                ) {
                    Text(
                        text = dataList[i],
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }

            }

        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewFn() {
    SettingsScreen()
}