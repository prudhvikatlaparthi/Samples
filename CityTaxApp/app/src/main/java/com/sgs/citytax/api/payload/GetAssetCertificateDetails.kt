package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetAssetCertificateDetails(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("tableorviewname")
        var tableName: String? = null,
        @SerializedName("assetid")
        var assetID: Int? = 0
)