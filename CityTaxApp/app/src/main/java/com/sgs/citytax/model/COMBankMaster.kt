package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class COMBankMaster(
        @SerializedName("bnkid")
        var bankId: Int? = 0,
        @SerializedName("Bank")
        var bankName: String? = "",
        @SerializedName("BankCode")
        var bankCode: String? = ""
) {
        override fun toString(): String {
                return bankName.toString()
        }
}