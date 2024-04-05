package cloud.mariapps.chatapp.ui.home

import androidx.navigation.fragment.findNavController
import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.base.BaseFragment
import cloud.mariapps.chatapp.databinding.FragmentHomeBinding
import cloud.mariapps.chatapp.listeners.OnBackPressListener
import cloud.mariapps.chatapp.model.internal.ToolbarItem
import cloud.mariapps.chatapp.navigation.AppController
import cloud.mariapps.chatapp.ui.composables.AlertItem
import cloud.mariapps.chatapp.utils.Global.mainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
), OnBackPressListener {

    @Inject
    lateinit var appController: AppController

    override fun setup() {
        setupToolBar(toolbarItem = ToolbarItem(title = getString(R.string.chat_group)))
    }

    override suspend fun observers() {

    }

    override fun listeners() {
        binding.fabAdd.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToJobOrderFilterFragment()
            findNavController().navigate(action)
        }
    }

    override fun backPress() {
        appController.showAlertDialog(
            alertItem = AlertItem(message = getString(R.string.are_you_want_to_exit),
                posBtnText = getString(R.string.yes),
                negBtnText = getString(R.string.no),
                posBtnListener = {
                    mainActivity.finish()
                },
                negBtnListener = {})
        )
    }

}