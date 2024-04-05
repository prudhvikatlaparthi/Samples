package com.pru.shopping.androidApp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.pru.shopping.androidApp.R
import com.pru.shopping.androidApp.databinding.FragmentWelcomeBinding
import com.pru.shopping.androidApp.ui.BaseFragment

class WelcomeFragment : BaseFragment(R.layout.fragment_welcome) {
    private lateinit var fragmentWelcomeBinding: FragmentWelcomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentWelcomeBinding = FragmentWelcomeBinding.bind(view)
        setupToolBar("", false, true)
        setupListener()
    }

    private fun setupListener() {
        fragmentWelcomeBinding.signIn.setOnClickListener {
            /*val action = WelcomeFragmentDirections.actionWelcomeFragmentToFragmentHome()
            findNavController().navigate(action)*/
        }

        fragmentWelcomeBinding.signUp.setOnClickListener {
            val a = 10/0
        }
    }
}