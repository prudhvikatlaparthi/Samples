package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.GamingMachineTax

data class GameMachineTaxListResponse(
        @SerializedName("IndividualTaxDtls")
        var gameMachineIndividualTaxDtls: ArrayList<GamingMachineTax> = arrayListOf()
)
