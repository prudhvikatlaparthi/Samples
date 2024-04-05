package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class LastParkingTicketDetails(
        @SerializedName("ParkingTicketDate")
        var parkingDate: String? = "",
        @SerializedName("ParkingStartDate")
        var parkingStartDate: String? = "",
        @SerializedName("ParkingEndDate")
        var parkingEndDate: String? = "",
        @SerializedName("vehno")
        var vehicleNo: String? = "",
        @SerializedName("amt")
        var amount: Double? = 0.0,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("ParkingPlace")
        var parkingPlace: String? = "",
        @SerializedName("ParkingType")
        var parkingType: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("NoticeReferenceNo")
        var referanceNo: String? = "",
        @SerializedName("netrec")
        var netReceivable: Double? = 0.0,
        @SerializedName("RateCycle")
        var rateCyle: String? = "",
        @SerializedName("ParkingTicketID")
        var parkingTicketID: Int? = 0,
        @SerializedName("CurrentDue")
        var currentDue:Double?=0.0,
        @SerializedName("IsPass")
        var isPass: String? = ""
)