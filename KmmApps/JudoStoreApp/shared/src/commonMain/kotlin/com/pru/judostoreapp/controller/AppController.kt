package com.pru.judostoreapp.controller

import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object AppController {
    sealed class ControlIntent {
        data object None : ControlIntent()
        data class Navigate(val screen: Screen) : ControlIntent()

        data object PopBackStack : ControlIntent()


        data class ShowLoader(
            var isForInlineProgress: Boolean, var message: String
        ) : ControlIntent()

        data object DismissLoader : ControlIntent()

        data class ShowAlertDialog(var message: String) : ControlIntent()

        data class ShowSnackBar(var message: String?) : ControlIntent()
    }

    private val _navChannel: Channel<ControlIntent> = Channel(
        capacity = Channel.UNLIMITED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val navChannel
        get() = _navChannel.receiveAsFlow()

    /*fun navigate(resId: Int, navOptions: NavOptions?) {
        val controlIntent = ControlIntent.Navigate(resId = resId, navOptions = navOptions)
        _navChannel.trySend(controlIntent)
    }

    fun navigate(directions: NavDirections, navOptions: NavOptions?) {
        val controlIntent =
            ControlIntent.NavigateDirections(directions = directions, navOptions = navOptions)
        _navChannel.trySend(controlIntent)
    }*/

    fun popBackStack() {
        val pop = ControlIntent.PopBackStack
        _navChannel.trySend(pop)
    }

    /*fun popBackStack(destinationId: Int, inclusive: Boolean) {
        val pop = ControlIntent.PopBackStackWithID(
            destinationId = destinationId,
            inclusive = inclusive
        )
        _navChannel.trySend(pop)
    }*/

    fun showLoader(isForInlineProgress: Boolean = true, message: String = "Loading") {
        _navChannel.trySend(
            ControlIntent.ShowLoader(
                isForInlineProgress = isForInlineProgress, message = message
            )
        )
    }

    fun dismissLoader() {
        _navChannel.trySend(ControlIntent.DismissLoader)
    }

    /*fun showAlertDialog(alertItem: AlertItem) {
        _navChannel.trySend(ControlIntent.ShowAlertDialog(alertItem))
    }*/

    fun showSnackBar(message: String?) {
        _navChannel.trySend(ControlIntent.ShowSnackBar(message))
    }

}