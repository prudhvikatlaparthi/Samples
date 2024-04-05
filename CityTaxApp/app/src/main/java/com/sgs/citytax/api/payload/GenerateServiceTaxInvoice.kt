package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class GenerateServiceTaxInvoice(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("custid")
        var customerID: Int? = 0,
        @SerializedName("txtypecode")
        var TaxTypeCode: String? = "",
        @SerializedName("txvchrno")
        var voucherNo: Int? = 0,
        @SerializedName("amt")
        var amount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("bycash")
        var isPaymentByCash: Boolean? = false,
        @SerializedName("bywallet")
        var isPaymentByWallet: Boolean? = false,
        @SerializedName("extracharge")
        var extraCharges: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("walletPaymentDetails")
        var walletPaymentDetails: SALWalletPaymentDetails? = null,
        @SerializedName("wallet")
        var wallet: PaymentByWallet? = null,
        @SerializedName("rmks")
        var remarks: String? = null,
        @SerializedName("servicecommission")
        var serviceCommission: ServiceCommission? = null,
)