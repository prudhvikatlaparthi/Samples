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
import com.sgs.citytax.api.response.VehicleTicketHistoryResponse
import com.sgs.citytax.databinding.FragmentVehicleTicketHistoryBinding
import com.sgs.citytax.model.VehicleTicketHistoryDetails
import com.sgs.citytax.ui.adapter.VehicleTicketHistoryAdapter
import com.sgs.citytax.util.Constant

class VehicleTicketHistoryFragment : BaseFragment() {
    private lateinit var mBinding: FragmentVehicleTicketHistoryBinding
    private var mListener: Listener? = null
    private var vehicleNo: String? = ""
    private lateinit var fromScreen: Constant.QuickMenu

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicle_ticket_history, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        processIntent()
        bindData()
    }

    private fun processIntent() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_VEHICLE_NO))
                vehicleNo = it.getString(Constant.KEY_VEHICLE_NO)

            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun bindData() {
        mListener?.showProgressDialog()

        APICall.getVehicleTicketsList(vehicleNo, object : ConnectionCallBack<VehicleTicketHistoryResponse> {
            override fun onSuccess(response: VehicleTicketHistoryResponse) {
                mListener?.dismissDialog()
                if (response.ticketHistoryDetails != null && response.ticketHistoryDetails.isNotEmpty()) {
                    val list: ArrayList<VehicleTicketHistoryDetails> = arrayListOf()
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_IMPONDMENT) {
                        for (item in response.ticketHistoryDetails) {
                            if (item.transactionTypeCode == Constant.InvoiceTransactionTypeCode.IMPOUNDMENT.code)
                                list.add(item)
                        }
                    } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE) {
                        for (item in response.ticketHistoryDetails) {
                            if (item.transactionTypeCode == Constant.InvoiceTransactionTypeCode.VIOLATION_TICKETS.code)
                                list.add(item)
                        }
                    }
                    mBinding.rcvTicketHistory.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
                    mBinding.rcvTicketHistory.adapter = VehicleTicketHistoryAdapter(list)
                } else {
                    mListener?.showAlertDialog(getString(R.string.msg_no_data))
                }
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