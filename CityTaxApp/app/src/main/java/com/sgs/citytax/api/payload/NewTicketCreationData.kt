package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

class NewTicketCreationData {
    @SerializedName("rmks")
    var remark: String = ""

    @SerializedName("amt")
    var amount: Double = 0.0

    @SerializedName("VehicleOwnerAccountID")
    var VehicleOwnerAccountID: Int? = null

    @SerializedName("vehno")
    var vehicleNo: String = ""

    @SerializedName("ParkingEndDate")
    var parkingEndDate: String = ""

    @SerializedName("ParkingStartDate")
    var parkingStartDate: String = ""

    @SerializedName("OverstayRateCycleID")
    var overstayRateCycleID: Int = 0

    @SerializedName("PricingRuleID")
    var pricingRuleID: Int = 0

    @SerializedName("RateCycleID")
    var rateCycleID: Int = 0

    @SerializedName("ParkingTypeID")
    var parkingTypeID: Int = 0

    @SerializedName("usrorgbrid")
    var userOrgBrId: Int = 0

    @SerializedName("ParkingPlaceID")
    var parkingPlaceID: Int = 0

    @SerializedName("ParkingTicketDate")
    var parkingTicketDate: String = ""

    @SerializedName("OverstayPricingRuleID")
    var overstayPricingRuleID: Int? = null

    @SerializedName("TenurePeriod")
    var tenurePeriod: Int = 0

    @SerializedName("ParentParkingTicketID")
    var parentParkingTicketID: Int? = 0

    @SerializedName("IsPass")
    var isPass: String? = null
}