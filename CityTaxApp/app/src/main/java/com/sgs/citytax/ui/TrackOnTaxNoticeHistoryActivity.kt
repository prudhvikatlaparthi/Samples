package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityTrackOnTaxNoticeHistoryBinding
import com.sgs.citytax.model.TaxNoticeDetail
import com.sgs.citytax.ui.fragments.TrackOnTaxNoticeHistoryFragment
import com.sgs.citytax.util.Constant

class TrackOnTaxNoticeHistoryActivity : BaseActivity(), TrackOnTaxNoticeHistoryFragment.Listener {

    private lateinit var binding: ActivityTrackOnTaxNoticeHistoryBinding
    private var mImpondmentReturnHistory: ArrayList<ImpondmentReturn> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_on_tax_notice_history)
        showToolbarBackButton(R.string.label_ticket_history)
        processIntent()
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun processIntent() {
        intent?.let {
            mImpondmentReturnHistory = it.getParcelableArrayListExtra(Constant.KEY_TRACK_ON_TAX_NOTICE_HISTORY)

        }
    }

    private fun attachFragment() {
        val fragment = TrackOnTaxNoticeHistoryFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constant.KEY_TRACK_ON_TAX_NOTICE_HISTORY, mImpondmentReturnHistory)
        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment, false, R.id.container)
    }

}

