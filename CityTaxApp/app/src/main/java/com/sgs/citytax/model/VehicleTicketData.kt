package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class VehicleTicketData(
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("docid")
        var documentID: Int? = 0,
        @SerializedName("geoaddressID")
        var geoAddressId: Int? = 0,
        @SerializedName("ViolatorAccountID")
        var violatorAccountId: Int? = 0,
        @SerializedName("DrivingLicenseNo")
        var drivingLicenseNumber: String? = "",
        @SerializedName("DriverAccountID")
        var driverAccountId: Int? = 0,
        @SerializedName("VehicleOwnerAccountID")
        var vehcleOwnerAccountId: Int? = 0,
        @SerializedName("vehno")
        var vehicleNumber: String? = "",
        @SerializedName("FineAmount")
        var fineAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("PricingRuleID")
        var pricingRuleId: Int? = 0,
        @SerializedName("ViolationTypeID")
        var violationTypeId: Int? = 0,
        @SerializedName("usrorgbrid")
        var userOrgBranchId: Int? = 0,
        @SerializedName("ViolationTicketDate")
        var violationTicketDate: String? = "",
        @SerializedName("ViolationTicketID")
        var violationTicketId: Int? = 0,
        @SerializedName("vehiclemodel")
        var vehicleModel: VehicleModel? = null,
        @SerializedName("ownermodel")
        var ownerModel: OwnerModel? = null,
        @SerializedName("drivermodel")
        var driverModel: CitizenModel? = null,
        @SerializedName("ViolatorModel")
        var violatorModel: CitizenModel? = null
)