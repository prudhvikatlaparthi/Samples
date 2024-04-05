package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class AccountsPayload(
    @SerializedName("payLoadString")
    var payloadString : String?=null,
    @SerializedName("context")
    var context : SecurityContext = SecurityContext(),
    @SerializedName("frmacctid")
    var fromAccountId : Int? =null,
    @SerializedName("FromAccountID")
    var frmAcctIdForProd : Int? =null,
    @SerializedName("filter")
    var filter : String? =null
)