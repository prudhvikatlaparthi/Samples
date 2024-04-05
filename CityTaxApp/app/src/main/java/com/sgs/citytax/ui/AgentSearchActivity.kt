package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityAgentSearchBinding
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class AgentSearchActivity : BaseActivity(), AgentSearchFragment.Listener, AgentEntryFragment.Listener,
        DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener,
        NotesEntryFragment.Listener,
        NotesMasterFragment.Listener {

    private lateinit var binding: ActivityAgentSearchBinding
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agent_search)
        showToolbarBackButton(R.string.title_agent)
        attachFragment()
        mScreenMode = Constant.ScreenMode.EDIT
    }

    private fun attachFragment() {
        addFragmentWithOutAnimation(AgentSearchFragment(), true, R.id.agentSearchContainer)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.agentSearchContainer)

    override fun onBackPressed() {
        when (currentFragment) {
            is AgentSearchFragment -> {
                (currentFragment as AgentSearchFragment).onBackPressed()
            }
            is DocumentsMasterFragment -> {
                (currentFragment as DocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is NotesMasterFragment -> {
                (currentFragment as NotesMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
        when (currentFragment) {
            is AgentEntryFragment -> {
                showToolbarBackButton(R.string.title_agent)
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.agentSearchContainer)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }
}