package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ParkingTicketPayloadData(
        @SerializedName("rmks")
        var remakrs: String? = "",
        @SerializedName("amt")
        var amount: Double? = 0.0,
        @SerializedName("VehicleOwnerAccountID")
        var vehicleOwnerAccountId: Int? = 0,
        @SerializedName("vehno")
        var vehicleNo: String? = "",
        @SerializedName("ParkingEndDate")
        var parkingEndDate: String? = "",
        @SerializedName("ParkingStartDate")
        var parkingStartDate: String? = "",
        @SerializedName("OverstayRateCycleID")
        var overStayRateCycleId: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleId: Int? = 0,
        @SerializedName("RateCycleID")
        var rateCycleId: Int? = 0,
        @SerializedName("ParkingTypeID")
        var parkingTypeId: Int? = 0,
        @SerializedName("usrorgbrid")
        var userOrgBranchId: Int? = 0,
        @SerializedName("ParkingPlaceID")
        var parkingPlaceId: Int? = 0,
        @SerializedName("ParkingTicketDate")
        var parkingTicketDate: String? = "",
        @SerializedName("OverstayPricingRuleID")
        var overStayPricingRuleId: Int? = 0,
        @SerializedName("TenurePeriod")
        var tenurePeriod: Int? = 0,
        @SerializedName("ParentParkingTicketID")
        var parentParkingTicketID:Int?=0
)