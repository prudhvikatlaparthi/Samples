package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteAccountMappingData(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tablname")
        var tableName: String? = null,
        @SerializedName("vchrno")
        var voucherNo: String = "",
        @SerializedName("acctid")
        var accountID: Int = 0
)