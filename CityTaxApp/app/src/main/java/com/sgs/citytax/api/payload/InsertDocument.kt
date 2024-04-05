package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference

data class InsertDocument(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tablname")
        var TableName: String? = null,
        @SerializedName("prykeyval")
        var PrimaryKeyValue: String? = null,
        @SerializedName("docnoinitial")
        var docnoinitial: String? = null,
        @SerializedName("attach")
        var attachment: List<COMDocumentReference> = arrayListOf()
)
