package com.sgs.citytax.ui

import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.Fragment
import com.sgs.citytax.util.Constant

interface FragmentCommunicator {
    fun showToolbarBackButton(title: Int)
    fun showToast(message: Int)
    fun showToast(message: String)
    fun showSnackbarMsg(message: Int)
    fun showSnackbarMsg(message: String?)
    fun showAlertDialog(message: String)
    fun showProgressDialog()
    fun finish()
    fun dismissDialog()
    fun popBackStack()
    fun addFragment(fragment: Fragment, addToBackStack: Boolean)
    fun showAlertDialogFailure(message: String, noRecordsFound: Int, onClickListener: DialogInterface.OnClickListener)
    fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    var screenMode: Constant.ScreenMode
}