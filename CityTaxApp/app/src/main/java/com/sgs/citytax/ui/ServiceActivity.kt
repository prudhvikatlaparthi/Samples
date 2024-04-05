package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityServiceBinding
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_QUICK_MENU

class ServiceActivity : BaseActivity(),
        ServiceFragment.Listener,
        ServiceEntryFragment.Listener,
        BusinessOwnerEntryFragment.Listener,
        DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener,
        PhoneMasterFragment.Listener,
        PhoneEntryFragment.Listener,
        CardMasterFragment.Listener,
        CardEntryFragment.Listener,
        EmailMasterFragment.Listener,
        BusinessOwnerSearchFragment.Listener,
        EmailEntryFragment.Listener,
        NotesMasterFragment.Listener,
        NotesEntryFragment.Listener,
        LocateDialogFragment.Listener {
    private lateinit var binding: ActivityServiceBinding
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service)
        showToolbarBackButton(R.string.title_service_tax)
        processIntent()
        attachFragment()
    }

    private fun attachFragment() {
        val fragment = ServiceFragment()
        val bundle = Bundle()
        bundle.putSerializable(KEY_QUICK_MENU, mCode)
        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment, true, R.id.container)
    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(KEY_QUICK_MENU))
                mCode = it.getSerializableExtra(KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    override var screenMode: Constant.ScreenMode
        get() = Constant.ScreenMode.EDIT
        set(value) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onBackPressed() {
        when (currentFragment) {
            is EmailMasterFragment -> {
                (currentFragment as EmailMasterFragment).onBackPressed()
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
            is ServiceEntryFragment -> {
                (currentFragment as ServiceEntryFragment).onBackPressed()
            }
            is ServiceFragment -> {
                (currentFragment as ServiceFragment).onBackPressed()
            }
            is CardMasterFragment -> {
                (currentFragment as CardMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
        when (currentFragment) {
            is BusinessOwnerEntryFragment -> {
                showToolbarBackButton(R.string.citizen)
            }
            is ServiceFragment -> {
                showToolbarBackButton(R.string.title_service_tax)
            }
            is ServiceEntryFragment -> {
                showToolbarBackButton(R.string.title_service_tax)
            }
        }
    }

    override fun onLatLonFound(latitude: Double?, longitude: Double?) {
        latitude?.let { lat ->
            longitude?.let {
                val fragment = this.supportFragmentManager.findFragmentById(R.id.container)
                if (fragment is ServiceEntryFragment)
                    fragment.updateText(lat, it)
            }
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.container)
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, true, R.id.container)
    }


}