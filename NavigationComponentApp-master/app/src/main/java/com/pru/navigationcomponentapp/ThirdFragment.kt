package com.pru.navigationcomponentapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pru.navigationcomponentapp.databinding.FragmentThirdBinding

class ThirdFragment : BaseFragment(R.layout.fragment_third) {

    private var _fragmentThirdBinding: FragmentThirdBinding? = null

    private val fragmentThirdBinding get() = _fragmentThirdBinding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupToolBar("Search")
        _fragmentThirdBinding = FragmentThirdBinding.inflate(layoutInflater, container, false)


        return fragmentThirdBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentThirdBinding = null
    }
}