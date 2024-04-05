package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PropertyDueSummary(
        @SerializedName("Year")
        var year: Int? = 0,
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("TaxSubType")
        var taxSubType: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("InvoiceAmount")
        var invoiceAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("InvoiceDue")
        var invoiceDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PenaltyAmount")
        var penaltyAmount:BigDecimal?= BigDecimal.ZERO,
        @SerializedName("PenaltyDue")
        var penaltyDue:BigDecimal?= BigDecimal.ZERO
)