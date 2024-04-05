package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VehicleDetails

data class VehicleOwnershipDetailsResult(
        @SerializedName("VehicleOwnershipDetails")
        var vehicleDetails:ArrayList<VehicleDetails>? = null
)