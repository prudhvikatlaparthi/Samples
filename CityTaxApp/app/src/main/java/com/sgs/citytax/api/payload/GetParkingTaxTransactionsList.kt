package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetParkingTaxTransactionsList(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("vehno")
        var vehno: String? = "",
        @SerializedName("parkingplcid")
        var parkingplcid: Int? = 0,
        @SerializedName("onlydue")
        var onlydue: String? = "Y"
)