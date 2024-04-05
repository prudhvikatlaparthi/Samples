package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMWeaponExemptionReasons(
        @SerializedName("WeaponExemptionReasonID")
        var weaponExemptionReasonID: Int = 0,
        @SerializedName("WeaponExemptionReason")
        var weaponExemptionReason: String? = null,
        @SerializedName("act")
        var active: String? = null
) {
    override fun toString(): String {
        return weaponExemptionReason.toString()
    }
}