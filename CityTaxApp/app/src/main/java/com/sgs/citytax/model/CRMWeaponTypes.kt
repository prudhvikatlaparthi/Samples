package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMWeaponTypes(
        @SerializedName("WeaponTypeID")
        var weaponTypeID: Int = 0,
        @SerializedName("WeaponType")
        var weaponType: String? = null,
        @SerializedName("WeaponTypeCode")
        var weaponTypeCode: String? = null,
        @SerializedName("prodcode")
        var productCode: String? = null,
        @SerializedName("act")
        var active: String? = null
) {
    override fun toString(): String {
        return weaponType.toString()
    }
}