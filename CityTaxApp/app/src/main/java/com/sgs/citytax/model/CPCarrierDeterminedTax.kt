package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CPCarrierDeterminedTax(
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("vehtyp")
        var vehicleType: String? = "",
        @SerializedName("VehicleCount")
        var vehicleCount: Int? = 0,
        @SerializedName("FixedAmountForVehicleType")
        var fixedAmountForVehicleType: Double? = 0.000,
        @SerializedName("TotalFixedTaxAmount")
        var totalFixedTaxAmount: Double? = 0.000
)