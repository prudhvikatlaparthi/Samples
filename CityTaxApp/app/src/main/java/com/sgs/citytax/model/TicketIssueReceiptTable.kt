package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class TicketIssueReceiptTable(
        @SerializedName("TaxRuleBookCode")
        var taxRuleBookCode: String? = "",
        @SerializedName("prod")
        var productName: String? = "",
        @SerializedName("TaxInvoiceID")
        var taxInvoiceId: Int? = 0,
        @SerializedName("TaxationYear")
        var taxationYear: String? = "",
        @SerializedName("ViolationDate")
        var violationDate: String? = "",
        @SerializedName("TicketNo")
        var ticketNo: String? = "",
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
        @SerializedName("TicketAmount")
        var ticketAmount: Double? = 0.0,
        @SerializedName("note")
        var note: String? = "",
        @SerializedName("GeneratedBy")
        var generatedBy: String? = "",
        @SerializedName("BadgeNo")
        var badgeNo: String? = "",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0,
        @SerializedName("SignatureID")
        var signatureId: Int? = 0,
        @SerializedName("TransactionVoucherNo")
        var ticketId: Int? = 0,
        var awsPath: String? = null,
        @SerializedName("CitizenSycotaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("DriverCitizenSycotaxID")
        var driverSycoTaxID: String? = "",
        @SerializedName("DriverCitizenCardNo")
        var driverIDCardNumber: String? = "",
        @SerializedName("VehicleOwnerCitizenSycotaxID")
        var vehicleOwnerCitizenSycoTaxID: String? = "",
        @SerializedName("VehicleOwnerCitizenCardNo")
        var vehicleOwnerCitizenCardNo: String? = "",
        @SerializedName("ViolatorCitizenSycotaxID")
        var violatorCitizenSycotaxID: String? = "",
        @SerializedName("ViolatorCitizenCardNo")
        var violatorCitizenCardNo: String? = "",
        @SerializedName("ViolatorBusinessSycoTaxID")
        var violatorBusinessSycoTaxID: String? = "",
        @SerializedName("VehicleOwnerBusinessSycotaxID")
        var vehicleOwnerBusinessSycotaxID: String? = "",
        @SerializedName("FineAmount")
        var fineAmount: Double? = 0.0,
        @SerializedName("ExtraCharge")
        var extraCharge: Double? = 0.0,
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = ""
)