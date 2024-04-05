package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityTrackOnTrasactionHistoryBinding
import com.sgs.citytax.ui.fragments.TrackOnTransactionHistoryFragment

class TrackOnTransactionHistoryActivity : BaseActivity(), TrackOnTransactionHistoryFragment.Listener {

    private lateinit var binding: ActivityTrackOnTrasactionHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_on_trasaction_history)

        showToolbarBackButton(R.string.business_transaction_history)
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }


    private fun attachFragment() {
        val fragment = TrackOnTransactionHistoryFragment.newInstance()
        addFragment(fragment, true, R.id.container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
