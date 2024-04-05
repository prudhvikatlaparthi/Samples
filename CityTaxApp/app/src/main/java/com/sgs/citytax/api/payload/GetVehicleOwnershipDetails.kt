package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetVehicleOwnershipDetails(
        var context :SecurityContext = SecurityContext(),
        @SerializedName("vehno")
        var vehicleNo:String?=""
)