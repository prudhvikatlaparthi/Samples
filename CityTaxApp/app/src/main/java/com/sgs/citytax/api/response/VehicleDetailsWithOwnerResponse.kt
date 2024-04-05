package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VehicleDetails

data class VehicleDetailsWithOwnerResponse(
        @SerializedName("VehiclesDetails")
        var vehicleDetails:ArrayList<VehicleDetails>? = null
)