package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VehicleSycotaxListResponse (
        @SerializedName("VehicleSycotaxList")
        var vehicleSycotaxList: ArrayList<UnusedVehicleSycotaxID>? = arrayListOf()
)