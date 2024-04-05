package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.ProEstimatedTax

class GetEstimatedTax4PropPayload (
        @SerializedName("context")
        var context: SecurityContext = SecurityContext(),
        @SerializedName("proestimatedtax")
        var propEstimatedTax: ProEstimatedTax? = null
)