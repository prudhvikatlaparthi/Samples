package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.TaxSubType

class TaxSubTypeListResponse(
        @SerializedName("TaxSubTypeList")
        var taxSubTypeList: ArrayList<TaxSubType> = arrayListOf()
)