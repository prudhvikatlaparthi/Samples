package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.response.AgentCommissionPayOutListResponse
import com.sgs.citytax.api.response.Results


data class AgentCommissionResult (
        @SerializedName("Results")
     var results: AgentCommissionPayOutListResponse? = null,

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