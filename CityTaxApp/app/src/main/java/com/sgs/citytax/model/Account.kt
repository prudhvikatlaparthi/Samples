package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Account(
    @SerializedName("acctname")
    var accountName: String? = "",
    @SerializedName("mob")
    var mobileNumber: String? = "",
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("acctid")
    var accountId: BigDecimal = BigDecimal.ZERO
){
    override fun toString(): String {
        return "$accountName\n${email ?: ""}".trim()
    }
}