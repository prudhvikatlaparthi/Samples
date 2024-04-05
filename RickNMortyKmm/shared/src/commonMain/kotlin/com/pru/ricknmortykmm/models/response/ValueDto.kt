package com.pru.ricknmortykmm.models.response
import com.pru.ricknmortykmm.utils.BigDecimal
import com.pru.ricknmortykmm.utils.CommonUtils
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


@Serializable
data class ValueDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("price")
    val price: Double? = null
)