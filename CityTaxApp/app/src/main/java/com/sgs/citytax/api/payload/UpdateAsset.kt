package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class UpdateAsset(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("assetid")
        var assetID : Int ? = 0
)