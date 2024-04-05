package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import java.math.BigDecimal

data class TicketPaymentData(
        @SerializedName("custid")
        var customerId: Int = 0,
        @SerializedName("CashAmount")
        var cashAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("vchrno")
        var voucherNo: Int? = 0,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("SearchType")
        var SearchType: String? = "",
        @SerializedName("SearchValue")
        var SearchValue: String? = "",
        @SerializedName("SubTransactionTypeCode")
        var TransactionTypeCode: String? = "",
        @SerializedName("MinPayAmount")
        var MinPayAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("SubTransactionNo")
        var TransactionNo: Int = 0,
        @SerializedName("ParkingPlaceID")
        var ParkingPlaceID: Int = 0,
        @SerializedName("vehno")
        var vehno: String? = "",
        @SerializedName("ParkingAmount")
        var ParkingAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("qty")
        var qty: Int? = null,
        @SerializedName("filewithext")
        var filenameWithExt: String? = null,
        @SerializedName("dtsrc")
        var fileData: String? = null,

)