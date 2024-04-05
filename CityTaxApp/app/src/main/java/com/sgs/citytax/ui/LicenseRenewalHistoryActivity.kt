package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GetSearchIndividualTaxDetails
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityBusinessTransactionHistoryBinding
import com.sgs.citytax.databinding.ActivityLicenseRenewalHistoryBinding
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.ui.fragments.BusinessTransactionHistoryFragment
import com.sgs.citytax.ui.fragments.LicenseRenewalHistoryFragment
import com.sgs.citytax.ui.fragments.PropertyTransactionHistoryFragment
import com.sgs.citytax.util.Constant

class LicenseRenewalHistoryActivity : BaseActivity(), FragmentCommunicator {

    private lateinit var binding: ActivityLicenseRenewalHistoryBinding
    private var licenceID: Int = 0
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_LICENSE_RENEW


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_license_renewal_history)
        showToolbarBackButton(R.string.license_renewal_history)
        processIntent()
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun processIntent() {

        intent?.extras?.let {
            if(it.containsKey(Constant.KEY_PRIMARY_KEY)){
                licenceID = it.getInt(Constant.KEY_PRIMARY_KEY, 0)
            }
            if(it.containsKey(Constant.KEY_QUICK_MENU)){
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            }
        }
    }

    private fun attachFragment() {
        val fragment = LicenseRenewalHistoryFragment.newInstance(licenceID,mCode)
        addFragment(fragment, true, R.id.container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {

    }

    override var screenMode: Constant.ScreenMode
        get() = Constant.ScreenMode.VIEW
        set(value) {}
}

