package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class PaymentByWallet4TicketPayment(

        var context: SecurityContext? = SecurityContext(),
        @SerializedName("custid")
        var customerId: Int = 0,
        @SerializedName("wallet")
        var wallet: PaymentByWallet? = null,
        @SerializedName("Data")
        var data: ParkingTicketDataWalletPayload? = null,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("makewalletpayment")
        var makewalletpayment: SALWalletPaymentDetails? = null,
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("SearchType")
        var SearchType: String? = "",
        @SerializedName("SearchValue")
        var SearchValue: String? = "",
        @SerializedName("txntypcode")
        var TransactionTypeCode: String? = "",
        @SerializedName("MinPayAmount")
        var MinPayAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("TransactionNo")
        var TransactionNo: Int = 0,
        @SerializedName("ParkingPlaceID")
        var ParkingPlaceID: Int = 0,
        @SerializedName("vehno")
        var vehno: String? = "",
        @SerializedName("ParkingAmount")
        var ParkingAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("data")
        var walletdata: TicketPaymentData? = null

)