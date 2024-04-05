package com.sgs.citytax.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityParkingTicketReceiptBinding
import com.sgs.citytax.ui.fragments.ParkingTicketPreviewFragment
import com.sgs.citytax.util.Constant

class ParkingTicketReceiptActivity : BaseActivity(), ParkingTicketPreviewFragment.Listener {

    private lateinit var mBinding: ActivityParkingTicketReceiptBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var invoiceId: Int? = 0
    private var advanceReceivedId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_parking_ticket_receipt)
        hideToolbar()
        processIntent()
        attachFragment()
    }

    private fun processIntent() {
        intent.extras?.let {
            if (it.containsKey(Constant.KEY_TAX_INVOICE_ID))
                invoiceId = it.getInt(Constant.KEY_TAX_INVOICE_ID)

            if (it.containsKey(Constant.KEY_ADVANCE_RECEIVED_ID))
                advanceReceivedId = it.getInt(Constant.KEY_ADVANCE_RECEIVED_ID)

            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun attachFragment() {
        val fragment = ParkingTicketPreviewFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putInt(Constant.KEY_TAX_INVOICE_ID, invoiceId ?: 0)
        bundle.putInt(Constant.KEY_ADVANCE_RECEIVED_ID, advanceReceivedId ?: 0)
        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment, false, R.id.container)
        prefHelper.isFromHistory = false
    }
}