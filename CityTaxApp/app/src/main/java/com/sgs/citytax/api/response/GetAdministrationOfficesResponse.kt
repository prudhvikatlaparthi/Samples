package com.sgs.citytax.api.response


import com.google.gson.annotations.SerializedName

data class GetAdministrationOfficesResponse(
    @SerializedName("Table")
    val getAdministrationOffice: List<GetAdministrationOffice>? = null
)