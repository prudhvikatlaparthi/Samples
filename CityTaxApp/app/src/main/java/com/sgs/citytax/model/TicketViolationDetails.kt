package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class TicketViolationDetails(
        @SerializedName("txntypcode")
        var taxTypeCoe: String? = "",
        @SerializedName("TransactionNo")
        var transactionNumber: Int? = 0,
        @SerializedName("txndt")
        var taxDate: String? = "",
        @SerializedName("SettledAmount")
        var settledAmount: Double? = 0.0,
        @SerializedName("NoticeReferenceNo")
        var referanceNumber: String? = "",
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
        var impSubType: String? = "",
        @SerializedName("ImpoundmentReason")
        var impReason: String? = "",
        @SerializedName("FineAmount")
        var fineAmount: Double? = 0.0,
        @SerializedName("vehno")
        var vehicleNumber:String?="",
        @SerializedName("CurrentDue")
        var currentDue: Double? = 0.0,
        @SerializedName("DueAfterSettlement")
        var dueAfterSettlement: Double? = 0.0,
        @SerializedName("VehicleSycotaxID")
        var vehicleSycoTaxID:String?="",
        @SerializedName("ExtraCharge")
        var extraCharge:Double?=0.0,
        @SerializedName("amt")
        var totalCharges:Double?=0.0,
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = "",
        @SerializedName("ViolatorCitizenCardNo")
        var violatorCitizenCardNo: String? = "",
        @SerializedName("ViolatorCitizenSycotaxID")
        var violatorCitizenSycotaxID: String? = ""

)