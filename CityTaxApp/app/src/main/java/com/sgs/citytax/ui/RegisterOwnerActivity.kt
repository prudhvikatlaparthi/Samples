package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityRegisterOwnerBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.VUCRMAccounts
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class RegisterOwnerActivity : BaseActivity(), BusinessOwnerEntryFragment.Listener
        , DocumentsMasterFragment.Listener
        , DocumentEntryFragment.Listener
        , NotesMasterFragment.Listener
        , NotesEntryFragment.Listener
        , PhoneMasterFragment.Listener
        , PhoneEntryFragment.Listener
        , CardMasterFragment.Listener
        , CardEntryFragment.Listener
        , EmailMasterFragment.Listener
        , EmailEntryFragment.Listener
        , AddressMasterFragment.Listener
        , AddressEntryFragment.Listener {
    private lateinit var mBinding: ActivityRegisterOwnerBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var vuCrmAccount: VUCRMAccounts? = null
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register_owner)
        mScreenMode = Constant.ScreenMode.EDIT
        showToolbarBackButton(R.string.citizen)
        processIntent()
        showBusinessOwnerEntryScreen()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.QuickMenu

            vuCrmAccount = ObjectHolder.registerBusiness.vuCrmAccounts
        }
    }

    private fun showBusinessOwnerEntryScreen() {
        val businessOwnerEntryFragment = BusinessOwnerEntryFragment()
        //region SetArguments
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        businessOwnerEntryFragment.arguments = bundle
        //endregion
        addFragment(businessOwnerEntryFragment, false, R.id.container)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.container)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    private fun showConfirmationDialog() {
        showAlertDialog(R.string.do_you_want_to_exit,
                R.string.yes,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    super.onBackPressed()
                },
                R.string.no,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                })
    }

    override fun onBackPressed() {
        when (currentFragment) {
            is BusinessOwnerEntryFragment -> {
                showConfirmationDialog()
                return
            }
            is AddressMasterFragment -> {
                (currentFragment as AddressMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is DocumentsMasterFragment -> {
                (currentFragment as DocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is NotesMasterFragment -> {
                (currentFragment as NotesMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is PhoneMasterFragment -> {
                (currentFragment as PhoneMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is EmailMasterFragment -> {
                (currentFragment as EmailMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is CardMasterFragment -> {
                (currentFragment as CardMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            else ->
                super.onBackPressed()
        }
        when (currentFragment) {
            is BusinessOwnerEntryFragment -> {
                showToolbarBackButton(R.string.citizen)
            }
        }
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }
}