package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class UMXUserPolicyDetails(
        @SerializedName("Accepted")
        var accepted: Char? = 'N',
        @SerializedName("usrid")
        var userId: String? = "",
        @SerializedName("VersionNo")
        var versionNo: String? = ""

)