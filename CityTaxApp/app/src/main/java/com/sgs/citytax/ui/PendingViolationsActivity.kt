package com.sgs.citytax.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityPendingViolationsBinding
import com.sgs.citytax.model.LAWViolationType
import com.sgs.citytax.model.TicketHistory
import com.sgs.citytax.ui.fragments.PendingViolationSearchDialogFragment
import com.sgs.citytax.ui.fragments.PendingViolationsFragment
import com.sgs.citytax.util.Constant

class PendingViolationsActivity : BaseActivity(),
        PendingViolationsFragment.Listener,
        PendingViolationSearchDialogFragment.Listener {

    private lateinit var mBinding: ActivityPendingViolationsBinding
    private var mViolationTypes: ArrayList<LAWViolationType>? = arrayListOf()
    private val pendingViolationsFragment = PendingViolationsFragment.newInstance()
    private var mTicketHistory: TicketHistory = TicketHistory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pending_violations)
        showToolbarBackButton(R.string.title_pending_violations)
        bindSpinner()
    }

    private fun attachFragment() {
        addFragmentWithOutAnimation(pendingViolationsFragment, false, R.id.container)
    }

    private fun bindSpinner() {
        showProgressDialog()
        APICall.getCorporateOfficeLOVValues("LAW_ViolationTickets", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                dismissDialog()
                mViolationTypes = arrayListOf()
                mViolationTypes?.addAll(response.violationTypes)
                attachFragment()
            }

            override fun onFailure(message: String) {
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_search_business, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == android.R.id.home)
            onBackPressed()
        else if (item.itemId == R.id.action_search) {
            val fragment = PendingViolationSearchDialogFragment.newInstance()
            val bundle = Bundle()
            bundle.putParcelableArrayList(Constant.KEY_VIOLATION_TYPES, mViolationTypes)
            bundle.putParcelable(Constant.KEY_TICKET_HISTORY, mTicketHistory)
            fragment.arguments = bundle
            this.supportFragmentManager.let {
                fragment.show(it, PendingViolationSearchDialogFragment::class.java.simpleName)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onApply(ticketHistory: TicketHistory) {
        mTicketHistory = ticketHistory
        pendingViolationsFragment.apply(mTicketHistory)
    }

    override fun clear() {
        mTicketHistory = TicketHistory()
    }

}