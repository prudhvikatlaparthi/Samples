package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivitySummaryDetailsBinding
import com.sgs.citytax.ui.fragments.AgentSummaryDetailsFragment
import com.sgs.citytax.util.Constant

class AgentSummaryDetailsActivity : BaseActivity() {
    private var binding: ActivitySummaryDetailsBinding? = null
    var accountID = 0
    var organizationID = 0
    private var fromScreen: Constant.QuickMenu? = null

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_summary_details)
        showToolbarBackButton(R.string.label_summary_details)
        processIntent()
        setUpMasterFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (currentFragment) {
            is AgentSummaryDetailsFragment -> {
                showToolbarBackButton(R.string.label_summary_details)
                val agentMasterFragment = currentFragment as AgentSummaryDetailsFragment
                agentMasterFragment.handleBackClick()
            }
            else -> super.onBackPressed()
        }
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.summaryFrame)


    private fun setUpMasterFragment() {

        addFragmentWithOutAnimation(AgentSummaryDetailsFragment.newInstance(fromScreen), true, R.id.summaryFrame)
    }

}