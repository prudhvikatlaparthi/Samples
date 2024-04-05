package com.sgs.citytax.ui

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack

class BackgroundService(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val latLng = LatLng(inputData.getDouble("KEY_LAT", 0.0), inputData.getDouble("KEY_LONG", 0.0))
        APICall.updateConnectedDevice(latLng, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
            }

            override fun onFailure(message: String) {
            }
        })

        return Result.success()
    }
}


