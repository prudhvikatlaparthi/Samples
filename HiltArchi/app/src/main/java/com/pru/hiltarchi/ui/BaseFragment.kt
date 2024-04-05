package com.pru.hiltarchi.ui

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pru.hiltarchi.listeners.OnBackPressedListener
import com.pru.hiltarchi.ui.fragments.TodoDetailFragment

abstract class BaseFragment(layout: Int) : Fragment(layout) {
    private val builder: AlertDialog.Builder by lazy { AlertDialog.Builder(requireContext()) }


    fun showAlertDialog(
        title: String = "Alert",
        message: String,
        positiveButtonName: String,
        positiveListener: () -> Unit,
        negativeButtonName: String,
        negativeListener: () -> Unit
    ) {
        builder.setTitle(title).setMessage(message)
            .setNegativeButton(negativeButtonName) { dialog, _ ->
                negativeListener.let {
                    dialog.dismiss()
                    it()
                }
            }
            .setPositiveButton(
                positiveButtonName
            ) { dialog, _ ->
                positiveListener.let {
                    dialog.dismiss()
                    it()
                }
            }.show()
    }


}