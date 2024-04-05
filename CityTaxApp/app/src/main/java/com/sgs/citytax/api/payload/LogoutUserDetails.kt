package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.base.MyApplication

data class LogoutUserDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("dvccode")
        var deviceCode: String? = MyApplication.getPrefHelper().serialNumber
)