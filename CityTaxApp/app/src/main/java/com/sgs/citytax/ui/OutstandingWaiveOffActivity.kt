package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GetSearchIndividualTaxDetails
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityOutstandingWaiveOffBinding
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.ui.fragments.OutstandingWaiveOffFragment
import com.sgs.citytax.util.Constant

class OutstandingWaiveOffActivity : BaseActivity(), OutstandingWaiveOffFragment.Listener {
    private lateinit var binding: ActivityOutstandingWaiveOffBinding
    private var accountId = 0
    private var fromScreen: Constant.QuickMenu? = null
    private var getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails? = null
    private var property: VuComProperties? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_outstanding_waive_off)
        showToolbarBackButton(R.string.outstanding_waive_off)
        processIntent()
        attachFragment()
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.QuickMenu
            if (intent.hasExtra(Constant.KEY_ACCOUNT_ID))
                accountId = intent.getIntExtra(Constant.KEY_ACCOUNT_ID, 0)
            if (intent.hasExtra(Constant.KEY_INDIVIDUAL_TAX_DETAILS))
                getSearchIndividualTaxDetails = intent.getParcelableExtra(Constant.KEY_INDIVIDUAL_TAX_DETAILS)

            if (intent.hasExtra(Constant.KEY_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF))
                property = it.getParcelableExtra(Constant.KEY_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF)
        }
    }


    private fun attachFragment() {
        val fragment = OutstandingWaiveOffFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putInt(Constant.KEY_ACCOUNT_ID, accountId)
        bundle.putParcelable(Constant.KEY_INDIVIDUAL_TAX_DETAILS, getSearchIndividualTaxDetails)
        bundle.putParcelable(Constant.KEY_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF, property)
        fragment.arguments = bundle
        addFragment(fragment, false)
    }


    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.outstandingWaiveOffContainer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}