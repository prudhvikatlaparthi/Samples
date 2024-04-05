package com.pru.judostoreapp.pref

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

object Preference : Settings by Settings() {

    var userId: Int
        get() = this["UserId"] ?: 0
        set(value) {
            this["UserId"] = value
        }

    var cartId: Int
        get() = this["CartId"] ?: 0
        set(value) {
            this["CartId"] = value
        }

    var isAdmin: Boolean
        get() = this["IsAdmin"] ?: false
        set(value) {
            this["IsAdmin"] = value
        }
}