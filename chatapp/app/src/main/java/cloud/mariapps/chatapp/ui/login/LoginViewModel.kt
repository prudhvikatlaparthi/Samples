package cloud.mariapps.chatapp.ui.login

import cloud.mariapps.chatapp.base.BaseViewModel

class LoginViewModel : BaseViewModel<LoginIntent>() {
    override fun triggerEvent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Authenticate -> {

            }
        }
    }
}