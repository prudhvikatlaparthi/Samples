package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ParentProperty(
        @SerializedName("ParentPropertyID")
        var parentPropertyID: Int? = 0,
        @SerializedName("ParentPropertyName")
        var parentPropertyName: String? = ""
) {
    override fun toString(): String {
        return parentPropertyName.toString()
    }
}