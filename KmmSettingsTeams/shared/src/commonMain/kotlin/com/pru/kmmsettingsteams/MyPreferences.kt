package com.pru.kmmsettingsteams

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

object MyPreferences {
    private val settings: Settings = Settings()
    private const val kCOUNTERKEY = "counter_key"

    fun setCounterValue(value : Int) {
        settings[kCOUNTERKEY] = value
    }

    fun getCounterValue() :Int {
        return settings[kCOUNTERKEY, 0]
    }

}