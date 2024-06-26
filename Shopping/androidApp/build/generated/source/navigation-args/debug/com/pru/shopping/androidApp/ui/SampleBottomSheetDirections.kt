package com.pru.shopping.androidApp.ui

import androidx.navigation.NavDirections
import com.pru.shopping.androidApp.SidemenuGraphDirections

public class SampleBottomSheetDirections private constructor() {
  public companion object {
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
