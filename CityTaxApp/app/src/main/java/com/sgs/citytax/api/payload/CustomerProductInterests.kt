package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

data class CustomerProductInterests(
        @SerializedName("CustomerID")
        var customerID: Int = 0,
        @SerializedName("ProductCode")
        var productCode: String = "",
        @SerializedName("StatusCode")
        var statusCode: String = "",
        @SerializedName("Remarks")
        var remarks: String = "",
        @SerializedName("CustomerProductInterestID")
        var customerProductInterestID: Int = 0,
        @SerializedName("Active")
        var active: Char = 'Y'

)