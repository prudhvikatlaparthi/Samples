package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteWeapons(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("weaponid")
        var weaponid: Int? = 0
)