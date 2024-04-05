package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class UMXUserOrgBranches(
        @SerializedName("brname")
        var branchName: String? = "",
        @SerializedName("prntorgbrid")
        var parentOrganisationId: Int? = 0,
        @SerializedName("rmks")
        var remarks: String? = "",
        var fax: String? = "",
        @SerializedName("brcode")
        var branchCode: String? = "",
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int? = 0
) {
    override fun toString(): String {
        return branchName.toString()
    }
}