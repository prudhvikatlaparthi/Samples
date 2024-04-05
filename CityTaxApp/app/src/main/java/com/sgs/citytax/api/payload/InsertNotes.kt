package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.COMNotes

data class InsertNotes(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tablname")
        var tableName: String? = null,
        @SerializedName("prykeyval")
        var primaryKeyValue: String? = null,
        @SerializedName("notes")
        var notes: ArrayList<COMNotes>? = arrayListOf()
)