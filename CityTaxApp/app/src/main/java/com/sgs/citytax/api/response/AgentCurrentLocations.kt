package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AgentCurrentLocations(
        @SerializedName("AgentCurrentLocations")
        var agentLocations: List<AgentLocations> = arrayListOf(),
        @SerializedName("TotalRecordCounts")
        var totalRecordCounts: Int = 0
)