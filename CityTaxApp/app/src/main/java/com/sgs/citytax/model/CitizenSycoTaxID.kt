package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class CitizenSycoTaxID(
        @SerializedName("CitizenSycotaxID")
        var sycoTaxID: String? = ""
){
        override fun toString(): String {
                return "$sycoTaxID"
        }
}