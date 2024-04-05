package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.BusinessLocations
import com.sgs.citytax.model.LawPendingTransactionLocations

data class LAWPendingTransaction4Agent(
        @SerializedName("PendingTransaction4Agent")
        var lawPendingTransactionLocations: List<LawPendingTransactionLocations> = arrayListOf(),
        @SerializedName("TotalRecordCounts")
        var totalRecordCounts: Int = 0
)