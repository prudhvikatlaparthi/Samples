package com.sgs.citytax.api.response

import com.google.gson.annotations.SerializedName

data class GetCitizenIdentityCardsResponse(
        @SerializedName("CitizenIdentityCardsList")
        val list: List<CitizenIdentityCard>? = null
)