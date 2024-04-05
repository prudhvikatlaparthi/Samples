package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityLicenseRenewalHistoryBinding
import com.sgs.citytax.ui.fragments.ParentPropertyPlanImageFragment
import com.sgs.citytax.util.Constant

class ParentPropertyPlanImagesActivity : BaseActivity(), ParentPropertyPlanImageFragment.Listener {

    private lateinit var binding: ActivityLicenseRenewalHistoryBinding
    private var propertyID: Int = 0
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE
    private var mParentType: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_license_renewal_history)
        processIntent()
        attachFragment()
        if(mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND || mCode == Constant.QuickMenu.QUICK_MENU_CREATE_LAND) {
            when (mParentType) {
                getString(R.string.parent_land_plan_images) -> showToolbarBackButton(R.string.parent_land_plan_images)
                getString(R.string.parent_land_images) -> showToolbarBackButton(R.string.parent_land_images)
                getString(R.string.parent_documents_land) -> showToolbarBackButton(R.string.parent_documents_land)
            }
        } else {
            when (mParentType) {
                getString(R.string.parent_property_plan_documents) -> showToolbarBackButton(R.string.parent_property_plan_documents)
                getString(R.string.parent_property_images) -> showToolbarBackButton(R.string.parent_property_images)
                getString(R.string.parent_land_plan_images) -> showToolbarBackButton(R.string.parent_land_plan_images)
                getString(R.string.parent_land_images) -> showToolbarBackButton(R.string.parent_land_images)
                getString(R.string.parent_documents_property) -> showToolbarBackButton(R.string.parent_documents_property)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun processIntent() {

        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_PRIMARY_KEY)) {
                propertyID = it.getInt(Constant.KEY_PRIMARY_KEY, 0)
            }
            if (it.containsKey(Constant.KEY_QUICK_MENU)) {
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            }
            if(it.containsKey(Constant.KEY_PARENT_TYPE)) {
                mParentType = it.getString(Constant.KEY_PARENT_TYPE,"")
            }
        }
    }

    private fun attachFragment() {
        val fragment = ParentPropertyPlanImageFragment.newInstance(propertyID, mCode, mParentType)
        addFragment(fragment, true, R.id.container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {

    }
}

