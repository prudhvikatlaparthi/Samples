package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CRMIncidentSubtype(
        @SerializedName("IncidentSubtype")
        var incidentSubType: String? = null,
        @SerializedName("IncidentID")
        var incidentID: Int? = 0,
        @SerializedName("IncidentSubtypeID")
        var incidentSubTypeID: Int? = 0,
        @SerializedName("act")
        var act: String? = null
) {
    override fun toString(): String {
        return incidentSubType.toString()
    }
}