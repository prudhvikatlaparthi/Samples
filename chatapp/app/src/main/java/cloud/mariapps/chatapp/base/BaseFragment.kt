package cloud.mariapps.chatapp.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import cloud.mariapps.chatapp.model.internal.ToolbarItem
import cloud.mariapps.chatapp.utils.Global.mainActivity
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : Fragment() {

    private var enableDrawer = true
    private var hideActionBar = false
    private var hideBackArrow = false
    private var _binding: VB? = null

    protected val binding: VB
        get() = _binding as VB


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        viewLifecycleOwner.lifecycleScope.launch {
            observers()
        }
        listeners()
    }

    abstract fun setup()

    abstract suspend fun observers()

    abstract fun listeners()

    fun setupToolBar(
        toolbarItem: ToolbarItem,
        isOnlyPortrait : Boolean = false
    ) {
        mainActivity.requestedOrientation =
            if (isOnlyPortrait) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        this.enableDrawer = toolbarItem.enableDrawer
        this.hideActionBar = toolbarItem.hideActionBar
        this.hideBackArrow = toolbarItem.hideBackArrow
        drawerCheck()
        mainActivity.hideActionBarCheck(toolbarItem.title, hideActionBar, hideBackArrow)
    }

    private fun drawerCheck() {
        if (enableDrawer) {
            openDrawer()
        } else {
            closeDrawer()
        }
    }

    private fun openDrawer() {
        mainActivity.unLockNavigationDrawer()
    }

    private fun closeDrawer() {
        mainActivity.lockNavigationDrawer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}