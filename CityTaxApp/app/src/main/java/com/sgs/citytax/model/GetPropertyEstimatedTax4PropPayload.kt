package com.sgs.citytax.model

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.SecurityContext

data class GetPropertyEstimatedTax4PropPayload(@SerializedName("context")
                                               var context: SecurityContext = SecurityContext(),
                                               @SerializedName("proestimatedtax")
                                               var proPropertyEstimatedTax: ProPropertyEstimatedTax? = null)
