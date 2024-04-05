package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

class TaxSubType(
        @SerializedName("TaxSubType")
        var taxSubType: String? = ""

) {
    override fun toString(): String {
        return taxSubType.toString()
    }
}