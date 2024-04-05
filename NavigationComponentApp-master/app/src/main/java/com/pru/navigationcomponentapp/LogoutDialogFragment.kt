package com.pru.navigationcomponentapp

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pru.navigationcomponentapp.databinding.DialogLogoutFragmentBinding

class LogoutDialogFragment : BottomSheetDialogFragment() {

    private lateinit var dialogLogoutFragmentBinding: DialogLogoutFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialogLogoutFragmentBinding = DialogLogoutFragmentBinding.inflate(layoutInflater,container,false)

        dialogLogoutFragmentBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        dialogLogoutFragmentBinding.btnYes.setOnClickListener {
        dismiss()
            requireActivity().finish()
        }

        return dialogLogoutFragmentBinding.root
    }
}