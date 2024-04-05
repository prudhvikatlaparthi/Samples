package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CRMProfessions(
        @SerializedName("Profession")
        var profession: String? = "",
        @SerializedName("ProfessionCode")
        var professionCode: String? = "",
        @SerializedName("ProfessionID")
        var professionID: Int = 0
) {
    override fun toString(): String {
        return profession.toString()
    }
}