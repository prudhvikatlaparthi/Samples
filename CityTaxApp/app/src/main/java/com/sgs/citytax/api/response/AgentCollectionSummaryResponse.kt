package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AgentCollectionSummaryResponse(

        @SerializedName("TotalCollectionAmount")
        var totalCollectionAmount: Double? = null,
        @SerializedName("Results")
        var results: AgentCollectionResult? = null,
        @SerializedName("PageSize")
        var pageSize: Int? = null,
        @SerializedName("PageIndex")
        var pageIndex: Int? = null,
        @SerializedName("RecordsFound")
        var recordsFound: Int? = null,
        @SerializedName("TotalRecords")
        var totalRecords: Int? = null,
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords: Int? = null
)

data class AgentCollectionResult(
        @SerializedName("ChildAgentSummary")
        var childAgentSummaryList: List<ChildAgentSummary>? = null
)