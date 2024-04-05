package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.ImpoundSignature
import com.sgs.citytax.model.Impoundment
import com.sgs.citytax.model.MultipleImpoundmentTypes

class StoreMultipleImpoundmentTicketPayload(
        @SerializedName("context")
        var context: SecurityContext = SecurityContext(),
        @SerializedName("multitypes")
        var impoundmentTicketPayload: ArrayList<MultipleImpoundmentTypes> = arrayListOf(),
        @SerializedName("data")
        var impoundment: Impoundment? = Impoundment(),
        @SerializedName("fileext")
        var fileExtension: String? = "",
        @SerializedName("filedata")
        var fileData: String? = "",
        @SerializedName("attach")
        var documentsList: ArrayList<COMDocumentReference> = arrayListOf(),
        @SerializedName("signatures")
        var signature: ImpoundSignature?=null
)