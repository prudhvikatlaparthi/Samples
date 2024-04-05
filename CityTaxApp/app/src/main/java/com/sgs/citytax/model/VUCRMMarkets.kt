package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class VUCRMMarkets(
        @SerializedName("Market")
        var market: String? = null,
        @SerializedName("MarketID")
        var marketID: Int = 0,
        @SerializedName("act")
        var Active: String? = null,
        @SerializedName("code")
        var code: String? = null

) {
    override fun toString(): String {
        return market.toString()
    }
}