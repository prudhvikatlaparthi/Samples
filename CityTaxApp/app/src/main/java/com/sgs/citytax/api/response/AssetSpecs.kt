package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class AssetSpecs(
        @SerializedName("specid")
        var specificationID: Int? = 0,
        @SerializedName("spec")
        var specification: String? = "",
        @SerializedName("datatyp")
        var dataType: String? = "",
        @SerializedName("mand")
        var mandatory: String? = null,
        @SerializedName("ListValues")
        var listValues: String? = null,
        @SerializedName("SpecificationValueID")
        var specificationValueID: Int? = 0,
        @SerializedName("DateValue")
        var dateValue: String? = ""
)