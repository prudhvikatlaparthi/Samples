package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCRMOrganization(
        @SerializedName("orgz")
        var organization: String? = null,
        @SerializedName("orgzid")
        var organizationID: Int? = 0
) {
    override fun toString(): String {
        return organization.toString()
    }
}