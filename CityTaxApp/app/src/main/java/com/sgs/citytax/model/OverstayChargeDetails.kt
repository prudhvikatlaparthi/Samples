package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class OverstayChargeDetails(
        @SerializedName("ParkingTicketID")
        var parkingId: Int? = 0,
        @SerializedName("ParkingPlaceID")
        var parkingPlaceId: Int? = 0,
        @SerializedName("usrorgbrid")
        var userOrgBranchId: Int? = 0,
        @SerializedName("ParkingTypeID")
        var paringTypeId: Int? = 0,
        @SerializedName("RateCycleID")
        var rateCycleId: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleId: Int? = 0,
        @SerializedName("OverstayRateCycleID")
        var overStayRateCycleId: Int? = 0,
        @SerializedName("OverstayPricingRuleID")
        var overStayPricingRuleId: Int? = 0,
        @SerializedName("ParkingTicketDate")
        var parkingDate: String? = "",
        @SerializedName("ParkingStartDate")
        var parkingStartDate: String? = "",
        @SerializedName("ParkingEndDate")
        var parkingEndDate: String? = "",
        @SerializedName("VehicleNo", alternate = ["vehno"])
        var vehicleNo: String? = "",
        @SerializedName("amt")
        var amount: Double? = 0.0,
        @SerializedName("Remarks")
        var remarks: String? = "",
        @SerializedName("TenurePeriod")
        var tenurePeriod: Int? = 0,
        @SerializedName("PricingRule")
        var pricingRule: String? = "",
        @SerializedName("ParkingPlace")
        var parkingPlace: String? = "",
        @SerializedName("ParkingType")
        var parkingType: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("NoticeReferenceNo")
        var referanceNo: String? = "",
        @SerializedName("acctname")
        var accountName: String? = "",
        @SerializedName("netrec")
        var netReceivable: Double? = 0.0,
        @SerializedName("RateCycle")
        var rateCyle: String? = "",
        @SerializedName("OverstayRateCycle")
        var overStayRateCycle: String? = "",
        @SerializedName("OverstayPricingRule")
        var overStayPricingRule: String? = "",
        @SerializedName("VehicleOwnerAccountID")
        var vehicleOwnerAccountId:Int? = 0

)