package com.pru.responsiveapp.ui.alpha

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.pru.responsiveapp.R
import com.pru.responsiveapp.createMenuList
import com.pru.responsiveapp.data.models.DataItem
import com.pru.responsiveapp.databinding.FragmentCreateBinding
import com.pru.responsiveapp.popFragment
import com.pru.responsiveapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFragment : BaseFragment(R.layout.fragment_create) {
    private lateinit var fragmentCreateBinding: FragmentCreateBinding
    private val ldViewModel: LDViewModel by viewModels(ownerProducer = {requireParentFragment()})
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCreateBinding = FragmentCreateBinding.bind(view)
//        ldViewModel = ViewModelProvider(requireParentFragment()).get(LDViewModel::class.java)
        setupToolbar(
            "Create Fruit",
            showBackButton = true,
            isRequiredOptionMenu = true,
            menuList = createMenuList
        ) {
            fragmentCreateBinding.button.performClick()
        }
        fragmentCreateBinding.button.setOnClickListener {
            val dataItem = DataItem(
                title = fragmentCreateBinding.etTitle.text.toString(),
                description = fragmentCreateBinding.etDescr.text.toString()
            )
            ldViewModel.updateData(dataItem)
            parentFragmentManager.popFragment()
        }
    }
}