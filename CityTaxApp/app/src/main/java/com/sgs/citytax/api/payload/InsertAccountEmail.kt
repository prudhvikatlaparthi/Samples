package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.CRMAccountEmails

data class InsertAccountEmail(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("AccountEmails")
        var accountEmails: CRMAccountEmails? = null
)