package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ImpondmentDetails(
        @SerializedName("ImpoundmentID")
        var impoundmentID: Int? = 0,
        @SerializedName("ImpoundmentDate")
        var impoundmentDate: String? = "",
        @SerializedName("ImpoundmentTypeID")
        var impoundmentTypeID: Int? = 0,
        @SerializedName("ImpoundmentSubTypeID")
        var impoundmentSubTypeID: Int? = 0,
        @SerializedName("ImpoundmentReason")
        var impoundmentReason: String? = "",
        @SerializedName("ImpoundmentCharge")
        var impoundmentCharge: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("usrorgbrid")
        var usrorgbrid: Int? = 0,
        @SerializedName("ViolationTypeID")
        var violationTypeID: Int? = 0,
        @SerializedName("PricingRuleID")
        var pricingRuleID: Int? = 0,
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("FineAmount")
        var fineAmount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("vehno")
        var vehicleNo: String? = "",
        @SerializedName("VehicleOwnerAccountID")
        var vehicleOwnerAccountID: Int? = 0,
        @SerializedName("DriverAccountID")
        var driverAccountID: Int? = 0,
        @SerializedName("DrivingLicenseNo")
        var drivingLicenseNo: String? = "",
        @SerializedName("ViolatorAccountID")
        var violatorAccountID: Int? = 0,
        @SerializedName("GoodsOwnerAccountID")
        var goodsOwnerAccountID: Int? = 0,
        @SerializedName("GeoAddressID")
        var geoAddressID: Int? = 0,
        @SerializedName("docid")
        var docid: Int? = 0,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("ImpoundedByAccountID")
        var impoundedByAccountID: Int? = 0,
        @SerializedName("ImpoundmentReturnDate")
        var impoundmentReturnDate: String? = "",
        @SerializedName("HandoverImageID")
        var handoverImageID: String? = "",
        @SerializedName("OwnerSignatureID")
        var ownerSignatureID: String? = "",
        @SerializedName("ReturnAgentSignatureID")
        var returnAgentSignatureID:  String? = "",
        @SerializedName("ReturnRemarks")
        var returnRemarks: String? = "",
        @SerializedName("ReturnedByAccountID")
        var returnedByAccountID: Int? = 0,
        @SerializedName("ViolationTicketID")
        var violationTicketID: Int? = 0,
        @SerializedName("AllowAuction")
        var allowAuction: String? = "",
        @SerializedName("AuctionDate")
        var auctionDate: String? = "",
        @SerializedName("crtd")
        var created: String? = "",
        @SerializedName("crtddt")
        var crtddt: String? = "",
        @SerializedName("ImpoundedByUserID")
        var impoundedByUserID:  String? = "",
        @SerializedName("ReturnedByUserID")
        var returnedByUserID: String? = "",
        @SerializedName("ImpoundmentType")
        var impoundmentType: String? = "",
        @SerializedName("ApplicableOnVehicle")
        var applicableOnVehicle: String? = "",
        @SerializedName("ApplicableOnGoods")
        var applicableOnGoods: String? = "",
        @SerializedName("ImpoundmentSubType")
        var impoundmentSubType: String? = "",
        @SerializedName("PoliceStation")
        var policeStation: String? = "",
        @SerializedName("VehicleOwner")
        var vehicleOwner: String? = "",
        @SerializedName("GoodsOwner")
        var goodsOwner: String? = "",
        @SerializedName("VehicleNo1")
        var vehicleNo1: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceID: Int? = 0,
        @SerializedName("qty")
        var quantity: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("returnQty", alternate = ["ReturnedQuantity"])
        var returnQuantity: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = "",
        @SerializedName("ReturnedAmountPaid")
        var returnedAmountPaid:  BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PendingReturnQuantity")
        var pendingReturnQuantity:  BigDecimal? = BigDecimal.ZERO,
        @SerializedName("PendingReturnAmount")
        var pendingReturnAmount:  BigDecimal? = BigDecimal.ZERO
)


