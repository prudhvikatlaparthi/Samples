package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetPendingViolationImpoundmentList
import com.sgs.citytax.api.response.PendingViolationImpoundment
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPendingViolationsBinding
import com.sgs.citytax.model.TicketHistory
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.adapter.PendingViolationsAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.Pagination

class PendingViolationsFragment : BaseFragment(), IClickListener {

    private lateinit var mBinding: FragmentPendingViolationsBinding
    private var mListener: Listener? = null
    private lateinit var pagination: Pagination
    private val resultList: MutableList<TicketHistory> = mutableListOf()
    private lateinit var adapter: PendingViolationsAdapter
    private var mTicketHistory: TicketHistory = TicketHistory()

    companion object {
        fun newInstance() = PendingViolationsFragment()
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending_violations, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        setViews()
        bindData()
    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = PendingViolationsAdapter(this)
        mBinding.recyclerView.adapter = adapter
        pagination = Pagination(1, 10, mBinding.recyclerView) { pageNumber, PageSize ->
            bindData(pageIndex = pageNumber, pageSize = PageSize)
        }
    }

    private fun bindData(pageIndex: Int = 1, pageSize: Int = 10) {
        val getPendingViolationImpoundmentList = GetPendingViolationImpoundmentList()
        // getPendingViolationImpoundmentList.filterString = ""
        getPendingViolationImpoundmentList.filterType = "violationtickets"
        getPendingViolationImpoundmentList.pageSize = pageSize
        getPendingViolationImpoundmentList.pageIndex = pageIndex
        if (mTicketHistory.violationTypeID != -1)
            getPendingViolationImpoundmentList.typeID = mTicketHistory.violationTypeID
        if (mTicketHistory.violationSubTypeID != -1)
            getPendingViolationImpoundmentList.subTypeID = mTicketHistory.violationSubTypeID
        if (mTicketHistory.mobileNo != null && !TextUtils.isEmpty(mTicketHistory.mobileNo)) {
            getPendingViolationImpoundmentList.mobileFilter = "Mobile"
            getPendingViolationImpoundmentList.mobileFilterString = mTicketHistory.mobileNo
        }
        if (mTicketHistory.vehicleNo != null && !TextUtils.isEmpty(mTicketHistory.vehicleNo)) {
            getPendingViolationImpoundmentList.vehicleFilter = "VehicleNo"
            getPendingViolationImpoundmentList.vehicleFilterString = mTicketHistory.vehicleNo
        }
        if (pageIndex == 1) {
            mListener?.showProgressDialog()
            resultList.clear()
            mBinding.recyclerView.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            mListener?.showProgressDialog()
        } else {
            mBinding.ProgressBar.isVisible = true
        }
        APICall.getPendingViolations(getPendingViolationImpoundmentList, object : ConnectionCallBack<PendingViolationImpoundment> {
            override fun onSuccess(response: PendingViolationImpoundment) {
                response.totalSearchedRecords?.let {
                    pagination.totalRecords = it
                }

                if (response.results?.ticketHistory?.size ?: 0 > 0) {
                    response.results?.ticketHistory?.let {
                        resultList.addAll(it)
                        pagination.stopPagination(it.size)
                        adapter.differ.submitList(resultList)
                        adapter.notifyDataSetChanged()
                        pagination.setIsScrolled(false)
                    }
                } else {
                    pagination.stopPagination(0)
                    if (pageIndex == 1) {
                        resetRecyclerAdapter()
                        mListener?.showAlertDialog(getString(R.string.msg_no_data))
                    }
                }
                mListener?.dismissDialog()
                mBinding.ProgressBar.isVisible = false
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (pageIndex == 1) {
                    mListener?.showAlertDialog(message)
                }
                mBinding.ProgressBar.isVisible = false
            }
        })
    }

    fun apply(ticketHistory: TicketHistory) {
        mTicketHistory = ticketHistory
        bindData()
    }

    private fun resetRecyclerAdapter() {
        resultList.clear()
        adapter.differ.submitList(resultList)
        adapter.notifyDataSetChanged()
    }

    interface Listener {
        fun showProgressDialog()
        fun showAlertDialog(message: String)
        fun dismissDialog()
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val mTicketHistory = obj as TicketHistory
        val payment = MyApplication.resetPayment()

        payment.amountDue = mTicketHistory.transactionDue!!
        payment.amountTotal = mTicketHistory.transactionDue!!
        payment.minimumPayAmount = mTicketHistory.minmumPayAmount!!
        payment.customerID = mTicketHistory.transactionNo!!
        payment.paymentType = Constant.PaymentType.TICKET_PAY
        payment.productCode = mTicketHistory.prodcode!!
        payment.TransactionTypeCode = mTicketHistory.transactiontypcode!!
        payment.TransactionNo = mTicketHistory.transactionNo!!
        payment.customerID = mTicketHistory?.accounttId!!

        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_TICKET_PAYMENT)
        startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)

    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.let {
                if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID) && it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0) > 0) {
                    Log.e("Ticket payment frag", ">>>>>>>> Payment response came")

                    val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                    if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0))
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.TICKET_PAYMENT.Code)
                    startActivityForResult(intent, Constant.REQUEST_CODE_TAX_PRINT_PAGE)
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_TAX_PRINT_PAGE) {
            //this will clear the adapter and bind the list again once the payment and receipt are done successfully
            bindData()
        }
    }
}