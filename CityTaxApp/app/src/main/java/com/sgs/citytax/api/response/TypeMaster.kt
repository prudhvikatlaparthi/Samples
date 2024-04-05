package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class TypeMaster(
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("typeId")
        var typId: Int? = 0
) {
    override fun toString(): String {
        return type.toString()
    }
}