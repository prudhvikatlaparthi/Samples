package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VehicleMaster

data class SearchVehicleResultResponse(
        @SerializedName("Results")
        var results: GetSearchVehicleDetails? = null,
        @SerializedName("VehiclesDetails")
        var vehiclesDetails: ArrayList<VehicleMaster>? = null
)