package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityBusinessOwnerBinding
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class BusinessOwnerSearchActivity : BaseActivity(),
        BusinessOwnerSearchFragment.Listener,
        BusinessOwnerEntryFragment.Listener,
        DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener,
        PhoneMasterFragment.Listener,
        PhoneEntryFragment.Listener,
        EmailMasterFragment.Listener,
        EmailEntryFragment.Listener,
        AddressMasterFragment.Listener,
        AddressEntryFragment.Listener,
        NotesMasterFragment.Listener,
        NotesEntryFragment.Listener,
        CardEntryFragment.Listener,
        CardMasterFragment.Listener{
    private lateinit var binding: ActivityBusinessOwnerBinding
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var fromScreen: Constant.QuickMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_owner)
        showToolbarBackButton(R.string.citizen)
        intent.extras?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        attachFragment()
        mScreenMode = Constant.ScreenMode.EDIT
    }

    private fun attachFragment() {
        val fragment = BusinessOwnerSearchFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        fragment.arguments = bundle
        addFragment(fragment,false)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.businessOwnerContainer)

    override fun onBackPressed() {
        when (currentFragment) {
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
            is CardMasterFragment -> {
                (currentFragment as CardMasterFragment).onBackPressed()
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
            else ->
                super.onBackPressed()
        }
        when (currentFragment) {
            is BusinessOwnerEntryFragment -> {
                showToolbarBackButton(R.string.citizen)
            }

            is BusinessOwnerSearchFragment -> {
                showToolbarBackButton(R.string.citizen)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.businessOwnerContainer)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }
}