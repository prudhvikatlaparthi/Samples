package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.VuTax

data class TaxesResponse(
        @SerializedName("VU_CRM_CustomerProductInterests")
        var typeOfTaxes: List<VuTax> = arrayListOf()
)
