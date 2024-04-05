package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PendingServiceInvoiceListTable

data class PendingServiceListResponse(
        @SerializedName("Results")
        var list: PendingServiceInvoiceListTable? = null
)