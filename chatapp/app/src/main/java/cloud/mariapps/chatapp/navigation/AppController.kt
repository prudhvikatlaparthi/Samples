package cloud.mariapps.chatapp.navigation

import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.ui.composables.AlertItem
import cloud.mariapps.chatapp.utils.Global.getString
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

interface AppController {
    val navChannel_: Channel<ControlIntent>

    val navChannel: Flow<ControlIntent>

    fun navigate(resId: Int, navOptions: NavOptions? = null)

    fun navigate(directions: NavDirections, navOptions: NavOptions? = null)

    fun popBackStack()

    fun popBackStack(destinationId: Int, inclusive: Boolean)

    fun navigateUp()

    fun showLoader(
        isForInlineProgress: Boolean = false,
        message: String = getString(R.string.loading)
    )

    fun dismissLoader()

    fun showAlertDialog(alertItem: AlertItem)

    sealed class ControlIntent {
        data class Navigate(@IdRes val resId: Int, val navOptions: NavOptions? = null) :
            ControlIntent()

        data class NavigateDirections(
            val directions: NavDirections,
            val navOptions: NavOptions? = null
        ) : ControlIntent()

        object PopBackStack : ControlIntent()

        data class PopBackStackWithID(@IdRes val destinationId: Int, val inclusive: Boolean) :
            ControlIntent()

        object NavigateUp : ControlIntent()

        data class ShowLoader(
            var isForInlineProgress: Boolean,
            var message: String
        ) : ControlIntent()

        object DismissLoader : ControlIntent()

        data class ShowAlertDialog(var alertItem: AlertItem) : ControlIntent()
    }
}