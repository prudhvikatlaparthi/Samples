package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TicketHistory

data class PendingViolationImpoundment(
        @SerializedName("Results")
        var results: PendingList? = null,
        @SerializedName("PageSize")
        var pageSize: Int? = 10,
        @SerializedName("PageIndex")
        var pageIndex: Int? = 1,
        @SerializedName("TotalRecords")
        var totalRecords: Int? = 0,
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords: Int? = null
)

data class PendingList(
        @SerializedName("PendingList")
        var ticketHistory: ArrayList<TicketHistory>? = arrayListOf()
)