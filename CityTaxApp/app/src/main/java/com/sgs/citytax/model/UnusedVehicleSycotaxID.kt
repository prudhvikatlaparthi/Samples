package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class UnusedVehicleSycotaxID (
        @SerializedName("VehicleSycotaxID")
        var vehicleSycotaxID:String? = ""
)