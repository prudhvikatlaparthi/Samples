package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetTicketsIssuedToVehicles(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("filterdata")
        var filterData:String?=""
)