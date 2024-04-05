package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.model.ASTAssetRentPreCheckLists
import com.sgs.citytax.model.ASTAssetRents
import com.sgs.citytax.model.COMDocumentReference

data class AssignAsset(
        var context:SecurityContext = SecurityContext(),
        @SerializedName("assetrental")
        var assetRents:ASTAssetRents?= null,
        @SerializedName("prechklst")
        var assetRentPreCheckLists: ArrayList<ASTAssetRentPreCheckLists> = arrayListOf(),
        @SerializedName("signature")
        var attachment: List<COMDocumentReference> = arrayListOf(),
        @SerializedName("assetrenttypeid")
        var assetRentTypeID: Int? = 0
)