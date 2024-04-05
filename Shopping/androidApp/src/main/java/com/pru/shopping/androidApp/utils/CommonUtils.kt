package com.pru.shopping.androidApp.utils

import android.app.Activity
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.isVisible


fun View.show() {
    isVisible = true
}

fun View.hide() {
    isVisible = false
}

const val kErrorMessage = "Something went wrong"
const val kNoDataMessage = "No data found"

fun Activity.getDeviceMeasurements(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()