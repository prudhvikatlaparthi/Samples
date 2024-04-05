package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityAgentOnboardingBinding
import com.sgs.citytax.model.CRMAgents
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class AgentOnboardingActivity : BaseActivity(),
        NotesEntryFragment.Listener,
        NotesMasterFragment.Listener,
        AgentEntryFragment.Listener,
        AgentMasterFragment.Listener,
        DocumentEntryFragment.Listener,
        DocumentsMasterFragment.Listener {

    private lateinit var mBinding: ActivityAgentOnboardingBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var crmAgent: CRMAgents? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_agent_onboarding)
        mScreenMode = Constant.ScreenMode.EDIT
        showToolbarBackButton(R.string.title_agent)
        processIntent()
        showFragment()

    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.QuickMenu

            if (intent.getParcelableExtra<CRMAgents>(Constant.KEY_AGENT) != null) {
                crmAgent = intent.getParcelableExtra(Constant.KEY_AGENT)
            }

            //While Agent Live Tracking
            if(fromScreen == Constant.QuickMenu.QUICK_MENU_VIEW_AGENT){
                mScreenMode = Constant.ScreenMode.VIEW
            }
        }
    }

    private fun showFragment() {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT ||
            fromScreen == Constant.QuickMenu.QUICK_MENU_VIEW_AGENT) {
            val fragment = AgentEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)

            //While Agent Live Tracking
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_VIEW_AGENT)
                bundle.putParcelable(Constant.KEY_AGENT, crmAgent)

            fragment.arguments = bundle
            //endregion

            addFragment(fragment, false, R.id.container)
        } else {
            val fragment = AgentMasterFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            //endregion

            addFragment(fragment, false, R.id.container)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (currentFragment) {
            is AgentMasterFragment ->
                (currentFragment as AgentMasterFragment).onBackPressed()
            is AgentEntryFragment -> {
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_AGENT ||
                    fromScreen == Constant.QuickMenu.QUICK_MENU_VIEW_AGENT)
                    finish()
                else
                    popBackStack()
            }
            is DocumentsMasterFragment -> {
                showToolbarBackButton(R.string.title_agent)
                (currentFragment as DocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is NotesMasterFragment -> {
                showToolbarBackButton(R.string.title_agent)
                (currentFragment as NotesMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            else -> super.onBackPressed()
        }
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

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, addToBackStack, R.id.container)
    }

}