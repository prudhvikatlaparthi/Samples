package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BookingAdvanceReceiptDetails(
        @SerializedName("AssetCategory")
        var assetCategory: String? = "",
        @SerializedName("AssetCategoryCode")
        var assetCategoryCode: String? = "",
        @SerializedName("AssetID")
        var assetId: Int? = 0,
        @SerializedName("AssetNo")
        var assetNo: String? = "",
        @SerializedName("BookingQuantity")
        var bookingQuantity: Int? = 0,
        @SerializedName("BookingStartDate")
        var bookingStartDate: String? = "",
        @SerializedName("BookingEndDate")
        var bookingEndDate: String? = "",
        @SerializedName("SecurityDeposit")
        var securityDeposit: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("BookingRequestDate")
        var bookingRequestDate:String?=""
)