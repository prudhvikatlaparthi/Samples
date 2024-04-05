package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class OutstandingVoucherNo(
        @SerializedName("TaxGroupCode")
        var taxGroupCode: String? = null,
        @SerializedName("VoucherNo")
        var voucherNo: Int? = null,
        @SerializedName("TaxSubType")
        var taxSubType: String? = null,
        @SerializedName("AccountID")
        var accountID: Int? = null,
        @SerializedName("Name")
        var name: String? = null
) {
    override fun toString(): String {
        return "$name"
    }
}