package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class SaveInitialOutstanding(
        var context: SecurityContext = SecurityContext(),
        @SerializedName("data")
        var saveOutstanding: SaveOutstanding? = null
)