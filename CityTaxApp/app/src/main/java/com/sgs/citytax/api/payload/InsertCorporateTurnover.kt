package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.CRMCorporateTurnover

data class InsertCorporateTurnover(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("CorporateTurnover")
        var corporateTurnOver: CRMCorporateTurnover? = null,
        @SerializedName("proportionaldutyonrental")
        var proportionalDutyOnRental: ProportionalDutyOnRental? = null
)