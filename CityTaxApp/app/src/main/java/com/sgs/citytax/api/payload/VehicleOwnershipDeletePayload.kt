package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class VehicleOwnershipDeletePayload(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("vehicleownershipid")
        var vehicleOwnershipId: String? = null)