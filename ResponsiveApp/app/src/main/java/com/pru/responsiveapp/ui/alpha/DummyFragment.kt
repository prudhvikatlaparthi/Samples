package com.pru.responsiveapp.ui.alpha

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import androidx.navigation.fragment.findNavController
import com.pru.responsiveapp.R
import com.pru.responsiveapp.databinding.FragmentDummyBinding
import com.pru.responsiveapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DummyFragment : BaseFragment(R.layout.fragment_dummy) {
    private lateinit var fragmentDummyBinding: FragmentDummyBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar("Dummy")
        fragmentDummyBinding = FragmentDummyBinding.bind(view)
        fragmentDummyBinding.tap.setOnClickListener {
            findNavController().navigate(R.id.action_dummyFragment_to_listDetailFragment)
        }
        fragmentDummyBinding.tap1.setOnClickListener {
            fragmentDummyBinding.etName.text.clear()
            fragmentDummyBinding.etName.inputType = (InputType.TYPE_CLASS_NUMBER)
//            fragmentDummyBinding.etName.keyListener = DigitsKeyListener.getInstance("0123456789")
            fragmentDummyBinding.etName.filters = arrayOf(
                InputFilter { src, start, end, dst, dstart, dend ->
                    if (src == "") {
                        return@InputFilter src
                    }
                    if (src.toString().matches(Regex("0157"))) {
                        src
                    } else return@InputFilter ""
                }
            )
        }

        fragmentDummyBinding.tap2.setOnClickListener {
            fragmentDummyBinding.etName.text.clear()
            fragmentDummyBinding.etName.inputType = (InputType.TYPE_CLASS_TEXT)
//            fragmentDummyBinding.etName.keyListener = DigitsKeyListener.getInstance("abcdefgh")
            fragmentDummyBinding.etName.filters = arrayOf(
                InputFilter { src, start, end, dst, dstart, dend ->
                    if (src == "") {
                        return@InputFilter src
                    }
                    if (src.toString().matches(Regex("[a-zA-Z]+"))) {
                        src
                    } else ""
                }
            )
        }
    }
}