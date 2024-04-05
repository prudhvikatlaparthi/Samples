package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class SaveOutstanding(
        @SerializedName("InitialOutstandingID")
        var initialOutstandingID: Int? = null,
        @SerializedName("OutstandingTypeCode")
        var outstandingTypeCode: String? = null,
        @SerializedName("Year")
        var year: Int? = null,
        @SerializedName("acctid")
        var accountID: Int? = null,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("netrec")
        var netReceivable: BigDecimal? = BigDecimal.ZERO
)