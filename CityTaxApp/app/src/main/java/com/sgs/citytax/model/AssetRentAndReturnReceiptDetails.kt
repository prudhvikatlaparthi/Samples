package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AssetRentAndReturnReceiptDetails(
        @SerializedName("BookingRequestID")
        var bookingRequestId: Int? = 0,
        @SerializedName("BookingRequestDate")
        var bookingRequestDate: String? = "",
        @SerializedName("AssetRentID")
        var assetRentId: Int? = 0,
        @SerializedName("AssignDate")
        var assignDate: String? = "",
        @SerializedName("ReceiveDate")
        var receivedDate: String? = "",
        @SerializedName("AssetNo")
        var assetNo: String? = "",
        @SerializedName("AssetSycotaxID")
        var assetSycoTaxId: String? = "",
        @SerializedName("OdometerStartDate")
        var odometerStartDate: String? = "",
        @SerializedName("OdometerEndDate")
        var odometerEndDate: String? = "",
        @SerializedName("OdometerStart")
        var odometerStart: Double? = 0.0,
        @SerializedName("OdometerEnd")
        var odometerEnd: Double? = 0.0,
        @SerializedName("TaxationYear")
        var taxationYear: Int? = 0,
        @SerializedName("AssetAssignTo")
        var assignAssetTo: String? = "",
        @SerializedName("Number")
        var phoneNumber: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("st")
        var state: String? = "",
        @SerializedName("cty")
        var city: String? = "",
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
        @SerializedName("AssignedBy")
        var assignedBy: String? = "",
        @SerializedName("recdby")
        var receivedBy: String? = "",
        @SerializedName("FineAmount")
        var fineAmount: Double? = 0.0,
        @SerializedName("rmks")
        var remarks: String? = "",
        @SerializedName("CitizenSycotaxID")
        var citizenSycoTaxId: String? = "",
        @SerializedName("CitizenCardNo")
        var citizenCardNumber: String? = "",
        @SerializedName("SycoTaxID")
        var sycoTaxID: String?="",
        @SerializedName("PrintCounts")
        var printCounts: Int? = 0
)