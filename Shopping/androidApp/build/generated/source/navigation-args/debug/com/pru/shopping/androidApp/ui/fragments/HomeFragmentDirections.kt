package com.pru.shopping.androidApp.ui.fragments

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.pru.shopping.androidApp.R
import com.pru.shopping.androidApp.SidemenuGraphDirections

public class HomeFragmentDirections private constructor() {
  public companion object {
    public fun actionHomeFragmentToTodoDetailFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_todoDetailFragment)

    public fun actionGlobalProfileFragment(): NavDirections =
        SidemenuGraphDirections.actionGlobalProfileFragment()

    public fun actionGlobalSampleBottomSheet(): NavDirections =
        SidemenuGraphDirections.actionGlobalSampleBottomSheet()

    public fun actionGlobalHomeFragment(): NavDirections =
        SidemenuGraphDirections.actionGlobalHomeFragment()

    public fun actionGlobalShopByCategoryFragment(): NavDirections =
        SidemenuGraphDirections.actionGlobalShopByCategoryFragment()

    public fun actionGlobalImageViewPagerFragment(): NavDirections =
        SidemenuGraphDirections.actionGlobalImageViewPagerFragment()
  }
}
