package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PendingServiceInvoiceListTable(
        @SerializedName("PendingServiceInvoice")
        var pendingList: ArrayList<PendingServiceDetails> = arrayListOf()
)