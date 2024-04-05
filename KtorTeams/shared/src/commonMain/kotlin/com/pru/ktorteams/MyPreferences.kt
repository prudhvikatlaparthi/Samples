package com.pru.ktorteams

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

object MyPreferences {
    private val settings: Settings = Settings()
    private const val counterKey = "counterKey"

    fun setCounterKey(value: Int) {
        settings[counterKey] = value
    }

    fun getCounterKey(): Int = settings[counterKey] ?: 0
}