package com.sgs.citytax.model

data class RateCycle(
        var rateCycleID: Int? = 0,
        var rateCycle: String? = ""
) {
    override fun toString(): String {
        return rateCycle ?: ""
    }
}