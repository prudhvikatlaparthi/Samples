package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VehicleMaster

data class GetSearchVehicleDetails(
        @SerializedName("VehicleDetails")
        var vehicleDetails: ArrayList<VehicleMaster> = arrayListOf()
)