package com.pru.responsiveapp.ui.alpha

import android.os.Bundle
import android.view.View
import com.pru.responsiveapp.R
import com.pru.responsiveapp.databinding.FragmentListDetailBinding
import com.pru.responsiveapp.popFragment
import com.pru.responsiveapp.replaceFragment
import com.pru.responsiveapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListDetailFragment : BaseFragment(R.layout.fragment_list_detail) {
    lateinit var fragmentListDetailBinding: FragmentListDetailBinding
    private lateinit var listFragment: ListFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentListDetailBinding = FragmentListDetailBinding.bind(view)
        listFragment = ListFragment()
        setupToolbarTitle(name = "Fruits")
        setupViews()
        setupListeners()
    }

    private fun setupListeners() {
        childFragmentManager.addOnBackStackChangedListener {
            if (childFragmentManager.fragments.size > 0) {
                var fragment =
                    childFragmentManager.fragments[childFragmentManager.fragments.size - 1]
                if (fragment is DetailFragment && isTablet()) {
                    fragment = childFragmentManager.fragments[0]
                }
                if (fragment is ListFragment) {
                    fragment.prepareToolbar()
                }
            }
        }
    }

    private fun setupViews() {
        if (isTablet()) {
            childFragmentManager.popFragment()
            childFragmentManager.replaceFragment(
                fragment = listFragment,
                containerID = fragmentListDetailBinding.listContainer.id
            )
            childFragmentManager.replaceFragment(
                fragment = DetailFragment(),
                containerID = fragmentListDetailBinding.detailContainer!!.id
            )
        } else {
            childFragmentManager.replaceFragment(
                fragment = listFragment,
                containerID = fragmentListDetailBinding.listContainer.id
            )
        }
    }
}