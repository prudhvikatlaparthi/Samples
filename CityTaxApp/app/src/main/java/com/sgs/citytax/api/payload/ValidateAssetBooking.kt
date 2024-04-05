package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class ValidateAssetBooking(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("assetid")
        var assetID: Int? = 0,
        @SerializedName("asstcatid")
        var assetCategoryID: Int? = 0,
        @SerializedName("bookingqty")
        var bookingQuantity: Int? = 0,
        @SerializedName("bkstrtdt")
        var bookingStartDate: String? = "",
        @SerializedName("bkedt")
        var bookingEndDate: String? = "",
        @SerializedName("bkreqlineid")
        var bookingRequestLineID: Int? = 0,
        @SerializedName("usrorgbrid")
        var userOrgBranchID: Int? = 0
)