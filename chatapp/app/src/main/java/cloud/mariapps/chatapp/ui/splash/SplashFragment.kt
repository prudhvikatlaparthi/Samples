package cloud.mariapps.chatapp.ui.splash

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cloud.mariapps.chatapp.base.BaseFragment
import cloud.mariapps.chatapp.databinding.FragmentSplashBinding
import cloud.mariapps.chatapp.model.internal.ToolbarItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : BaseFragment<FragmentSplashBinding>(
    FragmentSplashBinding::inflate
) {
    override fun setup() {
        setupToolBar(
            toolbarItem = ToolbarItem(
                title = null, enableDrawer = true, hideActionBar = true
            )
        )
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            delay(2000)
            val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }

    override suspend fun observers() {

    }

    override fun listeners() {

    }


}