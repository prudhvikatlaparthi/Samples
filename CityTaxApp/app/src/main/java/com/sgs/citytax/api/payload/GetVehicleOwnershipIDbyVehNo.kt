package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetVehicleOwnershipIDbyVehNo(
        var context :SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var acctid:String?="",
        @SerializedName("vehno")
        var vehicleNo:String?=""
)