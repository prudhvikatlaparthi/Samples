package com.pru.navigationcomponentapp

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.pru.navigationcomponentapp.databinding.ThreeFragmentBinding

class ThreeFragment() : BaseFragment(R.layout.three_fragment) {

    private lateinit var threeFragmentBinding: ThreeFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar("Messages", hideActionBar = true, enableDrawer = false)
        threeFragmentBinding = ThreeFragmentBinding.bind(view)

        threeFragmentBinding.fabThree.setOnClickListener {
            val action = ThreeFragmentDirections.actionNavThreeFragmentToFixFragment()
            findNavController().navigate(action)
        }
    }
}