package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetOutstandingList(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("acctid")
        var accountID: Int? = 0,
        @SerializedName("prodcode")
        var prodcode: String ? = null,
        @SerializedName("vchrno")
        var voucherNo: Int  ? = 0

)