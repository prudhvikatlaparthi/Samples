package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class VerifyBusinessContacts(

        var context: SecurityContext = SecurityContext(),
        @SerializedName("mob")
        var mobile: String = "",
        @SerializedName("smstemplatecode")
        var smsTemplateCode: String? = "",
        @SerializedName("mobileotp")
        var mobileOTP: String? = "",
        @SerializedName("emailtemplatecode")
        var emailTemplateCode: String? = "",
        @SerializedName("emailotp")
        var emailOTP: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("acctid")
        var accountId: Int? = 0

)