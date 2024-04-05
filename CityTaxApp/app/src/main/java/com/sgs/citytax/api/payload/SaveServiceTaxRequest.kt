package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference

data class SaveServiceTaxRequest(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var serviceRequest: NewServiceRequest? = null,
        @SerializedName("rmks")
        var remarks: String = "",
        @SerializedName("srupdates")
        var srUpdate: SRUpdate? = null,
        @SerializedName("attach")
        var attachment: List<COMDocumentReference> = arrayListOf()
)