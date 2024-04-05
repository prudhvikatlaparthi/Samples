package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.ParkingInOutsData

data class StoreVehicleParkingInOuts(
        val context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var data: ParkingInOutsData? = null

)