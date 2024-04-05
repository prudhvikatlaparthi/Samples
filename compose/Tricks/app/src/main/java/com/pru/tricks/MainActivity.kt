package com.pru.tricks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.pru.tricks.ui.theme.TricksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TricksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    AsyncImage(
        model = "https://thumbnail.imgbin.com/21/17/2/imgbin-batman-cartoon-animation-cartoon-batman-Ln4ndPAzNFdTQ24GZXvAQEX3d_t.jpg",
        contentDescription = null,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TricksTheme {
        Greeting()
    }
}