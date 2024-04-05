package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BookingRequestDetails(
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
        @SerializedName("EstimatedBookingAmount")
        var estimatedBookingAmount: Double? = 0.0,
        @SerializedName("PaymentCycle")
        var paymentCycle: String? = "",
        @SerializedName("DurationPriceUnitPrice")
        var durationPrice: String? = "",
        @SerializedName("DistancePriceUnitPrice")
        var distancePrice: String? = "",
        @SerializedName("BookingDistance")
        var bookingDistance: Double? = 0.0,
        @SerializedName("BookingAdvance")
        var bookingAdvance: Double? = 0.0,
        @SerializedName("SecurityDeposit")
        var securityDeposit:Double?=0.0,
        @SerializedName("DurationRate")
        var durationRate:String?="",
        @SerializedName("DistanceRate")
        var distanceRate:String?="",
        @SerializedName("ContractTenurePeriod")
        var contractTenurePeriod:String?=null
)