package com.sgs.citytax.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AssetListForReturn(
        @SerializedName("AssetRentID")
        var assetRentId: Int? = 0,
        @SerializedName("BookingRequestLineID")
        var bookingrequestLineId: Int? = 0,
        @SerializedName("AssetID")
        var assetId: Int? = 0,
        @SerializedName("AssignDate")
        var assignDate: String? = "",
        @SerializedName("AssetNo")
        var assetNumber: String? = "",
        @SerializedName("AssetSycotaxID")
        var assetSycoTaxId: String? = "",
        @SerializedName("AssetCategoryID")
        var assetCategoryId: Int? = 0,
        @SerializedName("LifeTimeStartDate")
        var lifeTimeStartDate: String? = "",
        @SerializedName("LifeTimeEndDate")
        var lifeTimeEnddDate: String? = "",
        @SerializedName("UserOrgBranchID")
        var ownerOrgBranchId: Int? = 0,
        @SerializedName("BranchName")
        var branchName: String? = "",
        @SerializedName("AssetCategory")
        var assetCategory: String? = "",
        @SerializedName("AssetTypeCode")
        var assetTypeCode: String? = "",
        @SerializedName("AssetType")
        var assetType: String? = "",
        @SerializedName("BookingRequestID")
        var bookingrequestId: Int? = 0,
        @SerializedName("Active")
        var active: String? = "",
        @SerializedName("BookingStartDate")
        var bookingStartDate: String? = "",
        @SerializedName("BookingEndDate")
        var bookingEndDate: String? = "",
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false

)