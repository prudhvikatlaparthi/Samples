package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class NewTicketCreation(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var data: NewTicketCreationData? = null
)