package com.sgs.citytax.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

fun hasPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

fun requestForPermission(activity:Activity,permission: Array<String>, requestCode: Int) {
    requestPermissions(activity,permission, requestCode)
}

fun isPermissionGranted(grantResults: IntArray): Boolean {
    var permissionGranted = true
    if (grantResults.isNotEmpty()) {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                permissionGranted = false
                break
            }
        }
    } else permissionGranted = false
    return permissionGranted
}