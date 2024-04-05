package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class TicketImpoundDetails(
        @SerializedName("txntypcode")
        var taxTypeCoe: String? = "",
        @SerializedName("txndt")
        var taxDate: String? = "",
        @SerializedName("SettledAmount")
        var settledAmount: Double? = 0.0,
        @SerializedName("NoticeReferenceNo")
        var referanceNumber: String? = "",
        @SerializedName("InvoiceTransactionVoucherNo")
        var voucherNo: Int? = 0,
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
        @SerializedName("ImpoundmentCharge")
        var impoundCharge: Double? = 0.0,
        @SerializedName("CurrentDue")
        var currentDue: Double? = 0.0,
        @SerializedName("DueAfterSettlement")
        var dueAfterSettlement: Double? = 0.0,
        @SerializedName("vehno")
        var vehicleNumber:String?="",
        @SerializedName("VehicleSycotaxID")
        var vehicleSycoTaxID:String?="",
        @SerializedName("ViolatorTypeCode")
        var violatorTypeCode: String? = "",
        @SerializedName("GoodsOwner")
        var goodsOwner: String? = "",
        @SerializedName("GoodsOwnerSycoTaxID")
        var idSycoTax: String? = "",
        @SerializedName("GoodsOwnerCitizenSycotaxID")
        var citizenIDSycotax: String? = "",
        @SerializedName("GoodsOwnerCitizenCardNo")
        var idCardNumber: String? = "",
        @SerializedName("ViolationCharge")
        var violationCharge: Double? = 0.0,
        @SerializedName("TowingCraneType")
        var towingCraneType: String? = "",
        @SerializedName("TowingTripCount")
        var towingTripCount: String? = "",
        @SerializedName("TowingCharge")
        var towingCharge: String? = "",
        @SerializedName("ExtraCharge")
        var extraCharge: Double? = 0.0


)