package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMActivityDomain(
        @SerializedName("ActivityDomain")
        var name: String? = null,
        @SerializedName("ActivityDomainID")
        var ID: Int
) {
    override fun toString(): String {
        return name.toString()
    }
}