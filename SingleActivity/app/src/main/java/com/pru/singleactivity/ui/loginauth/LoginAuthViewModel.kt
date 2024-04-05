package com.pru.singleactivity.ui.loginauth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pru.singleactivity.ui.login.LoginArgs
import com.pru.singleactivity.utils.CommonUtils.getArgs

class LoginAuthViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val loginArgs: LoginArgs = getArgs(savedStateHandle)

}