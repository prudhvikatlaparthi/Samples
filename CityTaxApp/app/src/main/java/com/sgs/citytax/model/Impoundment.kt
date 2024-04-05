package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.base.MyApplication
import java.math.BigDecimal

data class Impoundment(
        @SerializedName("ImpoundmentDate")
        var impoundmentDate: String? = "",
        @SerializedName("ImpoundmentTypeID")
        var impoundmentTypeID: Int? = 0,
        @SerializedName("ImpoundmentSubTypeID")
        var impoundmentSubTypeID: Int? = 0,
        @SerializedName("ImpoundmentID")
        var impoundmentID: Int? = 0,
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0,
        @SerializedName("DriverAccountID")
        var driverAccountID: Int? = 0,
        @SerializedName("VehicleOwnerAccountID")
        var vehicleOwnerAccountID: Int? = 0,
        @SerializedName("GoodsOwnerAccountID")
        var goodsOwnerAccountID: Int? = 0,
        @SerializedName("ImpoundFromAccountID")
        var impoundFromAccountID: Int? = 0,
        @SerializedName("FineAmount")
        var fineAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("GoodsValuation")
        var goodsValuation: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ImpoundmentCharge")
        var impoundmentCharge: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("ImpoundmentReason")
        var impoundmentReason: String? = "",
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("vehno")
        var vehicleNo: String? = null,
        @SerializedName("DrivingLicenseNo")
        var drivingLicenseNo: String? = "",
        @SerializedName("ViolationTypeID")
        var violationTypeID: Int? = 0,
        @SerializedName("GeoAddress")
        var geoAddress: GeoAddress? = GeoAddress(),
        @SerializedName("ImpoundedByAccountID")
        var impoundedByAccountID: Int? = MyApplication.getPrefHelper().accountId,
        @SerializedName("ImpoundedByUserID")
        var impoundedByUserID: String? = MyApplication.getPrefHelper().loggedInUserID,
        @SerializedName("qty")
        var quantity: Int? = 0,
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = "",
        @SerializedName("vehiclemodel")
        var vehicleModel: VehicleModel? = null,
        @SerializedName("ownermodel")
        var ownerModel: OwnerModel? = null,
        @SerializedName("drivermodel")
        var driverModel: CitizenModel? = null,
        @SerializedName("animalowner")
        var animalModel: CitizenModel? = null,
        @SerializedName("impoundfrom")
        var impoundFrom: CitizenModel? = null
)