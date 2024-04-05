package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetTicketsIssuedToDriver
import com.sgs.citytax.databinding.FragmentDriverTicketHistoryBinding
import com.sgs.citytax.model.TicketHistory
import com.sgs.citytax.ui.adapter.DriverTicketHistoryAdapter
import com.sgs.citytax.util.Constant

class DriverTicketHistoryFragment : BaseFragment() {
    private lateinit var mBinding: FragmentDriverTicketHistoryBinding
    private var mListener: Listener? = null
    private var mDrivingLicenseNo: String? = ""
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_driver_ticket_history, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_DRIVING_LICENSE_NO))
                mDrivingLicenseNo = it.getString(Constant.KEY_DRIVING_LICENSE_NO)
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        bindData()
    }

    private fun bindData() {
        mListener?.showProgressDialog()

        val getTicketsIssuedToDriver = GetTicketsIssuedToDriver()
        getTicketsIssuedToDriver.drivingLicenseNo = mDrivingLicenseNo

        APICall.getTicketsIssuedToDriver(getTicketsIssuedToDriver, object : ConnectionCallBack<List<TicketHistory>> {
            override fun onSuccess(response: List<TicketHistory>) {
                val list: ArrayList<TicketHistory> = arrayListOf()
                if (mCode == Constant.QuickMenu.QUICK_MENU_IMPONDMENT) {
                    for (item in response) {
                        if (item.invoiceTransactionTypeCode == Constant.InvoiceTransactionTypeCode.IMPOUNDMENT.code)
                            list.add(item)
                    }
                } else if (mCode == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE) {
                    for (item in response) {
                        if (item.invoiceTransactionTypeCode == Constant.InvoiceTransactionTypeCode.VIOLATION_TICKETS.code)
                            list.add(item)
                    }
                }
                mBinding.rcvTicketHistory.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
                mBinding.rcvTicketHistory.adapter = DriverTicketHistoryAdapter(list)
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String)
    }
}