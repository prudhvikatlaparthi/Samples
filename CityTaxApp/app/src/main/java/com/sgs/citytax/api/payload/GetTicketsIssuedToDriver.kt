package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTicketsIssuedToDriver(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("drivinglicenseno")
        var drivingLicenseNo: String? = ""
)