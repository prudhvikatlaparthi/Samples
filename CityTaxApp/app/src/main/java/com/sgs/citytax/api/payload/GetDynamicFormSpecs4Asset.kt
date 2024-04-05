package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetDynamicFormSpecs4Asset(
        var context:SecurityContext = SecurityContext(),
        @SerializedName("assetcatid")
        var assetCategoryId: Int? = 0

        /*    @SerializedName("assetcatcode")
            var assetCategoryCode:String?=""*/
)