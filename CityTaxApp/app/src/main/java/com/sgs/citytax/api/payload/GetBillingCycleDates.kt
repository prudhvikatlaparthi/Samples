package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetBillingCycleDates(
        val context:SecurityContext = SecurityContext(),
        @SerializedName("TaxInvoiceDate")
        var taxInvoiceDate:String?="",
        @SerializedName("BillingCycleID")
        var billingCycleID:Int?=0
)