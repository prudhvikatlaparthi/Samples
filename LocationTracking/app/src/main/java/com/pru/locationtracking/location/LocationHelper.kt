package com.pru.locationtracking.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationHelper {
    fun getLocation(interval : Long)
}