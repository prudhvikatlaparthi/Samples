package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference


data class EditServiceRequest(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("svcreqno")
        var serviceRequestNo: String? = null,
        @SerializedName("srupdates")
        var srUpdate: SRUpdate? = null,
        @SerializedName("attach")
        var attachment: List<COMDocumentReference> = arrayListOf(),
        @SerializedName("long")
        var longitude: Double? = 0.0,
        @SerializedName("lat")
        var latitude: Double? = 0.0
)