package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VehicleOwnership(
        @SerializedName("VehicleOwnershipID")
        var vehicleOwnershipID: String? = null,
        @SerializedName("acctid")
        var accountID: String? = null,
        @SerializedName("vehno")
        var vehicleNo: String? = null,
        @SerializedName("4rmdt")
        var fromDate: String? = "",
        @SerializedName("2dt")
        var toDate: String? = ""
)