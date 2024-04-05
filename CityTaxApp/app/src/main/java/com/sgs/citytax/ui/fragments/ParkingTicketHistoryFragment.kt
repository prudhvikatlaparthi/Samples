package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.databinding.FragmentParkingTicketHistoryBinding
import com.sgs.citytax.model.ParkingTicket
import com.sgs.citytax.ui.adapter.ParkingTicketHistoryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class ParkingTicketHistoryFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentParkingTicketHistoryBinding
    private var mListener: Listener? = null
    private var mParkingTicket: ParkingTicket? = null

    companion object {
        fun newInstance() = ParkingTicketHistoryFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_parking_ticket_history, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            mParkingTicket = it.getParcelable(Constant.KEY_PARKING_TICKET_NOTICE_HISTORY)
        }
        bindData()
    }

    private fun bindData() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mParkingTicket?.let {
            val list: ArrayList<ParkingTicket> = arrayListOf()
            list.add(it)
            mBinding.recyclerView.adapter = ParkingTicketHistoryAdapter(this, list)
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val v = EditText(context)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(16, 0, 16, 0)
        v.layoutParams = params
        v.hint = getString(R.string.hint_enter_remarks)
        // endregion
        mListener?.showAlertDialog(R.string.remarks,
                R.string.save,
                View.OnClickListener {
                    val remarks = v.text?.toString()?.trim()
                    if (TextUtils.isEmpty(remarks)) {
                        v.error = getString(R.string.msg_enter_remarks)
                    } else {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        showConfirmationDialog(obj as ParkingTicket, remarks)
                    }
                },
                R.string.cancel,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                R.string.skip_and_save,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    showConfirmationDialog(obj as ParkingTicket)
                },
                v)
    }

    private fun showConfirmationDialog(parkingTicket: ParkingTicket, remarks: String? = "") {
        mListener?.showAlertDialog(R.string.are_you_sure_you_want_to_continue,
                R.string.yes,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    if (remarks == null || TextUtils.isEmpty(remarks))
                        cancelTaxNotice(parkingTicket)
                    else
                        cancelTaxNotice(parkingTicket, remarks)
                },
                R.string.no,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                })
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun cancelTaxNotice(parkingTicket: ParkingTicket, remarks: String? = "") {
        parkingTicket.parkingTicketID?.let { it ->
            mListener?.showProgressDialog()
            APICall.cancelTaxNotice(it, remarks, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    val list: ArrayList<ParkingTicket> = arrayListOf()
                    mParkingTicket?.let {
                        list.add(it)
                    }
                    if (list.contains(parkingTicket)) {
                        val index = list.indexOf(parkingTicket)
//                        parkingTicket.statusCode = Constant.TaxInvoices.CANCELLED.Status
                        list[index] = parkingTicket
                    }
                    bindData()
                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }

    interface Listener {
        fun showProgressDialog()
        fun showAlertDialog(message: String)
        fun dismissDialog()
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener)
    }

}