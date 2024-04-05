package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AllocatedStock(
        @SerializedName("allocdt")
        var allocationDate: String? = "",
        @SerializedName("prod")
        var product: String? = "",
        @SerializedName("prodcode")
        var productCode: String? = "",
        @SerializedName("FromAccountName")
        var fromAccountName: String? = "",
        @SerializedName("ToAccountName")
        var toAccountName: String? = "",
        @SerializedName("qty")
        var quantity: String? = "",
        @SerializedName("Photo")
        var photo: String? = ""
)