package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityIncidentBinding
import com.sgs.citytax.ui.fragments.IncidentEntryFragment
import com.sgs.citytax.ui.fragments.IncidentFragment
import com.sgs.citytax.ui.fragments.LocateDialogFragment
import com.sgs.citytax.util.Constant

class IncidentActivity : BaseActivity(), IncidentFragment.Listener, IncidentEntryFragment.Listener, LocateDialogFragment.Listener {
    private lateinit var binding: ActivityIncidentBinding
    private var accountID: String? = null
    private var fromScreen: Constant.QuickMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_incident)
        showToolbarBackButton(R.string.menu_incident_management)
        processIntent()
        attachFragment()
    }

    private fun attachFragment() {
        val bundle = Bundle()
        val incidentManagementFragment = IncidentFragment()

        bundle.putString(Constant.KEY_ACCOUNT_ID, accountID)
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        incidentManagementFragment.arguments = bundle
        addFragmentWithOutAnimation(incidentManagementFragment, true, R.id.incidentContainer)
    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?
            if (it.hasExtra("s_agent_acctid"))
                accountID = it.getStringExtra("s_agent_acctid")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.incidentContainer)
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, true, R.id.incidentContainer)
    }

    override fun onLatLonFound(latitude: Double?, longitude: Double?) {
        latitude?.let { lat ->
            longitude?.let {
                val fragment = this.supportFragmentManager.findFragmentById(R.id.incidentContainer)
                if (fragment is IncidentEntryFragment)
                    fragment.updateText(lat, it)
            }
        }
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.incidentContainer)

    override fun onBackPressed() {
        when (currentFragment) {
            is IncidentFragment -> {
                (currentFragment as IncidentFragment).onBackPressed()
            }
            is IncidentEntryFragment -> {
                (currentFragment as IncidentEntryFragment).onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

}