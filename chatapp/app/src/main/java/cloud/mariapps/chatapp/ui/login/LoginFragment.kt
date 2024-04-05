package cloud.mariapps.chatapp.ui.login

import androidx.navigation.fragment.findNavController
import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.base.BaseFragment
import cloud.mariapps.chatapp.databinding.FragmentLoginBinding
import cloud.mariapps.chatapp.model.internal.ToolbarItem
import cloud.mariapps.chatapp.utils.Global
import cloud.mariapps.chatapp.utils.Global.getString

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {

    override fun setup() {
        setupToolBar(toolbarItem = ToolbarItem(
            title = Global.getString(R.string.app_name),
            enableDrawer = false,
            hideActionBar = false,
            hideBackArrow = true
        ))
    }

    override suspend fun observers() {

    }

    override fun listeners() {
        binding.btnLogin.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }
}