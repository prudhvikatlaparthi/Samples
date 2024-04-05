package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetCitizenIdentityCards(
        var context: SecurityContext? = SecurityContext(),
        @SerializedName("conid")
        var contactID: Int? = 0
)