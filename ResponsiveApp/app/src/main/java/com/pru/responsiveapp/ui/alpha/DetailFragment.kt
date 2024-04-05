package com.pru.responsiveapp.ui.alpha

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pru.responsiveapp.R
import com.pru.responsiveapp.databinding.FragmentDetailBinding
import com.pru.responsiveapp.popFragment
import com.pru.responsiveapp.replaceFragment
import com.pru.responsiveapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : BaseFragment(R.layout.fragment_detail) {
    private lateinit var fragmentDetailBinding: FragmentDetailBinding
    private val ldViewModel: LDViewModel by viewModels(ownerProducer = { requireParentFragment() })
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentDetailBinding = FragmentDetailBinding.bind(view)
        setupViews()
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        fragmentDetailBinding.tvEdit?.setOnClickListener {
            if (isTablet()) {
                parentFragmentManager.replaceFragment(
                    isAnimationRequired = true, fragment = CreateFragment(),
                    containerID = R.id.detailContainer
                )
            } else {
                parentFragmentManager.replaceFragment(
                    isAnimationRequired = true, fragment = CreateFragment(),
                    containerID = R.id.listContainer
                )
            }
        }
    }

    private fun setupObservers() {
        ldViewModel.number.observe(viewLifecycleOwner) {
            if (it > -1) {
                ldViewModel.obxData.value?.get(it)?.apply {
                    if (!isTablet()) {
                        setupToolbarTitle(title)
                    }
                    fragmentDetailBinding.numbTxt.text = title + "\n" + description
                }
            }
        }
    }

    private fun setupViews() {
        if (!isTablet()) {
            setupToolbar(
                showBackButton = true,
                isRequiredOptionMenu = true,
                menuList = detailMenuList,
                menuItemClickCallBack = {
                    parentFragmentManager.replaceFragment(
                        isAnimationRequired = true, fragment = CreateFragment(),
                        containerID = R.id.listContainer
                    )
                })
        } else {
            parentFragmentManager.popFragment()
        }
    }
}