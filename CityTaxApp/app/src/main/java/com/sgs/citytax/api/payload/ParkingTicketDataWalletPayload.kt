package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ParkingTicketDataWalletPayload(
        @SerializedName("rmks")
        var remakrs: String? = "",
        @SerializedName("prodcode")
        var prodCode: String? = "",
        @SerializedName("ParkingAmount")
        var ParkingAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("MinPayAmount")
        var MinPayAmount: BigDecimal = BigDecimal.ZERO,
        @SerializedName("custid")
        var customerID: Int? = 0,
        @SerializedName("vehno")
        var vehicleNo: String? = "",
        @SerializedName("SubTransactionTypeCode")
        var TransactionTypeCode: String? = "",
        @SerializedName("SubTransactionNo")
        var TransactionNo: Int = 0,
        @SerializedName("ParkingPlaceID")
        var ParkingPlaceID: Int = 0

)