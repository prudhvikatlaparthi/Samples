package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName

data class TaxTypesDetails(
        @SerializedName("GamingMachineType" ,alternate = ["WeaponType","CartType"])
        var type: String? = "",
        @SerializedName("GamingMachineSycotaxID" ,alternate = ["WeaponSycotaxID","CartSycotaxID"])
        var sycoTaxID: String? ="",
        @SerializedName("Owner")
        var owner: String? ="",
        @SerializedName("serno")
        var serialNo: String? ="",
        @SerializedName("CartNo")
        var number: String? =""


)