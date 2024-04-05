package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ImpoundmentReceiptTable(
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
        var impoundmentCharge: Double? = 0.0,
        @SerializedName("ImpoudmentTarif")
        var impoudmentTarif: String? = null,
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
        @SerializedName("HandoverImageID")
        var imageId: Int? = 0,
        @SerializedName("OwnerSignatureID")
        var ownerSignatureId: Int? = 0,
        @SerializedName("ReturnAgentSignatureID")
        var returnSigId: Int? = 0,
        @SerializedName("docid")
        var docId: Int? = 0,
        @SerializedName("PrintCounts")
        var printCount: Int? = 0,
        @SerializedName("SignatureID")
        var signatureId: Int? = 0,
        var awsPath: String? = null,
        @SerializedName("CitizenSycotaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("DriverCitizenSycotaxID")
        var driverCitizenSycoTaxID: String? = "",
        @SerializedName("DriverCitizenCardNo")
        var driverCitizenCardNo: String? = "",
        @SerializedName("VehicleOwnerCitizenSycotaxID")
        var vehicleOwnerCitizenSycotaxID: String? = "",
        @SerializedName("VehicleOwnerCitizenCardNo")
        var vehicleOwnerCitizenCardNo: String? = "",
        @SerializedName("ViolatorCitizenSycotaxID")
        var violatorCitizenSycotaxID: String? = "",
        @SerializedName("ViolatorCitizenCardNo")
        var violatorCitizenCardNo: String? = "",
        @SerializedName("ViolatorBusinessSycoTaxID")
        var violatorBusinessSycoTaxID: String? = "",
        @SerializedName("GoodsOwner")
        var goodsOwner: String? = "",
        @SerializedName("GoodsOwnerSycoTaxID")
        var goodsOwnerSycoTaxID: String? = "",
        @SerializedName("ImpoundFromCitizenCardNo")
        var impoundFromCitizenCardNo: String? = "",
        @SerializedName("ImpoundFromCitizenSycotaxID")
        var impoundFromCitizenSycotaxID: String? = "",
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = "",
        @SerializedName("AnimalOwner")
        var animalOwner: String? = "",
        @SerializedName("ImpoundmentReason")
        var impoundmentReason: String? = "",
        @SerializedName("qty")
        var impoundQuantity: String? = "",
        @SerializedName("ViolationCharge")
        var violationCharge: Double? = 0.0,
        @SerializedName("ImpoundFromAccount")
        var impoundFromAccount: String? = "",
        @SerializedName("AnimalImpoundFromCitizenSycotaxID")
        var impoundFromCitizenSycotaxID1: String? = "",
        @SerializedName("AnimalImpoundFromCitizenCardNo")
        var impoundFromCitizenCardNo1: String? = "",
        @SerializedName("AnimalImpoundFromSycotaxID")
        var impoundFromSycotaxID: String? = "",
        @SerializedName("Yard")
        var yard: String? = "",
        @SerializedName("TowingCraneType")
        var towingCraneType: String? = "",
        @SerializedName("TowingTripCount")
        var towingTripCount: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("TowingCharge")
        var towingCharge: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("ExtraCharge")
        var extraCharge: BigDecimal? = BigDecimal.ZERO,

)