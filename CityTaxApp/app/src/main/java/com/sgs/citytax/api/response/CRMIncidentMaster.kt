package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CRMIncidentMaster(
        @SerializedName("Incident")
        var incident: String? = null,
        @SerializedName("IncidentID")
        var incidentID: Int? = 0,
        @SerializedName("code")
        var code: String? = null,
        @SerializedName("act")
        var act: String? = null
) {
    override fun toString(): String {
        return incident.toString()
    }
}