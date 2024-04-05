package com.pru.shopping.androidApp

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections

public class SidemenuGraphDirections private constructor() {
  public companion object {
    public fun actionGlobalProfileFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_profileFragment)

    public fun actionGlobalSampleBottomSheet(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_sampleBottomSheet)

    public fun actionGlobalHomeFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_homeFragment)

    public fun actionGlobalShopByCategoryFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_shopByCategoryFragment)

    public fun actionGlobalImageViewPagerFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_imageViewPagerFragment)
  }
}
