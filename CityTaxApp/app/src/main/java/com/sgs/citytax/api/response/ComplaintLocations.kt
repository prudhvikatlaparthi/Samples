package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ComplaintLocations(
        @SerializedName("ComplaintLocations")
        var complaintsDetailLocations: List<ComplaintIncidentDetailLocation> = arrayListOf(),
        @SerializedName("TotalRecordCounts")
        var totalRecordCounts: Int = 0
)