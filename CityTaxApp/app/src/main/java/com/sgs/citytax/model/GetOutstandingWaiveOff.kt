package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class GetOutstandingWaiveOff(
        @SerializedName("InitialOutstandingID")
        var initialOutstandingID: Int? = 0,
        @SerializedName("OutstandingTypeCode")
        var outstandingTypeCode: String? = "",
        @SerializedName("Year")
        var year: Int? = 0,
        @SerializedName("AccountID")
        var accountID: Int? = 0,
        @SerializedName("ProductCode")
        var productCode: String? = "",
        @SerializedName("VoucherNo")
        var voucherNO: Int? = 0,
        @SerializedName("NetReceivable")
        var netReceivable: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("OutstandingType")
        var outstandingType: String? = "",
        @SerializedName("AccountName")
        var accountName: String? = "",
        @SerializedName("ReceivedAmount")
        var receivedAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("WaiveOffSettledAmount")
        var waiveOffSettledAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PaymentSettledAmount")
        var paymentSettledAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("CurrentDue")
        var currentDue: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("Product")
        var product: String? = "",
        @SerializedName("TaxSubType")
        var taxSubType: String? = "",
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = ""
)