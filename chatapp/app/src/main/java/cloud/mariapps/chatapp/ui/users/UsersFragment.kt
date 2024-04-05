package cloud.mariapps.chatapp.ui.users

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cloud.mariapps.chatapp.R
import cloud.mariapps.chatapp.base.BaseFragment
import cloud.mariapps.chatapp.databinding.FragmentUsersBinding
import cloud.mariapps.chatapp.model.internal.ToolbarItem
import cloud.mariapps.chatapp.model.internal.User
import cloud.mariapps.chatapp.navigation.AppController
import cloud.mariapps.chatapp.ui.composables.AlertItem
import cloud.mariapps.chatapp.ui.composables.AlertTitle
import cloud.mariapps.chatapp.utils.Constants
import cloud.mariapps.chatapp.utils.Global.getRandomString
import cloud.mariapps.chatapp.utils.Global.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UsersFragment : BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {

    @Inject
    lateinit var appController: AppController

    private val adapter by lazy { UsersAdapter(isViewMode = true) }
    private val userList = List(10) {
        User(userId = it, userName = getRandomString((6..16).random()), isSelected = false)
    }
    private val args by navArgs<UsersFragmentArgs>()

    override fun setup() {
        when (args.fromScreen) {
            Constants.FromScreen.CreateChat -> {
                binding.searchWrapper.isVisible = true
                adapter.updateViewMode(false)
            }
            Constants.FromScreen.AddUsers -> {
                binding.searchWrapper.isVisible = false
                binding.etAddGroupName.isVisible = false
                binding.btnStartChat.text = getString(R.string.done)
                adapter.updateViewMode(false)
            }
            Constants.FromScreen.SelectUsers -> {
                binding.searchWrapper.isVisible = false
                binding.etAddGroupName.isVisible = false
                binding.btnStartChat.text = getString(R.string.add_more_users)
                adapter.updateViewMode(true)
            }
            else -> Unit
        }
        setupToolBar(toolbarItem = ToolbarItem(title = getTitle()))
        adapter.submitList(userList)
        binding.rcvUserSelect.adapter = adapter
    }

    private fun getTitle(): String {
        return when (args.fromScreen) {
            Constants.FromScreen.CreateChat -> getString(R.string.create_group)
            Constants.FromScreen.AddUsers -> getString(R.string.select_users)
            Constants.FromScreen.SelectUsers -> getString(R.string.users)
            else -> ""
        }
    }

    override suspend fun observers() {

    }

    override fun listeners() {
        binding.btnStartChat.setOnClickListener {
            when (args.fromScreen) {
                Constants.FromScreen.CreateChat -> {
                    val selectedUsers = adapter.currentList.filter {
                        it.isSelected
                    }
                    if (selectedUsers.size <= 1) {
                        showToast(getString(R.string.please_select_atleast_2))
                        return@setOnClickListener
                    }
                    appController.showAlertDialog(
                        alertItem = AlertItem(alertTitle = AlertTitle.CONFIRM,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            message = getString(R.string.pls_conf_to_create_grp),
                            posBtnText = getString(R.string.yes),
                            negBtnText = getString(R.string.no),
                            posBtnListener = {
                                val action =
                                    UsersFragmentDirections.actionUsersFragmentToChatScreenFragment()
                                findNavController().navigate(action)
                            },
                            negBtnListener = {})
                    )
                }
                Constants.FromScreen.SelectUsers -> {
                    val action = UsersFragmentDirections.actionUsersFragmentSelf(fromScreen = Constants.FromScreen.AddUsers)
                    findNavController().navigate(action)
                }
                Constants.FromScreen.AddUsers -> {
                    findNavController().popBackStack()
                }
                else -> Unit
            }
        }

        binding.etSearchUser.addTextChangedListener { text ->
            val searchUsers = userList.filter {
                it.userName?.lowercase()?.contains(
                    text?.toString() ?: "$"
                ) == true || it.userId?.toString()?.lowercase()
                    ?.contains(text?.toString() ?: "$") == true
            }
            adapter.submitList(searchUsers)
        }
    }

}