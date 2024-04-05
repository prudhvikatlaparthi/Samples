package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AssetRentalDetailsResponse(
        @SerializedName("AssetRentID")
        var assetRentId: Int? = 0,
        @SerializedName("BookingRequestLineID")
        var bookingRequestLineId: Int? = 0,
        @SerializedName("AssetID")
        var assetId: Int? = 0,
        @SerializedName("AssignDate")
        var assignDate: String? = "",
        @SerializedName("OdometerStart")
        var odometerStart: Double? = 0.0,
        @SerializedName("AssignLatitude")
        var assignLatitude: Double? = 0.0,
        @SerializedName("AssignLongitude")
        var assignLongitude: Double? = 0.0,
        @SerializedName("AssignByAccountID")
        var assignByAccountId: Int? = 0,
        @SerializedName("TenurePeriod")
        var tenurePeriod: Int? = 0,
        @SerializedName("DurationPaymentCycleID")
        var durationPaymentCycleId: Int? = 0,
        @SerializedName("DurationPricingRuleID")
        var durationPricingRuleId: Int? = 0,
        @SerializedName("DistancePricingRuleID")
        var distancePricingRuleID: Int? = 0,
        @SerializedName("ReceiveDate")
        var receiveDate: String? = "",
        @SerializedName("OdometerEnd")
        var odometerEnd: Double? = 0.0,
        @SerializedName("ReceiveLatitude")
        var receiveLatitude: Double? = 0.0,
        @SerializedName("ReceiveLongitude")
        var receiveLongitude: Double? = 0.0,
        @SerializedName("ReceiveByAccountID")
        var receivedByAccountId: Int? = 0,
        @SerializedName("Distance")
        var distance: Double? = 0.0,
        @SerializedName("DistanceAmount")
        var distanceAmount: Double? = 0.0,
        @SerializedName("DurationAmount")
        var durationAmount: Double? = 0.0,
        @SerializedName("FineAmount")
        var fineAmount: Double? = 0.0

)