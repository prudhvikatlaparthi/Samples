package com.pru.usersapp

import android.content.SharedPreferences

class Preference(private val sharedPref: SharedPreferences) {

    private fun getPrefString(key: String): String = sharedPref.getString(key, "") ?: ""
    private fun getPrefBoolean(key: String): Boolean = sharedPref.getBoolean(key, false)
    private fun setPref(key: String, value: Boolean) =
        sharedPref.edit().putBoolean(key, value).apply()

    private fun setPref(key: String, value: String?) =
        sharedPref.edit().putString(key, value).apply()

    var isLoggedIn: Boolean
        get() = getPrefBoolean("isLoggedIn")
        set(value) = setPref("isLoggedIn", value)

    var email: String
        get() = getPrefString("email")
        set(value) = setPref("email", value)

}