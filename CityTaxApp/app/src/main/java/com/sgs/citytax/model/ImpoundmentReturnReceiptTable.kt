package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ImpoundmentReturnReceiptTable(
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("prod")
        var productName: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("ImpoundmentDate")
        var impoundmentDate: String? = "",
        @SerializedName("ImpoundmentNo")
        var impoundmentNumber: String? = "",
        @SerializedName("ImpoundmentReturnDate")
        var impoundmentReturnDate: String? = "",
        @SerializedName("PoliceStation")
        var policeStation: String? = "",
        @SerializedName("vehno")
        var vehicleNumber: String? = "",
        @SerializedName("VehicleSycotaxID")
        var vehicleSycoTaxId: String? = "",
        @SerializedName("VehicleOwner")
        var vehicleOwner: String? = "",
        @SerializedName("Driver")
        var driver: String? = "",
        @SerializedName("DrivingLicenseNo")
        var drivingLicenseNumber: String? = "",
        @SerializedName("Violator")
        var violator: String? = "",
        @SerializedName("ImpoundFrom")
        var impoundmentFrom: String? = "",
        @SerializedName("mob")
        var mobile: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("zn")
        var zone: String? = "",
        @SerializedName("sec")
        var sector: String? = "",
        @SerializedName("Plot")
        var plot: String? = "",
        @SerializedName("Block")
        var block: String? = "",
        @SerializedName("doorno")
        var doorNo: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("zip")
        var zipCode: String? = "",
        @SerializedName("cty")
        var city: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("lat")
        var lat: String? = "",
        @SerializedName("long")
        var longitude: String? = "",
        @SerializedName("ViolationType")
        var violationType: String? = "",
        @SerializedName("ViolationClass")
        var violationClass: String? = "",
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("ImpoundmentType")
        var impoundmentType: String? = "",
        @SerializedName("ImpoundmentSubType")
        var impoundmentSubType: String? = "",
        @SerializedName("FineAmount")
        var fineAmount: Double? = 0.0,
        @SerializedName("ImpoundmentCharge")
        var impoundmentCharge: Int? = 0,
        @SerializedName("TotalAmount")
        var totalAmound: Double? = 0.0,
        @SerializedName("note")
        var note: String? = "",
        @SerializedName("GeneratedBy")
        var generatedBy: String? = "",
        @SerializedName("BadgeNo")
        var badgeNumber: String? = "",
        @SerializedName("ImpoundedBy")
        var impoundedBy: String? = "",
        @SerializedName("ReturnedBy")
        var returnedBy: String? = "",
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("ReturnRemarks")
        var returnRemarks: String? = "",
        @SerializedName("HandoverImageID")
        var imageId: Int? = 0,
        @SerializedName("OwnerSignatureID")
        var ownerSignatureId: Int? = 0,
        @SerializedName("ReturnAgentSignatureID")
        var returnSigId: Int? = 0,
        @SerializedName("ImpoundmentID")
        var impoundmentID: Int? = 0,
        @SerializedName("HandoverImageAWSPath")
        var handoverImageAWSPath: String? = "",
        @SerializedName("OwnerSignatureAWSPath")
        var ownerSignatureAWSPath: String? = "",
        @SerializedName("ReturnAgentSignatureAWSPath")
        var customerSignatureAWSPath: String? = "",
        @SerializedName("docid")
        var docId: Int? = 0,
        @SerializedName("PrintCounts")
        var printCount: Int? = 0,
        @SerializedName("CitizenSycotaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("VehicleOwnerCitizenCardNo")
        var vehicleOwnerCitizenCardNo: String? = "",
        @SerializedName("VehicleOwnerCitizenSycotaxID")
        var vehicleOwnerCitizenSycotaxID: String? = "",
        @SerializedName("DriverCitizenCardNo")
        var driverCitizenCardNo: String? = "",
        @SerializedName("DriverCitizenSycotaxID")
        var driverCitizenSycotaxID: String? = "",
        @SerializedName("ViolatorCitizenCardNo")
        var violatorCitizenCardNo: String? = "",
        @SerializedName("ViolatorCitizenSycotaxID")
        var violatorCitizenSycotaxID: String? = "",
        @SerializedName("GoodsOwnerSycoTaxID")
        var goodsOwnerSycoTaxID: String? = "",
        @SerializedName("ImpoundFromCitizenSycotaxID" , alternate = ["ImpoundFromCitizenSycotaxId"])
        var impoundFromCitizenSycoTaxID: String? = "",
        @SerializedName("ImpoundFromCitizenCardNo")
        var impoundFromCitizenCardNo: String? = "",
        @SerializedName("GoodsOwner")
        var goodsOwner: String? = "",
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = "",
        @SerializedName("ImpoundFromAccountName")
        var impoundFromAccountName: String? = "",
        @SerializedName("BusinessSycoTaxID")
        var businessSycoTaxID: String? = "",
        @SerializedName("ImpoundFromSycotaxID")
        var impoundFromSycotaxID: String? = "",
        @SerializedName("ImpoundmentReason")
        var impoundmentReason: String? = "",
       @SerializedName("Yard")
        var yard: String? = "",
        var returnedQuantity: BigDecimal = BigDecimal.ZERO,
        @SerializedName("YardID")
        var yardID: Int? = 0,
        @SerializedName("TowingCraneType")
        var towingCraneType: String? = "",
        @SerializedName("TowingTripCount")
        var towingTripCount: Int? = 0,
        @SerializedName("TowingCharge")
        var towingCharge: Double? = 0.0,
        @SerializedName("ExtraCharge")
        var extraCharge: Double? = 0.0,
        @SerializedName("ViolationCharge")
        var violationCharge: Double? = 0.0
)