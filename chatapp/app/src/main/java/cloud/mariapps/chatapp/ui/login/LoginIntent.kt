package cloud.mariapps.chatapp.ui.login

import cloud.mariapps.chatapp.base.BaseIntent

sealed class LoginIntent : BaseIntent() {
    data class Authenticate(val userId: String, val password: String) : LoginIntent()
}