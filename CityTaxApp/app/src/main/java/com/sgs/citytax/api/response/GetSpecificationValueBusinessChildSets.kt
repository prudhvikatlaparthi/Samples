package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.CartTax
import com.sgs.citytax.model.GamingMachineTax
import com.sgs.citytax.model.Weapon

data class GetSpecificationValueBusinessChildSets(
        @SerializedName("VU_CRM_Weapons")
        var VU_CRM_Weapons: List<Weapon> = arrayListOf(),
        @SerializedName("VU_CRM_GamingMachines")
        var VU_CRM_GamingMachines: List<GamingMachineTax> = arrayListOf(),
        @SerializedName("VU_CRM_Carts")
        var VU_CRM_Carts: List<CartTax> = arrayListOf()


)