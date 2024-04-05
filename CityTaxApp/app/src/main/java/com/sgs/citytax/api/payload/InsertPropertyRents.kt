package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.CRMPropertyRent

data class InsertPropertyRents(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("prtyrnts")
        var propertyRent: CRMPropertyRent? = null,
        @SerializedName("attach")
        var attachments: List<COMDocumentReference>? = null
)