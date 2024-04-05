package com.pru.locationtracking.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class LocationSdk(
    private val context: Context,
    private val client: FusedLocationProviderClient,
    private val coroutineScope: CoroutineScope
) : LocationHelper {

    private val _location = MutableSharedFlow<Location>()
    val location get() = _location

    @SuppressLint("MissingPermission")
    override fun getLocation(interval: Long) {
        val hasLocationPermission = hasLocationPermission()
        if (!hasLocationPermission) {
            throw LocationException("Please grant location permission")
        }
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!gpsEnabled && !networkEnabled) {
            throw LocationException("Gps or Network not available")
        }
        val request = LocationRequest.create()
            .setInterval(interval)
            .setFastestInterval(interval)



        client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            result?.locations?.lastOrNull()?.let {
                coroutineScope.launch {
                    _location.emit(it)
                }
            }
        }
    }

    private fun stopLocation() {
        client.removeLocationUpdates(locationCallback)
    }

    private fun hasLocationPermission(): Boolean {
        return context.packageManager.checkPermission(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            context.packageName
        ) == PackageManager.PERMISSION_GRANTED
                && context.packageManager.checkPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            context.packageName
        ) == PackageManager.PERMISSION_GRANTED
    }

    private inner class LocationException(message: String) : Exception(message)
}