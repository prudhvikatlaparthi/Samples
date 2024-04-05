package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.PendingLicenses4Agent

data class GetPendingLicenses4AgentResponse(
        @SerializedName("PendingLicenses4Agent")
        var PendingLicenses4Agent: List<PendingLicenses4Agent>? = null,
        @SerializedName("TotalRecordCounts")
        var totalRecordCounts: Int? = null
)