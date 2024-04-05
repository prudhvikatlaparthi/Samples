package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCRMAgents(
        var AgentID: Int? = 0,
        var AgentName: String? = "",
        @SerializedName("mob")
        var mobileNo: String? = ""
) {
    override fun toString(): String {
        return AgentName.toString()
    }
}