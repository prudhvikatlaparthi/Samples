package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class PropertyTaxInitOutstandingPenalties(
        val context : SecurityContext =  SecurityContext(),
        @SerializedName("vchrno")
        var voucherNo:Int?=0,
        @SerializedName("prodcode")
        var productCode:String?=""
)