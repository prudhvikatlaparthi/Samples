package com.sgs.citytax.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.os.Build
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.util.Constant.REQUEST_CODE_LOCATION
import com.sgs.citytax.util.Constant.REQUEST_CODE_LOCATION_SETTINGS

class LocationHelper(val context: Context, val view: View, val activity: Activity? = null, val fragment: Fragment? = null) {

    private lateinit var request: LocationRequest

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null

    private var listener: Location? = null

    init {
        if (mFusedLocationClient == null)
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)
    }

    fun fetchLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasPermission(context.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (fragment != null)
                fragment.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
            else if (activity != null)
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        } else isPlayServicesAvailable()
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (isPermissionGranted(grantResults)) {
                isPlayServicesAvailable()
            } else {
                showSnackBarMessage("Please enable location permission to proceed.")
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_CANCELED -> {
                    showSnackBarMessage("Please enable GPS to proceed.")
                }
                Activity.RESULT_OK -> {
                    listen()
                    mFusedLocationClient!!.requestLocationUpdates(request, mLocationCallback!!, Looper.myLooper())
                }
            }
        }
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun isPlayServicesAvailable() {
        val playAPI = GoogleApiAvailability.getInstance()
        val resultCode = playAPI.isGooglePlayServicesAvailable(context.applicationContext)
        if (resultCode != ConnectionResult.SUCCESS)
            if (playAPI.isUserResolvableError(resultCode))
                playAPI.getErrorDialog(activity, resultCode, 1010101002).show()
            else
                showSnackBarMessage("Please enable/update Google Play Services from settings")
        else
            isGPSEnabled()
    }

    private fun isGPSEnabled() {
        request = LocationRequest().setInterval(1).setFastestInterval(1).setSmallestDisplacement(10F).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(request)
        val result = LocationServices.getSettingsClient(context.applicationContext).checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
                listen()
                mFusedLocationClient!!.requestLocationUpdates(request, mLocationCallback!!, Looper.myLooper())
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable = exception as ResolvableApiException
                            resolvable.startResolutionForResult(context as Activity?, REQUEST_CODE_LOCATION_SETTINGS)
                        } catch (e: IntentSender.SendIntentException) {
                        } catch (e: ClassCastException) {
                        }
                    }
                }
            }
        }
    }

    private fun listen() {
        listener?.start()
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                super.onLocationResult(result)
                val latitude = result?.lastLocation?.latitude ?: 0.0
                val longitude = result?.lastLocation?.longitude ?: 0.0
                MyApplication.getPrefHelper().latitude = "$latitude"
                MyApplication.getPrefHelper().longitude = "$longitude"

                listener?.found(latitude, longitude)

                mFusedLocationClient?.removeLocationUpdates(mLocationCallback!!)
            }
        }
    }

    fun setListener(listener: Location) {
        this.listener = listener
    }

    fun disconnect() {
        if (mLocationCallback != null && mFusedLocationClient != null)
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        mLocationCallback = null
        mFusedLocationClient = null
    }

    interface Location {
        fun start()
        fun found(latitude: Double, longitude: Double)
    }

}