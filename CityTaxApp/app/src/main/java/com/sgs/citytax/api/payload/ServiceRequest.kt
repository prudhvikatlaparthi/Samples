package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName


data class ServiceRequest(
        @SerializedName("svcreqdt")
        var IncidentDate: String? = null,
        @SerializedName("stscode")
        var StatusCode: String? = null,
        @SerializedName("issdesc")
        var Description: String? = null,
        @SerializedName("assgnd2usrid")
        var AssignTo: String? = null,
        @SerializedName("acctid")
        var acctid: String? = null,
        @SerializedName("documents")
        var documents: String? = null,

        @SerializedName("incidentID")
        var IncedentTypeID: String? = null,

        @SerializedName("long")
        var longitude: Double? = 0.0,
        @SerializedName("lat")
        var latitude: Double? = 0.0


)