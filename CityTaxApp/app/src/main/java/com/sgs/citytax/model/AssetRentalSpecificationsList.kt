package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class AssetRentalSpecificationsList(
        @SerializedName("AssetRentID")
        var assetRentId: Int? = 0,
        @SerializedName("specid")
        var specificationID:Int? = 0,
        @SerializedName("datatyp",alternate = ["DataType"])
        var dataType: String? = "",
        @SerializedName("spec",alternate = ["Specification"])
        var specification: String? = "",
        @SerializedName("mand")
        var mandatory: String? = "",
        @SerializedName("val",alternate = ["Value"])
        var value: String? = "",
        @SerializedName("DateValue")
        var dateValue: String? = "",
        @SerializedName("SpecificationValueID")
        var specificationValueID: Int? = 0,
        @SerializedName("ListValues")
        var listValues: String? = "",
        @SerializedName("SpecificationValue")
        var specificationValue: String? = ""
)