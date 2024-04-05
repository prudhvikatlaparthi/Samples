package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMDocumentReference

data class UpdateEntityDocument(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tablname")
        var tableName: String? = "",
        @SerializedName("pkval")
        var primaryKeyValue: String? = "",
        @SerializedName("attach")
        var comDocumentReference: ArrayList<COMDocumentReference> = arrayListOf()
)