package com.pru.currentlocation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.pru.currentlocation.databinding.ActivityMainBinding


@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    private var cancellationTokenSource = CancellationTokenSource()

    private val fineLocationRationalSnackbar by lazy {
        Snackbar.make(
            binding.container,
            R.string.fine_location_permission_rationale,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.ok) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        binding.button.setOnClickListener {
            locationRequestOnClick()
        }
//        binding.button.performClick()
        locationRequestOnClick()
    }

    override fun onStop() {
        super.onStop()
        cancellationTokenSource.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_GPS) {
            val isGpsEnabled = isGPSAvailable()
            if (!isGpsEnabled) {
                Log.d(TAG, "onActivityResult: ")
            } else {
                requestCurrentLocation()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult()")

        if (requestCode == REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() ->
                    Log.d(TAG, "User interaction was cancelled.")

                grantResults[0] == PackageManager.PERMISSION_GRANTED ->{
                    Snackbar.make(
                        binding.container,
                        R.string.permission_approved_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    requestCurrentLocation()
                }


                else -> {
                    Snackbar.make(
                        binding.container,
                        R.string.fine_permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                "com.pru.currentlocation",
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

    fun locationRequestOnClick() {
        Log.d(TAG, "locationRequestOnClick()")

        val permissionApproved =
            applicationContext.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionApproved) {
            requestCurrentLocation()
        } else {
            requestPermissionWithRationale(
                Manifest.permission.ACCESS_FINE_LOCATION,
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                fineLocationRationalSnackbar
            )
        }
    }

    private fun requestCurrentLocation() {
        Log.d(TAG, "requestCurrentLocation()")
        if (applicationContext.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
            /*fusedLocationClient.lastLocation
                .addOnCompleteListener { taskLocation ->
                    dosome(taskLocation, "last")
                }*/
            currentLocationTask.addOnCompleteListener { task: Task<Location> ->
               dosome(taskLocation = task, from = "current")
            }
        }
    }

    fun dosome(taskLocation: Task<Location>, from: String) {
        val result =  if (taskLocation.isSuccessful && taskLocation.result != null) {
            val result: Location = taskLocation.result
            "Location (success): ${result.latitude}, ${result.longitude}"
        } else {
            val res = isGPSAvailable()
            if (!res) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)

                startActivityForResult(intent, REQUEST_ENABLE_GPS)
            }
            val exception = taskLocation.exception
            "Location (failure): $exception"
        }
        Log.d(TAG, "getCurrentLocation() from: $result")
        logOutputToScreen(result,from)
    }


    private fun logOutputToScreen(outputString: String, from: String) {
        val finalOutput = binding.outputTextView.text.toString() + "\n$from " + outputString
        binding.outputTextView.text = finalOutput
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 10001
        private const val REQUEST_ENABLE_GPS = 10002
    }
}