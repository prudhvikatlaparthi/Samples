package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.AssetRentalSpecificationsList

data class AssetPrePostCheckListSummaryResponse(
        @SerializedName("PostCheckList", alternate = ["PreCheckList"])
        var prePostCheckListData:ArrayList<AssetRentalSpecificationsList>? = arrayListOf(),
        @SerializedName("ASWFilePath")
        var aswPath:String?=""
)