package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetViolationTicketsByViolationTicketID(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("violationticketid")
        var violationTicketID: Int? = 0
)