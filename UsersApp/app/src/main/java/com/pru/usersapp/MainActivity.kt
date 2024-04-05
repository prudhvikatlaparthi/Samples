package com.pru.usersapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.pru.usersapp.ui.NavGraphs
import com.pru.usersapp.ui.theme.UsersAppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateStatusBarColor("#BEFF6C")
        setContent {
            UsersAppTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
        lifecycleScope.launchWhenStarted {
            val result = suspendActive()
            if (result is Result.Success) {
                Log.i("Prudhvi Log", "onCreate: ${result.data}")
            } else {
                Log.i("Prudhvi Log", "onCreate: ${result.message}")
            }
        }

        isActive(object : AppCheck<Tes>{
            override fun onSuccess(response: Tes) {

            }

            override fun onFailure(message: String) {

            }
        })
    }

    fun updateStatusBarColor(color: String?) { // Color must be in hexadecimal fromat
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor(color)
    }
}

suspend fun suspendActive() = suspendCoroutine<Result<Tes>> { continuation ->
    isActive(object : AppCheck<Tes> {
        override fun onSuccess(response: Tes) {
            continuation.resume(Result.Success(response))
        }

        override fun onFailure(message: String) {
            continuation.resume(Result.Failure(message))
        }

    })
}

fun isActive(appCheck: AppCheck<Tes>) {
    if (Random.nextBoolean()) {
        appCheck.onSuccess(Tes(data = (0..100).random()))
    } else appCheck.onFailure(message = "Error")
}

interface AppCheck<T> {
    fun onSuccess(response: T)
    fun onFailure(message: String)
}

data class Tes(var data: Int)

sealed class Result<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Result<T>(data = data)
    class Failure<T>(message: String) : Result<T>(message = message)
}