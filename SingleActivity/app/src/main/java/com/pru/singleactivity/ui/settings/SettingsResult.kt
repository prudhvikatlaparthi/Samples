package com.pru.singleactivity.ui.settings

import com.pru.singleactivity.base.Result
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsResult(
    var settingId: Int,
    var isFromAddressDialog: Boolean = false
) : Result()
