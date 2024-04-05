package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.InvoicePenalties

data class PenaltyList(
        @SerializedName("VU_SAL_TaxInvoicePenalties", alternate = ["TaxInvoicePenalties"])
        var Penalties: ArrayList<InvoicePenalties> = arrayListOf()
)
