package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class ViolationSignature(
        @SerializedName("violownersignatureimg")
        var data: String? = "",
        @SerializedName("violwnersignatureidfn")
        var fileName: String = "ViolationOwnerSignature.jpeg"
)