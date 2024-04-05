package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AgentSubscriptionList(
        @SerializedName("usrid")
        var userID: String? = null,
        @SerializedName("SubscriptionCode")
        var subscriptionCode: String? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("mob")
        var mobile: String? = null,
        @SerializedName("email")
        var email: String? = null
) {
    override fun toString(): String {
        return "$name"
    }
}