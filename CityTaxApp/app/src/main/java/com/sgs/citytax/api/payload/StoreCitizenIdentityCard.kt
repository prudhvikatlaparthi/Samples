package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.response.CitizenIdentityCard


data class StoreCitizenIdentityCard(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("data")
        var citizenIdentityCard: CitizenIdentityCard? = null
)