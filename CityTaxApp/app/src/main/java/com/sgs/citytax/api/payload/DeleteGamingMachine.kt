package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class DeleteGamingMachine(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("gamingmachineid")
        var gamingMachineId: Int? = 0
)