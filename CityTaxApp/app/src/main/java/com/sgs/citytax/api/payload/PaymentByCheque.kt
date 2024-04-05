package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class PaymentByCheque(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerId: Int = 0,
        @SerializedName("chequeamt")
        var chequeamt: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("prodcode")
        var prodcode: String? = null,
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("chqdetails")
        var chequeDetails: ChequeDetails ?= null,
        @SerializedName("filewithext")
        var filenameWithExt: String? = "",
        @SerializedName("dtsrc")
        var fileData: String? = ""
)