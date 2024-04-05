package com.pru.shopping.androidApp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.pru.shopping.androidApp.R
import com.pru.shopping.androidApp.adapters.ShopByCategoryAdapter
import com.pru.shopping.androidApp.databinding.FragmentShopByCategoryBinding
import com.pru.shopping.androidApp.ui.BaseFragment
import com.pru.shopping.androidApp.utils.Resource
import com.pru.shopping.androidApp.utils.hide
import com.pru.shopping.androidApp.utils.show
import com.pru.shopping.androidApp.viewmodels.ShopByCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopByCategoryFragment : BaseFragment(R.layout.fragment_shop_by_category) {

    private lateinit var shopByCategoryBinding: FragmentShopByCategoryBinding
    private val shopByCategoryViewModel by viewModels<ShopByCategoryViewModel>()
    private val shopByCategoryAdapter by lazy { ShopByCategoryAdapter(listOf()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shopByCategoryBinding = FragmentShopByCategoryBinding.bind(view)
        setupToolBar("Shop by Category", false, hideActionBar = false)
        shopByCategoryBinding.rcView.apply {
            adapter = shopByCategoryAdapter
        }
        setUpObservers()
    }

    private fun setUpObservers() {
        shopByCategoryViewModel.categoryItems.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Loading -> {
                    shopByCategoryBinding.pbView.show()
                }
                is Resource.Success -> {
                    shopByCategoryBinding.pbView.hide()
                    shopByCategoryAdapter.updateData(it.data)
                }
                is Resource.Error -> {
                    shopByCategoryBinding.pbView.hide()
                }
            }
        })
    }

}