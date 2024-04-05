package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityAgentCollectionSummaryBinding
import com.sgs.citytax.ui.fragments.AgentCollectionSummaryFragment

class AgentCollectionSummaryActivity : BaseActivity(), AgentCollectionSummaryFragment.Listener {

    private var binding: ActivityAgentCollectionSummaryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agent_collection_summary)
        showToolbarBackButton(R.string.title_agent_collection_summary)
        setUpMasterFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
    }

    private fun setUpMasterFragment() {
        addFragmentWithOutAnimation(AgentCollectionSummaryFragment(), true, R.id.container)
    }


}