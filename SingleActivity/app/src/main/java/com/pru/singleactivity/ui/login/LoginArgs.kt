package com.pru.singleactivity.ui.login

import com.pru.singleactivity.base.Args
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginArgs(
    var email: String
) : Args()