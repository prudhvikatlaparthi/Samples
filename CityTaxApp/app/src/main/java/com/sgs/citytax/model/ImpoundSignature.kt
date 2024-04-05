package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ImpoundSignature(
        @SerializedName("impownersignatureimg")
        var data: String? = "",
        @SerializedName("impownersignatureidfn")
        var fileName: String? = "ImpoundOwnerSignature.jpeg"

)