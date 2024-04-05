package com.pru.responsiveapp.ui.base

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.pru.responsiveapp.FirebaseAnalyticsLogger
import com.pru.responsiveapp.customviews.MenuView
import com.pru.responsiveapp.data.models.MyMenuItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseFragment(layout: Int) : Fragment(layout) {

    @Inject
    lateinit var firebaseAnalyticsLogger: FirebaseAnalyticsLogger

    fun setupToolbar(
        title: String? = null,
        showBackButton: Boolean = false,
        isRequiredOptionMenu: Boolean = false,
        menuList: List<MyMenuItem>? = null,
        menuItemClickCallBack: ((String) -> Unit)? = null
    ) {
        title?.let {
            setupToolbarTitle(name = it)
        }
        getMainActivity().activityMainBinding.myToolBarBack.isVisible = showBackButton
        if (isRequiredOptionMenu) {
            menuItemClickCallBack?.let {
                menuList?.let { mList ->
                    prepareMenuList(it, mList)
                }
            }
        } else {
            getMainActivity().activityMainBinding.optionsMenu.removeAllViews()
        }
    }

    fun setupToolbarTitle(name: String) {
        getMainActivity().activityMainBinding.myToolBarTitle.text = name
    }


    private fun prepareMenuList(callBack: (String) -> Unit, menuList: List<MyMenuItem>) {
        getMainActivity().activityMainBinding.optionsMenu.removeAllViews()
        getMainActivity().activityMainBinding.optionsMenu.isVisible = true
        menuList.forEach {
            val menuView = MenuView(requireContext(), it, callBack = callBack)
            getMainActivity().activityMainBinding.optionsMenu.addView(menuView)
        }
    }

    private fun getMainActivity(): MainActivity {
        return requireActivity() as MainActivity
    }

    override fun onResume() {
        super.onResume()
        firebaseAnalyticsLogger.sendCurrentScreenEvent(this.javaClass.simpleName)
    }

    fun isTablet(): Boolean = getMainActivity().isTablet()
}