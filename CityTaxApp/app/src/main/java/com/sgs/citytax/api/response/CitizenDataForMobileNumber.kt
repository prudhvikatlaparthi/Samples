package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class CitizenDataForMobileNumber(
    @SerializedName("Table" ) var Table : ArrayList<CitizenDataTable>?
)


