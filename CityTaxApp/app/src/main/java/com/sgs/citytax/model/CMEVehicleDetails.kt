package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CMEVehicleDetails(
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("vehtyp")
        var vehicleType: String? = "",
        @SerializedName("vehno")
        var vehicleNo: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("ActivityClass")
        var activityClass: String? = "",
        @SerializedName("VehicleTaxAmount")
        var vehicleAmount: Double? = 0.0
)