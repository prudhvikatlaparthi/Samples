package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

class LoginPayload(
        @SerializedName("context")
        var context: SecurityContext = SecurityContext()
)