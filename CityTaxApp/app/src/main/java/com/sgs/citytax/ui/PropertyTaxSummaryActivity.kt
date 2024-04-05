package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityIndividualTaxSummaryBinding
import com.sgs.citytax.ui.fragments.LandTaxSummaryFragment
import com.sgs.citytax.ui.fragments.PropertyTaxSummaryFragment
import com.sgs.citytax.util.Constant

class PropertyTaxSummaryActivity : BaseActivity(), PropertyTaxSummaryFragment.Listener, LandTaxSummaryFragment.Listener {

    private lateinit var binding: ActivityIndividualTaxSummaryBinding
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE
    private var PropertySycoTaxID: String? = ""
    private var taxRuleBookCode: String? = ""
    private var PropertyID: Int? = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_individual_tax_summary)
        processIntent()
        showToolbarBackButton(R.string.title_property_tax_summary)
        attachFragment()
    }

    private fun processIntent() {
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_TAX_RULE_BOOK_CODE))
                taxRuleBookCode = it.getString(Constant.KEY_TAX_RULE_BOOK_CODE)
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                PropertySycoTaxID = it.getString(Constant.KEY_SYCO_TAX_ID)
            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                PropertyID = it.getInt(Constant.KEY_PRIMARY_KEY)
        }
    }


    private fun attachFragment() {
        if (taxRuleBookCode == Constant.TaxRuleBook.LAND_PROP.Code) {
            showToolbarBackButton(R.string.title_land_tax_summary)
            val fragment = LandTaxSummaryFragment()
            val bundle = Bundle()
            bundle.putInt(Constant.KEY_PRIMARY_KEY, PropertyID ?: 0)
            fragment.arguments = bundle
            addFragmentWithOutAnimation(fragment, false, R.id.container)
        } else {
            val fragment = PropertyTaxSummaryFragment()
            showToolbarBackButton(R.string.title_property_tax_summary)
            val bundle = Bundle()
            bundle.putInt(Constant.KEY_PRIMARY_KEY, PropertyID ?: 0)
            fragment.arguments = bundle
            addFragmentWithOutAnimation(fragment, false, R.id.container)
        }

    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}