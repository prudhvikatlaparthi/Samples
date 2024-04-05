package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CRMAgents

data class ServiceRequestResponse(
        @SerializedName("Results")
        var results: ServiceRequests? = null,
        @SerializedName("TotalSearchedRecords")
        var totalSearchedRecords: Int = 0
)