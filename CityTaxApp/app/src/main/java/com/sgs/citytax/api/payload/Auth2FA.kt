package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class Auth2FA(

    var context: SecurityContext? = SecurityContext(),
    @SerializedName("setupcode")
    var authenticatorSetupCodeVal: String? = "",
    @SerializedName("secretkey")
    var AuthenticatorSecretKey: String? = ""
)