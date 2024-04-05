package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CartTaxSummary
import com.sgs.citytax.model.WeaponTaxSummary

data class CartTaxSummaryResponse(
        @SerializedName("IndividualTaxDtls")
        var cartIndividualTaxDtls: ArrayList<CartTaxSummary> = arrayListOf()
)
