package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.COMNotes

class StorePropertyOwnershipWithPropertyOwnerPayload(
        @SerializedName("context")
        var context: SecurityContext = SecurityContext(),
        @SerializedName("propertyownership")
        var propertyownership: PropertyOwnershipPayload = PropertyOwnershipPayload(),
        @SerializedName("propertyowners")
        var propertyowners: ArrayList<PropertyOwnersPayload> = arrayListOf(),
        @SerializedName("attach")
        var attachments: List<COMDocumentReference>? = null,
        @SerializedName("notes")
        var notes: List<COMNotes>? = null,
        @SerializedName("ispropertychildtab")
        var ispropertychildtab: Boolean = true
)