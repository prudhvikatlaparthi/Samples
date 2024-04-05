package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class IncidentLocations(
        @SerializedName("IncidentLocations")
        var incidentDetailLocation: List<IncidentDetailLocation> = arrayListOf(),
        @SerializedName("TotalRecordCounts")
        var totalRecordCounts: Int = 0
)