package com.pru.ricknmortykmm.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.pru.ricknmortykmm.Greeting
import com.pru.ricknmortykmm.android.utils.Global.bigDecimal
import com.pru.ricknmortykmm.android.utils.Global.bigDecimalScale
import com.pru.ricknmortykmm.remote.ApiService
import com.pru.ricknmortykmm.repository.RepositorySdk
import com.pru.ricknmortykmm.utils.ApiState
import java.math.RoundingMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GreetingView(Greeting().greet())
                }
            }
        }
        val apiService = ApiService()
        val repositorySdk = RepositorySdk(apiService = apiService)
        lifecycleScope.launchWhenStarted {
            repositorySdk.getValue().collect {
                if (it is ApiState.Success) {
                    Log.i(
                        "Prudhvi Log",
                        "onCreate: ${
                            it.data?.price.bigDecimal
                        }"
                    )
                    Log.i(
                        "Prudhvi Log",
                        "onCreate: scale ${
                            it.data?.price.bigDecimalScale
                        }"
                    )
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
