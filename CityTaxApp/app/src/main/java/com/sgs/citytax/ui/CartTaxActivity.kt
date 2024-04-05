package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityCartTaxBinding
import com.sgs.citytax.model.CartTax
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class CartTaxActivity : BaseActivity(),
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
        AddressEntryFragment.Listener,
        OutstandingsMasterFragment.Listener,
        OutstandingEntryFragment.Listener {
    private lateinit var binding: ActivityCartTaxBinding
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var mSycoTaxID: String? = ""
    private var mCartTax: CartTax? = null
    private var mCode: Constant.QuickMenu? = Constant.QuickMenu.QUICK_MENU_NONE

    private var primaryKey = 0
    private var acctID = 0 //cartID


    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_tax)
        showToolbarBackButton(R.string.title_cart_tax)
        processIntent()

        val fragment = CartTaxFragment.newInstance()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
        bundle.putString(Constant.KEY_SYCO_TAX_ID, mSycoTaxID)

        bundle.putInt(Constant.KEY_ACCOUNT_ID, acctID)
        bundle.putInt(Constant.KEY_PRIMARY_KEY, primaryKey)

        mCartTax?.let {
            bundle.putParcelable(Constant.KEY_CART_TAX, it)
            it.isInvoiceGenerated?.let { isInvoiceGenerated ->
                if (isInvoiceGenerated)
                    mScreenMode = Constant.ScreenMode.VIEW
            }
        }


        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment, false, R.id.container)
    }

    private fun processIntent() {
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_SYCO_TAX_ID))
                mSycoTaxID = it.getString(Constant.KEY_SYCO_TAX_ID)
            if (it.containsKey(Constant.KEY_CART_TAX))
                mCartTax = it.getParcelable(Constant.KEY_CART_TAX)
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu

            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                primaryKey = it.getInt(Constant.KEY_PRIMARY_KEY, 0)
            if (it.containsKey(Constant.KEY_ACCOUNT_ID))
                acctID = it.getInt(Constant.KEY_ACCOUNT_ID, 0)
        }
        if (intent.hasExtra(Constant.KEY_SCREEN_MODE))
            screenMode = intent.getSerializableExtra(Constant.KEY_SCREEN_MODE) as Constant.ScreenMode
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
            is OutstandingEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
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
            is OutstandingsMasterFragment ->
                (currentFragment as OutstandingsMasterFragment).onBackPressed()
            is CartTaxFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
        }
        super.onBackPressed()
        when (currentFragment) {
            is BusinessOwnerEntryFragment -> {
                showToolbarBackButton(R.string.citizen)
            }
            is CartTaxFragment -> showToolbarBackButton(R.string.title_cart_tax)
        }
    }

    private fun showConfirmationDialog() {
        showAlertDialog(R.string.do_you_want_to_exit,
                R.string.yes,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    if(currentFragment is OutstandingEntryFragment) {
                        (currentFragment as OutstandingEntryFragment).onBackPressed()
                        super.onBackPressed()
                    }else
                        finish()
                },
                R.string.no,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                })
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }
}