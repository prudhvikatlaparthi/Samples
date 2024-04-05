package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.response.*

data class GetDropdownFiltersForLAWSearchResponse(
        @SerializedName("LAW_ViolationTypes")
        var lAWViolationTypes : List<LAWViolationTypeS>? = null,
        @SerializedName("VU_LAW_ViolationTypes")
        var vULAWViolationSubTypes : List<VULAWViolationSubType>? = null,
        @SerializedName("VU_LAW_ImpoundmentTypes")
        var vULAWImpoundmentTypes: List<VULAWImpoundmentType>? = null,
        @SerializedName("VU_LAW_ImpoundmentSubTypes")
        var vULAWImpoundmentSubTypes: ArrayList<VULAWImpoundmentSubType>? = null
)
