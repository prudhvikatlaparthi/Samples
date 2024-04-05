package com.pru.responsiveapp.ui.alpha

import android.os.Bundle
import android.view.View
import com.pru.responsiveapp.R
import com.pru.responsiveapp.searchMenuList
import com.pru.responsiveapp.standardMenuList
import com.pru.responsiveapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment(R.layout.fragment_search) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(title = "Search", showBackButton = true)
        setupToolbar(
            title = "Search",
            showBackButton = true,
            isRequiredOptionMenu = true,
            menuItemClickCallBack = ::menuOptionsCallback,
            menuList = searchMenuList
        )
    }

    fun menuOptionsCallback(item : String){

    }
}