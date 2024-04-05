package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class IncidentAndComplaintsCountsResponse(
        @SerializedName("IncidentCount")
        var incidentCount: Int? = 0,
        @SerializedName("TaskCount")
        var complaintCount: Int? = 0
)
