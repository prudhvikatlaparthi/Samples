package com.pru.judostoreapp.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.pru.judostoreapp.controller.AppController
import com.pru.judostoreapp.pref.Preference
import com.pru.judostoreapp.repo.RepositorySDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import repositorySDK

class AuthModel : ScreenModel {
    var userName by mutableStateOf("")
    var password by mutableStateOf("")

    fun authenticate() {
        screenModelScope.launch(Dispatchers.IO) {
            AppController.showLoader()
            repositorySDK.authenticate(userName = userName, password = password).onSuccess {
                AppController.dismissLoader()
                Preference.userId = it.userId ?: 0
                Preference.isAdmin = it.role?.lowercase() == "admin"
            }.onFailure {
                AppController.dismissLoader()
                AppController.showSnackBar(it.message)
            }
        }
    }

}