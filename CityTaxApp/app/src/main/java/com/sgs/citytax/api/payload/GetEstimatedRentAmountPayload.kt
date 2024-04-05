package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetEstimatedRentAmountPayload(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("proptypeid")
        var proptypeid: Int? = null,
        @SerializedName("rentamt")
        var rentamt: Double? = null,
        @SerializedName("strtdt")
        var strtdt: String? = null
)