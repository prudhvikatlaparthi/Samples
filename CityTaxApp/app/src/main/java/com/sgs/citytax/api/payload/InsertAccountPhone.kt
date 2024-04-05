package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class InsertAccountPhone(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("AccountPhones")
        var accountPhone: AccountPhone? = null
)