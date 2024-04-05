package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMActivityClass(
        @SerializedName("ActivityClass")
        var name: String? = null,
        @SerializedName("ActivityClassCode")
        var code: String? = "",
        @SerializedName("ActivityClassID")
        var ID: Int? = 0
) {
    override fun toString(): String {
        return name.toString()
    }
}