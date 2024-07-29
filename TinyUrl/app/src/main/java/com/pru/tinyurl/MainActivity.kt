package com.pru.tinyurl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.andreabaccega.googlshortenerlib.GooglShortenerRequestBuilder
import com.andreabaccega.googlshortenerlib.GoogleShortenerPerformer
import com.pru.tinyurl.ui.theme.TinyUrlTheme
import com.squareup.okhttp.OkHttpClient


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TinyUrlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Button(
        onClick = {
            val shortener = GoogleShortenerPerformer(OkHttpClient())
            val surl = shortener.shortenUrl(GooglShortenerRequestBuilder().buildRequest("https://devmaputo.sycotax.bf/CustomerBill.aspx?TaxInvoiceNo=fwKOuQkBrfAGQzuls79kvQ==&LngCode=MrNDlwb/WHyqhRfV3OpFPw==")).shortenedUrl
            println(surl)
        }
    ) {
        Text(text = "Tiny Url")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TinyUrlTheme {
        Greeting("Android")
    }
}