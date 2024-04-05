package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class ProductCategory(
        @SerializedName("cat")
        var category: String = "",
        @SerializedName("catid")
        var categoryID: Int = -1
) {
    override fun toString(): String {
        return category
    }
}