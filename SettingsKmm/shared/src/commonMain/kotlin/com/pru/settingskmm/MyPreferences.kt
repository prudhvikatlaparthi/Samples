package com.pru.settingskmm

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

object MyPreferences {
    private val settings: Settings = Settings()

    // Keys
    private const val kName = "KName"


    
    fun saveName(name: String?) {
        settings[kName] = name
    }

    fun getName(): String? = settings[kName]

}
