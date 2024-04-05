package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CRMActivityDomain
import com.sgs.citytax.model.TaskCode

data class GetIndividualTaxCount(
        @SerializedName("CartCount")
        var cartCount: String? = null,
        @SerializedName("GamingCount")
        var gamingCount: String? = null,
        @SerializedName("WeaponsCount")
        var weaponsCount: String? = null,
        @SerializedName("PropertyCount")
        var propertyCount: String? = null,
        @SerializedName("LandCount")
        var landCount: String? = null

)