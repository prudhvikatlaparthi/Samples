package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CommissionHistory
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityAgentCommissionReportBinding
import com.sgs.citytax.ui.fragments.AgentCommissionReportFragment
import com.sgs.citytax.ui.fragments.RequestForPayoutFragment

class AgentCommissionReportActivity : BaseActivity(), AgentCommissionReportFragment.Listener, RequestForPayoutFragment.Listener {

    private lateinit var binding: ActivityAgentCommissionReportBinding
    private var isSupervisorOrInspector = prefHelper.isSupervisorOrInspector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agent_commission_report)
        showToolbarBackButton(R.string.agent_commission_report)
        setUpFragment()
    }

    private fun setUpFragment() {
        if (isSupervisorOrInspector) {
            addFragmentWithOutAnimation(RequestForPayoutFragment(), true, R.id.container)
        } else
            addFragmentWithOutAnimation(AgentCommissionReportFragment(), true, R.id.container)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home ->
                finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (currentFragment) {
            is AgentCommissionReportFragment -> {
                val agentCommissionReportFragment = currentFragment as AgentCommissionReportFragment
                agentCommissionReportFragment.handleBackClick()
            }
            is RequestForPayoutFragment -> {
                if (isSupervisorOrInspector) {
                    finish()
                } else
                    popBackStack()
            }
            else ->
                finish()
        }
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)


    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, true, R.id.container)
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragmentWithOutAnimation(fragment, true, R.id.container)
    }

    override fun printAgentDetails(commissionHistory: CommissionHistory) {
        val intent = Intent(this, PayoutPreviewActivity::class.java)
        intent.putExtra("CommissionHistory", commissionHistory)
        startActivity(intent)
    }
}