package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VehicleTicketHistoryDetails(
        @SerializedName("InvoiceTransactionTypeCode")
        var transactionTypeCode: String? = "",
        @SerializedName("InvoiceTransactionVoucherNo")
        var voucherNo:Int?=0,
        @SerializedName("InvoiceTransactionVoucherDate")
        var transactionDate: String? = "",
        @SerializedName("ViolationType")
        var violationType: String? = "",
        @SerializedName("ViolationClass")
        var violationClass: String? = "",
        @SerializedName("ViolationDetails")
        var violationDetails: String? = "",
        @SerializedName("ImpoundmentType")
        var impoundType: String? = "",
        @SerializedName("ImpoundmentSubType")
        var impoundSubtype: String? = "",
        @SerializedName("ImpoundmentReason")
        var impReason: String? = "",
        @SerializedName("vehno")
        var vehicleNo: String? = "",
        @SerializedName("VehicleSycotaxID")
        var sycoTaxId: String? = "",
        @SerializedName("VehicleOwner")
        var vehicleOwner: String? = "",
        @SerializedName("Driver")
        var driverName: String? = "",
        @SerializedName("drvrmob")
        var driverMobile: String? = "",
        @SerializedName("DrivingLicenseNo")
        var drivingLicenseNumber: String? = "",
        @SerializedName("FineAmount")
        var fineAmount: Double? = 0.0
        )