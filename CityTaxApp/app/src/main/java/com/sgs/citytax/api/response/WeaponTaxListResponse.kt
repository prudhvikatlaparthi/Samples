package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.payload.AccountPhone
import com.sgs.citytax.api.payload.Asset
import com.sgs.citytax.api.payload.BusinessTaxDueYearSummary
import com.sgs.citytax.model.*

data class WeaponTaxListResponse(
        @SerializedName("IndividualTaxDtls")
        var weaponIndividualTaxDtls: ArrayList<Weapon> = arrayListOf()
)
