package com.pru.shopping.androidApp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pru.shopping.androidApp.databinding.BottomSheetSampleBinding

class SampleBottomSheet : BottomSheetDialogFragment() {

    private lateinit var bottomSheetSampleBinding: BottomSheetSampleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bottomSheetSampleBinding = BottomSheetSampleBinding.inflate(layoutInflater,container,false)

        return bottomSheetSampleBinding.root

    }


}