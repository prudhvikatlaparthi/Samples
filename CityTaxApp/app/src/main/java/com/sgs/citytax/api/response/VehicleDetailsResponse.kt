package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VehicleMaster

data class VehicleDetailsResponse(
        @SerializedName("VehicleDetails")
        var vehicleMaster: VehicleMaster?=null,
        @SerializedName("IsSycotaxAvailable")
        var isSycoTaxAvailable:Boolean = false
)