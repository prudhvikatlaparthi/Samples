package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class TransactionHistoryGenPayload(
        var context : SecurityContext = SecurityContext(),
        @SerializedName("sycotaxid")
        var sycoTaxID : String ?= null,
        @SerializedName("pageindex")
        var pageindex : Int ?= 0,
        @SerializedName("pagesize")
        var pagesize : Int ?= 10
)