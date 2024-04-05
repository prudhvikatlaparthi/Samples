package com.pru.composeapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pru.composeapp.MainActivity

object CommonUtils {
    @Composable
    fun getMainActivity() = (LocalContext.current as? MainActivity)
}