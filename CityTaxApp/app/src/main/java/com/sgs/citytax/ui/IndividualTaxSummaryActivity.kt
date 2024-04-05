package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityIndividualTaxSummaryBinding
import com.sgs.citytax.ui.fragments.CartTaxSummaryFragment
import com.sgs.citytax.ui.fragments.GamingMachineTaxSummaryFragment
import com.sgs.citytax.ui.fragments.WeaponTaxSummaryFragment
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU
import com.sgs.citytax.util.Constant.KEY_SYCO_TAX_ID

class IndividualTaxSummaryActivity : BaseActivity(),
        CartTaxSummaryFragment.Listener,
        WeaponTaxSummaryFragment.Listener,
        GamingMachineTaxSummaryFragment.Listener {

    private lateinit var binding: ActivityIndividualTaxSummaryBinding
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE
    private var sycoTaxID: String? = ""
    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_individual_tax_summary)
        processIntent()
        setTitle()
        attachFragment()
    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(KEY_QUICK_MENU))
                mCode = it.getSerializableExtra(KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.hasExtra(KEY_SYCO_TAX_ID))
                sycoTaxID = it.getStringExtra(KEY_SYCO_TAX_ID)
        }
    }

    private fun setTitle() {
        if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_CART_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX)
            showToolbarBackButton(R.string.title_cart_tax_summary)
        else if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_WEAPON_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX)
            showToolbarBackButton(R.string.title_weapon_tax_summary)
        else if (mCode == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE ||
                mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX)
            showToolbarBackButton(R.string.title_gaming_machine_tax_summary)
    }

    private fun attachFragment() {
        if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_CART_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_CART_TAX||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX) {
            val fragment = CartTaxSummaryFragment()
            val bundle = Bundle()
            bundle.putString(KEY_SYCO_TAX_ID, sycoTaxID)
            fragment.arguments = bundle
            addFragment(fragment, false)
        } else if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_WEAPON_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX) {
            val fragment = WeaponTaxSummaryFragment()
            val bundle = Bundle()
            bundle.putString(KEY_SYCO_TAX_ID, sycoTaxID)
            fragment.arguments = bundle
            addFragment(fragment, false)
        } else if (mCode == Constant.QuickMenu.QUICK_MENU_GAMING_MACHINE ||
                mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_GAMING_MACHINE||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX ||
                mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX) {
            val fragment = GamingMachineTaxSummaryFragment()
            val bundle = Bundle()
            bundle.putString(KEY_SYCO_TAX_ID, sycoTaxID)
            fragment.arguments = bundle
            addFragment(fragment, false)
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