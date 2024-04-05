package com.pru.singleactivity.ui.settings

import com.pru.singleactivity.base.Args
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsArgs(
    var userId: Int,
    var isFromAddressDialog: Boolean = false
) : Args()
