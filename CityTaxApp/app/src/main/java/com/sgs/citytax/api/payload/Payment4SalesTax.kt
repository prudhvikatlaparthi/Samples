package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Payment4SalesTax(
        @SerializedName("custid")
        var customerId: Int = 0,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("qty")
        var quantity: Int = 0,
        @SerializedName("unitprc")
        var unitPrice: BigDecimal = BigDecimal.ZERO,
        @SerializedName("FinalPrice")
        var finalPrice: BigDecimal = BigDecimal.ZERO,
        @SerializedName("ValidUptoDate")
        var validUpToDate: String? = null,
        @SerializedName("IsPaymentByCash")
        var isPaymentByCash: Boolean = false,
        @SerializedName("IsPaymentByWallet")
        var isPaymentByWallet: Boolean = false,
        @SerializedName("IsPaymentByCheque")
        var isPaymentByCheque: Boolean = false,
        @SerializedName("walletPaymentDetails")
        var walletPaymentDetails: SALWalletPaymentDetails? = null,
        @SerializedName("wallet")
        var wallet: PaymentByWallet? = null,
        @SerializedName("rmks")
        var remarks: String? = null,
        @SerializedName("ChequeDetails")
        var chequeDetails: ChequeDetails ?= null,
        @SerializedName("filewithext")
        var filenameWithExt: String? = "",
        @SerializedName("dtsrc")
        var fileData: String? = "",
        @SerializedName("chequeamt")
        var chequeamt: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("vchrno")
        var voucherNo: Int? = 0
)