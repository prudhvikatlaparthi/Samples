package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class COMHotelDesFinances(
    @SerializedName("HotelDesFinanceID")
    var hotelDesFinanceID: Int? = 0,
    @SerializedName("HotelDesFinanceCode")
    var hotelDesFinanceCode: String? = null,
    @SerializedName("HotelDesFinance")
    var hotelDesFinance: String? = null
) {
    override fun toString(): String {
        return hotelDesFinance.toString()
    }
}
