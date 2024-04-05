package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityGamingWeaponBinding
import com.sgs.citytax.model.Weapon
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class WeaponRegisterActivity : BaseActivity(),
        WeaponTaxFragment.Listener,
        BusinessOwnerSearchFragment.Listener,
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

    private lateinit var mBinding: ActivityGamingWeaponBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var sycoTaxID: String? = null
    private var weaponTax: Weapon? = null
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var primaryKey = 0
    private var acctID = 0 //weaponID


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_gaming_weapon)
        showToolbarBackButton(R.string.title_weapon_tax)
        processIntent()
        showFragment()
    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.QuickMenu
            if (it.hasExtra(Constant.KEY_SYCO_TAX_ID))
                sycoTaxID = it.getStringExtra(Constant.KEY_SYCO_TAX_ID) ?: ""
            if (it.hasExtra(Constant.KEY_WEAPON_TAX)) {
                weaponTax = it.getParcelableExtra(Constant.KEY_WEAPON_TAX)
                weaponTax?.isInvoiceGenerated?.let { isInvoiceGenerated ->
                    if (isInvoiceGenerated)
                        screenMode = Constant.ScreenMode.VIEW
                }
            }
            if (it.hasExtra(Constant.KEY_SCREEN_MODE))
                screenMode = it.getSerializableExtra(Constant.KEY_SCREEN_MODE) as Constant.ScreenMode

            if (it.hasExtra(Constant.KEY_PRIMARY_KEY))
                primaryKey = it.getIntExtra(Constant.KEY_PRIMARY_KEY, 0)
            if (it.hasExtra(Constant.KEY_ACCOUNT_ID))
                acctID = it.getIntExtra(Constant.KEY_ACCOUNT_ID, 0)

        }
    }

    private fun showFragment() {
//        if (fromScreen == Constant.QuickMenu.QUICK_MENU_CREATE_WEAPON_TAX || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_WEAPON_TAX) {
        val fragment = WeaponTaxFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putSerializable(Constant.KEY_SYCO_TAX_ID, sycoTaxID)

        bundle.putInt(Constant.KEY_ACCOUNT_ID, acctID)
        bundle.putInt(Constant.KEY_PRIMARY_KEY, primaryKey)
        weaponTax?.let {
            bundle.putParcelable(Constant.KEY_WEAPON_TAX, it)
        }
        fragment.arguments = bundle
        addFragment(fragment, false, R.id.container)
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
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
            is GamingMachineTaxFragment -> {
                showConfirmationDialog()
                return
            }
            is WeaponTaxFragment -> {
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
            is WeaponTaxFragment -> showToolbarBackButton(R.string.title_weapon_tax)
        }
    }

    private fun showConfirmationDialog() {
        showAlertDialog(R.string.do_you_want_to_exit,
                R.string.yes,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    if (currentFragment is OutstandingEntryFragment) {
                        (currentFragment as OutstandingEntryFragment).onBackPressed()
                        super.onBackPressed()
                    } else
                        finish()
                },
                R.string.no,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                })
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.container)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

}