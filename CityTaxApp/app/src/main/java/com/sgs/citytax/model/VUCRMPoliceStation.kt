package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCRMPoliceStation(
        @SerializedName("brname")
        var branchName: String? = "",
        @SerializedName("brcode")
        var branchCode: String? = "",
        @SerializedName("usrorgid")
        var userOrgID: Int? = 0,
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int? = 0
){
        override fun toString(): String {
                return branchName.toString()
        }
}