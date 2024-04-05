package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetImpondmentReturnList(
        @SerializedName("Data")
        var getImpondmentReturnList: List<ImpondmentReturn> = arrayListOf()

)