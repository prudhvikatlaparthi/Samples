package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetEstimatedFineAmount(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("violationtypeid")
        var violationTypeId:Int?=0
)