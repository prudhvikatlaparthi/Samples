package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetCitizenForMobileNumberPayload(
    var context: SecurityContext = SecurityContext(),
    @SerializedName("mobile"  )
    var mobile  : String?  = null,
    @SerializedName("acctid"  )
    var acctid  : Int?     = null

)
