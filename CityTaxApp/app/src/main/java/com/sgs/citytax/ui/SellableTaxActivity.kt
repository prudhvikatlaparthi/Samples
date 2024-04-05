package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivitySellableTaxBinding
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class SellableTaxActivity : BaseActivity(),
        FragmentCommunicator, BusinessOwnerSearchFragment.Listener,
        BusinessOwnerEntryFragment.Listener,
        DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener,
        NotesMasterFragment.Listener,
        NotesEntryFragment.Listener,
        PhoneMasterFragment.Listener,
        PhoneEntryFragment.Listener,
        CardMasterFragment.Listener,
        CardEntryFragment.Listener,
        EmailMasterFragment.Listener,
        EmailEntryFragment.Listener,
        AddressMasterFragment.Listener,
        AddressEntryFragment.Listener {
    private lateinit var binding: ActivitySellableTaxBinding
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD

    private var fromScreen: Constant.QuickMenu? = Constant.QuickMenu.QUICK_MENU_SALES_TAX

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sellable_tax)
        mScreenMode = Constant.ScreenMode.EDIT


        intent?.let {
            fromScreen = it?.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_SALES_TAX)
            showToolbarBackButton(R.string.title_sales_tax)
        else
            showToolbarBackButton(R.string.title_security_service)

        val fragment = SalesTaxFragment.newInstance()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        fragment.arguments = bundle

        addFragment(fragment, false, R.id.container)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return false
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.container)
    }

    override fun onBackPressed() {
        when (currentFragment) {
            is DocumentsMasterFragment ->
                (currentFragment as DocumentsMasterFragment).onBackPressed()
            is NotesMasterFragment ->
                (currentFragment as NotesMasterFragment).onBackPressed()
            is PhoneMasterFragment ->
                (currentFragment as PhoneMasterFragment).onBackPressed()
            is EmailMasterFragment ->
                (currentFragment as EmailMasterFragment).onBackPressed()
            is CardMasterFragment ->
                (currentFragment as CardMasterFragment).onBackPressed()
            is AddressMasterFragment ->
                (currentFragment as AddressMasterFragment).onBackPressed()
        }
        if (currentFragment is SalesTaxFragment && (currentFragment as SalesTaxFragment).isPopUpVisible()) {
            (currentFragment as SalesTaxFragment).onBackPressed()
        } else {
            super.onBackPressed()
        }
        when (currentFragment) {
            is BusinessOwnerEntryFragment -> {
                showToolbarBackButton(R.string.citizen)
            }
            is SalesTaxFragment ->{
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_SALES_TAX)
                    showToolbarBackButton(R.string.title_sales_tax)
                else
                    showToolbarBackButton(R.string.title_security_service)
            }
        }
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }
}