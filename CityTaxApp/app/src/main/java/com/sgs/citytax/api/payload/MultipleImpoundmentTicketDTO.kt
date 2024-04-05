package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName

class MultipleImpoundmentTicketDTO (
    @SerializedName("ImpoundmentTypeID")
    var impoundmentTypeID:Int = 0,
    @SerializedName("ImpoundmentReason")
    var ImpoundmentReason:String? = null,
    @SerializedName("ImpoundmentCharge")
    var ImpoundmentCharge:Double? = null,
    @SerializedName("PricingRuleID")
    var PricingRuleID:Int? = null,
    @SerializedName("FineAmount")
    var FineAmount:Double? = null,
    @SerializedName("qty")
    var qty:Int? = null,
    @SerializedName("ViolationTypeID")
    var violationTypeID:Int? = null,
    @SerializedName("ViolationDetails")
    var violationDetails:String? = null
)