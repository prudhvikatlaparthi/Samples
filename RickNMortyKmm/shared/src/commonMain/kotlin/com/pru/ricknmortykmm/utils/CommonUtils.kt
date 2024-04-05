package com.pru.ricknmortykmm.utils

import com.pru.ricknmortykmm.models.response.ProfileDto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CommonUtils {
    /*object DecimalAsStringSerializer : KSerializer<BigDecimal> {

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("price", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: BigDecimal) {
            encoder.encodeString(value)
        }

        override fun deserialize(decoder: Decoder): BigDecimal {
            val string: String = decoder.decodeString()
            return BigDecimal().asBigDecimal(string) as BigDecimal
        }
    }*/

    fun getProfileString() {
        val pt = ProfileDto(created = "Alpha")
        val st = Json.encodeToString(pt)
        println(st)
        val pst = Json.decodeFromString<ProfileDto>(st)
        println(pst)
    }
}