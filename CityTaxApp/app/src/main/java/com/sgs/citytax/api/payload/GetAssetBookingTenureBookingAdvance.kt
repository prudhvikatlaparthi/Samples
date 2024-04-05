package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetAssetBookingTenureBookingAdvance(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("assetid")
        var assetID: Int? = 0,
        @SerializedName("assetcatid")
        var assetCategoryID: Int? = 0
)