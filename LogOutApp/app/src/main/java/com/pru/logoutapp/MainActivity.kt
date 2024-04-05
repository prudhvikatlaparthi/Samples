@file:OptIn(ExperimentalMaterial3Api::class)

package com.pru.logoutapp

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pru.logoutapp.ui.theme.LogOutAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogOutAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) },)
    }) {
        CalendarUI(Modifier.padding(it))
    }
}

@Composable
fun CalendarUI(modifier: Modifier) {
    val context = LocalContext.current

    var loginTime: Calendar? by remember {
        mutableStateOf(null)
    }
    val loginDateString = remember {
        derivedStateOf {
            loginTime?.let {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(it.time).toString()
            } ?: "-"
        }
    }
    val logoutDateString = remember {
        derivedStateOf {
            loginTime?.let {
                val logoutCalendar = Calendar.getInstance()
                logoutCalendar.time = it.time
                logoutCalendar.add(Calendar.HOUR, 9)
                logoutCalendar.add(Calendar.MINUTE, 30)
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(logoutCalendar.time)
                    .toString()
            } ?: "-"
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().padding(top = 10.dp)
    ) {
        Button(onClick = {
            val c = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    c[Calendar.HOUR_OF_DAY] = hourOfDay
                    c[Calendar.MINUTE] = minute
                    loginTime = c
                },
                loginTime?.get(Calendar.HOUR_OF_DAY) ?: c[Calendar.HOUR_OF_DAY],
                loginTime?.get(Calendar.MINUTE) ?: c[Calendar.MINUTE],
                true
            )
            timePickerDialog.show()
        }) {
            Text(text = "Select Time".uppercase(), fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(0.8f).fillMaxHeight(0.2f)) {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Login Time:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = loginDateString.value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(text = "Logout Time:", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = logoutDateString.value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 480, heightDp = 800)
@Composable
fun GreetingPreview() {
    LogOutAppTheme {
        MainScreen()
    }
}