package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class AssetBookingEstimatedPrice(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("assetcatid")
        var assetCategoryID: Int? = 0,
        @SerializedName("qty")
        var quantity: Int? = 0,
        @SerializedName("assetid")
        var assetID: Int? = 0,
        @SerializedName("stdt")
        var startDate: String? = "",
        @SerializedName("edt")
        var endDate: String? = "",
        @SerializedName("tenure")
        var tenure: Int? = 0,
        @SerializedName("distance")
        var distance: Int? = 0,
        @SerializedName("assetrenttypeid")
        var rentTypeID: Int? = 0,
        @SerializedName("area")
        var area: Int? = null
)