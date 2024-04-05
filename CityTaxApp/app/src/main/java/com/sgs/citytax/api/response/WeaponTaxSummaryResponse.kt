package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.WeaponTaxSummary

data class WeaponTaxSummaryResponse(
        @SerializedName("IndividualTaxDtls")
        var weaponIndividualTaxDtls: ArrayList<WeaponTaxSummary> = arrayListOf()
)
