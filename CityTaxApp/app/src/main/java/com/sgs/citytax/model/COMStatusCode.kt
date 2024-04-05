package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class COMStatusCode(
        @SerializedName("sts")
        var status: String? = null,
        @SerializedName("stscode")
        var statusCode: String? = null
) {
    override fun toString(): String {
        return status.toString()
    }
}