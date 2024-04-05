package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityBusinessMasterListBinding
import com.sgs.citytax.ui.fragments.BusinessFragment
import com.sgs.citytax.ui.fragments.BusinessSummaryApprovalFragment
import com.sgs.citytax.util.Constant

class BusinessMasterActivity : BaseActivity(), BusinessFragment.Listener, BusinessSummaryApprovalFragment.Listener {

    private lateinit var binding: ActivityBusinessMasterListBinding
    private var businessMode: Constant.BusinessMode = Constant.BusinessMode.None
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_master_list)
        if (intent.hasExtra(Constant.KEY_BUSINESS_MODE))
            businessMode =
                intent.getSerializableExtra(Constant.KEY_BUSINESS_MODE) as? Constant.BusinessMode? ?: Constant.BusinessMode.None

        if(businessMode == Constant.BusinessMode.None){
            showToolbarBackButton(R.string.business_verfication)
        }else{
            showToolbarBackButton(R.string.title_businesses_activation)
        }
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun attachFragment() {
        val bundle = Bundle()
        val businessFragment = BusinessFragment()
        bundle.putSerializable(Constant.KEY_BUSINESS_MODE, businessMode)
        businessFragment.arguments = bundle
        addFragmentWithOutAnimation(businessFragment, true, R.id.businessListContainer)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.businessListContainer)

    override fun onBackPressed() {
        when (currentFragment) {
            is BusinessFragment -> {
                (currentFragment as BusinessFragment).onBackPressed()
                showToolbarBackButton(R.string.menu_business)
            }
            is BusinessSummaryApprovalFragment -> {
                (currentFragment as BusinessSummaryApprovalFragment).onBackPressed()
                showToolbarBackButton(R.string.menu_business)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.businessListContainer)
    }
}
