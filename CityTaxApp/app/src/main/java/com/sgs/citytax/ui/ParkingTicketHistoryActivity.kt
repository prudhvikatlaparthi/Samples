package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityParkingTicketHistoryBinding
import com.sgs.citytax.model.ParkingTicket
import com.sgs.citytax.ui.fragments.ParkingTicketHistoryFragment
import com.sgs.citytax.util.Constant

class ParkingTicketHistoryActivity : BaseActivity(), ParkingTicketHistoryFragment.Listener {

    private lateinit var binding: ActivityParkingTicketHistoryBinding
    private var mParkingTicket: ParkingTicket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_parking_ticket_history)
        showToolbarBackButton(R.string.parking_ticket_history)
        processIntent()
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun processIntent() {
        intent?.extras?.let {
            mParkingTicket = it.getParcelable(Constant.KEY_PARKING_TICKET_NOTICE_HISTORY)
        }
    }

    private fun attachFragment() {
        val fragment = ParkingTicketHistoryFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelable(Constant.KEY_PARKING_TICKET_NOTICE_HISTORY, mParkingTicket)
        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment, false, R.id.container)
    }

}

