package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetImpondmentReturnHistory(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("fltrtype")
        var filterType: String? = "",
        @SerializedName("fltrstr")
        var filterString: String? = "",
        @SerializedName("onlydue")
        var onlydue: String? = "Y"
)