package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference


data class AddServiceRequest(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("servicerequest")
        var servicerequest: ServiceRequest? = null,
        @SerializedName("attach")
        var attachment: List<COMDocumentReference> = arrayListOf()


)