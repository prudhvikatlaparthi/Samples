package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ComplaintIncidentLocations(
        @SerializedName("IncidentLocations")
        var complaintDetailLocations: List<ComplaintIncidentDetailLocation> = arrayListOf(),
        @SerializedName("ComplaintLocations")
        var incidentDetailLocation: List<ComplaintIncidentDetailLocation> = arrayListOf()
)