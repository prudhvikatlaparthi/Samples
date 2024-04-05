package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetInsuranceTypes(
        @SerializedName("InsuranceTypeID")
        var insuranceTypeID: Int? = 0,
        @SerializedName("InsuranceType")
        var insuranceType: String? = null
) {
    override fun toString(): String {
        return insuranceType.toString()
    }
}