package com.pru.navigationcomponentapp

import android.app.AlertDialog
import androidx.fragment.app.Fragment


abstract class BaseFragment(layoutID: Int) : Fragment(layoutID) {
    private var enableDrawer = true
    private var hideActionBar = false
    private val builder: AlertDialog.Builder by lazy { AlertDialog.Builder(requireContext()) }
    fun setupToolBar(
        title: String?,
        enableDrawer: Boolean = true,
        hideActionBar: Boolean = false
    ) {
        this.enableDrawer = enableDrawer
        this.hideActionBar = hideActionBar
        drawerCheck()
        hideActionBarCheck()
        (requireActivity() as HomeActivity).supportActionBar?.title =
            title ?: getString(R.string.app_name)
    }

    private fun hideActionBarCheck() {
        if (hideActionBar) {
            (requireActivity() as HomeActivity).supportActionBar?.hide()
        } else {
            (requireActivity() as HomeActivity).supportActionBar?.show()
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
        (requireActivity() as HomeActivity).unLockNavigationDrawer()
    }

    private fun closeDrawer() {
        (requireActivity() as HomeActivity).lockNavigationDrawer()
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

    fun showAlertDialog(
        title: String = "Alert",
        message: String,
        positiveButtonName: String,
        positiveListener: () -> Unit,
        negativeButtonName: String,
        negativeListener: () -> Unit
    ) {
        builder.setTitle(title).setMessage(message)
            .setNegativeButton(negativeButtonName){
                dialog, which ->
                negativeListener.let {
                    dialog.dismiss()
                    it()
                }
            }
            .setPositiveButton(
                positiveButtonName
            ) { dialog, which ->
                positiveListener.let {
                    dialog.dismiss()
                    it()
                }
            } .show()
    }
}