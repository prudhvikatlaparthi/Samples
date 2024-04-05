package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class COMDocumentType(
        @SerializedName("doctypid")
        var documentTypeID: Int? = 0,
        @SerializedName("docrtyp")
        var name: String? = null,
        @SerializedName("docprftyp")
        var docprftyp: String? = null,
        @SerializedName("defntn")
        var defntn:String? = null
) {
    override fun toString(): String {
        return name.toString()
    }
}