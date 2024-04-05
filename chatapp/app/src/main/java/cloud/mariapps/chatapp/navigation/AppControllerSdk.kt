package cloud.mariapps.chatapp.navigation

import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import cloud.mariapps.chatapp.navigation.AppController.ControlIntent
import cloud.mariapps.chatapp.ui.composables.AlertItem
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class AppControllerSdk : AppController {
    override val navChannel_: Channel<ControlIntent> = Channel(
        capacity = Channel.UNLIMITED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val navChannel
        get() = navChannel_.receiveAsFlow()

    override fun navigate(resId: Int, navOptions: NavOptions?) {
        val controlIntent = ControlIntent.Navigate(resId = resId, navOptions = navOptions)
        navChannel_.trySend(controlIntent)
    }

    override fun navigate(directions: NavDirections, navOptions: NavOptions?) {
        val controlIntent =
            ControlIntent.NavigateDirections(directions = directions, navOptions = navOptions)
        navChannel_.trySend(controlIntent)
    }

    override fun popBackStack() {
        val pop = ControlIntent.PopBackStack
        navChannel_.trySend(pop)
    }

    override fun popBackStack(destinationId: Int, inclusive: Boolean) {
        val pop = ControlIntent.PopBackStackWithID(
            destinationId = destinationId,
            inclusive = inclusive
        )
        navChannel_.trySend(pop)
    }

    override fun navigateUp() {
        navChannel_.trySend(ControlIntent.NavigateUp)
    }

    override fun showLoader(isForInlineProgress: Boolean, message: String) {
        navChannel_.trySend(
            ControlIntent.ShowLoader(
                isForInlineProgress = isForInlineProgress,
                message = message
            )
        )
    }

    override fun dismissLoader() {
        navChannel_.trySend(ControlIntent.DismissLoader)
    }

    override fun showAlertDialog(alertItem: AlertItem) {
        navChannel_.trySend(ControlIntent.ShowAlertDialog(alertItem))
    }

}