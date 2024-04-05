package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.GamingMachineTaxSummary

data class GammingTaxSummaryResponse(
        @SerializedName("IndividualTaxDtls")
        var gammingIndividualTaxDtls: ArrayList<GamingMachineTaxSummary> = arrayListOf()
)
