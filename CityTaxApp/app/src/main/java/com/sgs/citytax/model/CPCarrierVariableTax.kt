package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CPCarrierVariableTax(
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("TaxInvoiceID1")
        var taxInvoiceID1: Int? = 0,
        @SerializedName("vehtyp")
        var vehicleType: String? = "",
        @SerializedName("TarifPerSeat")
        var tarifPerSeat: Double? = 0.000,
        @SerializedName("TotalSeats")
        var totalSeats: Int? = 0,
        @SerializedName("TarifPerTon")
        var tarifPerTon: Double? = 0.000,
        @SerializedName("TotalTonCapacity")
        var totalTonCapacity: Double? = 0.000,
        @SerializedName("TotalAmount")
        var totalAmount: Double? = 0.000
)