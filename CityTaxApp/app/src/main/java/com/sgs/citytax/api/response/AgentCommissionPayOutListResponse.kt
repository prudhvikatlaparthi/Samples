package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AgentCommissionPayOutListResponse(
        @SerializedName("CommissionHistory")
        var commissionHistory: List<CommissionHistory> = arrayListOf(),
        @SerializedName("TotalRecordCounts")
        var totalRecordsCount: Int? = null
)