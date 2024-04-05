package com.pru.shopping.androidApp.ui

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.pru.shopping.androidApp.R
import com.pru.shopping.androidApp.ui.activities.MainActivity
import com.pru.shopping.androidApp.ui.fragments.HomeFragment

abstract class BaseFragment(layoutID: Int) : Fragment(layoutID) {
    private var enableDrawer = true
    private var hideActionBar = false
    fun setupToolBar(
        title: String?,
        enableDrawer: Boolean = true,
        hideActionBar: Boolean = false
    ) {
        this.enableDrawer = enableDrawer
        this.hideActionBar = hideActionBar
        drawerCheck()
        hideActionBarCheck(title)
        checkBottomBarView()
    }

    private fun checkBottomBarView() {
        when (this) {
            is HomeFragment -> {
                (requireActivity() as MainActivity).activityBinding.bottomAppBar.isVisible = true
                (requireActivity() as MainActivity).activityBinding.fabCartIcon.isVisible = true
            }
            else -> {
                (requireActivity() as MainActivity).activityBinding.bottomAppBar.isVisible = false
                (requireActivity() as MainActivity).activityBinding.fabCartIcon.isVisible = false
            }
        }
    }

    private fun hideActionBarCheck(title: String? = null) {
        if (hideActionBar) {
            (requireActivity() as MainActivity).supportActionBar?.hide()
        } else {
            (requireActivity() as MainActivity).activityBinding.mytoolbarTitle.text =
                title ?: getString(R.string.app_name)
            (requireActivity() as MainActivity).supportActionBar?.show()
        }
    }

    private fun drawerCheck() {
        if (enableDrawer) {
            openDrawer()
        } else {
            closeDrawer()
        }
    }

    private fun openDrawer() {
        (requireActivity() as MainActivity).unLockNavigationDrawer()
    }

    private fun closeDrawer() {
        (requireActivity() as MainActivity).lockNavigationDrawer()
    }

    override fun onResume() {
        super.onResume()
        drawerCheck()
    }

    override fun onDestroyView() {
        if (!enableDrawer) {
            enableDrawer = !enableDrawer
            drawerCheck()
        }
        if (hideActionBar) {
            hideActionBar = !hideActionBar
            hideActionBarCheck()
        }
        super.onDestroyView()
    }
}