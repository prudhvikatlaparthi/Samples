package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class PenaltyWaiveOffDetailsTable(
        @SerializedName("PenaltyID")
        var penaltyId: Int? = 0,
        @SerializedName("PenaltyDate")
        var penaltyDate: String? = "",
        @SerializedName("PenaltyName")
        var penaltyName: String? = "",
        @SerializedName("TaxDue")
        var taxDue: Double? = 0.0,
        @SerializedName("pct")
        var penaltyPercent: Double? = 0.0,
        @SerializedName("PenaltyAmount")
        var penaltyAmount: Double? = 0.0,
        @SerializedName("PenaltyDue")
        var penaltyDue: Double? = 0.0,
        @SerializedName("WavedOffAmount")
        var waivedOffAmount: Double? = 0.0,
        @SerializedName("PenaltyDueAfterWavedOff")
        var penaltyDueAfterWaivedOff: Double? = 0.0
)