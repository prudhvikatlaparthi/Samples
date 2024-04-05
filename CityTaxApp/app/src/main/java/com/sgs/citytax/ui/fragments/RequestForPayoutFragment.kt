package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AgentCommissionResult
import com.sgs.citytax.api.response.AgentCollectionResult
import com.sgs.citytax.api.response.AgentCommissionPayOutListResponse
import com.sgs.citytax.api.response.CommissionHistory
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPayOutRequestBinding
import com.sgs.citytax.databinding.FragmentRejectionRemarksBinding
import com.sgs.citytax.model.CommissionDetails
import com.sgs.citytax.ui.adapter.CashDepositAdapter
import com.sgs.citytax.ui.adapter.RequestPayoutAdapter
import com.sgs.citytax.util.*
import java.util.*

class RequestForPayoutFragment : BaseFragment(), IClickListener {

    private lateinit var binding: FragmentPayOutRequestBinding
    private var rootView: View? = null
    private var listener: Listener? = null
    private var prefHelper = MyApplication.getPrefHelper()
    private var commissionHistories: MutableList<CommissionHistory> = ArrayList()
    private lateinit var mContext: Context

    private var isSupervisorOrInspector: Boolean = prefHelper.isSupervisorOrInspector()

    private lateinit var pagination: Pagination
    private val requestPayoutAdapter: RequestPayoutAdapter by lazy {
        RequestPayoutAdapter(commissionHistories,this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        try {
            listener = context as Listener
        } catch (e: Exception) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pay_out_request, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    override fun initComponents() {
        pagination = Pagination(1, 10, binding.rcvPayOutDetails) { pageNumber, pageSize ->
            bindData(false, 0,mPageindex =  pageNumber, mPageSize = pageSize)
        }
        pagination.setDefaultValues()
//        bindData(false, 0)
        initialiseListeners()
    }

//    fun bindData(isFromApproval: Boolean, position: Int) {
//        val accountId = prefHelper.accountId
//        listener?.showProgressDialog()
//        APICall.getPayoutListForAgent(accountId, object : ConnectionCallBack<DataResponse> {
//            override fun onSuccess(response: DataResponse) {
//                commissionHistories = response.commissionHistories
//                if (commissionHistories.isNotEmpty()) {
//                    binding.rcvPayOutDetails.addItemDecoration(DividerItemDecoration(this@RequestForPayoutFragment.context, LinearLayoutManager.VERTICAL))
//                    binding.rcvPayOutDetails.adapter = RequestPayoutAdapter(commissionHistories, iClickListener = this@RequestForPayoutFragment)
//
//                    if (isFromApproval) {
//                        listener?.printAgentDetails(commissionHistories[position])
//                    }
//                }
//                showViewsFirstTime()
//                listener?.dismissDialog()
//            }
//
//            override fun onFailure(message: String) {
//                listener?.dismissDialog()
//                binding.rcvPayOutDetails.adapter = null
//                showViewsFirstTime()
//                if (message.isNotEmpty())
//                    listener?.showAlertDialog(message)
//            }
//
//        })
//    }


    fun bindData(isFromApproval: Boolean, position: Int,mPageindex: Int = 1, mPageSize: Int = 10) {
        val accountId = prefHelper.accountId
        if (mPageindex == 1) {
            binding.rcvPayOutDetails.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            resetRecyclerAdapter()
        } else {
            binding.ProgressBar.isVisible = true
        }
        listener?.showProgressDialog()
        APICall.getPayoutListForAgent(accountId,mPageindex,mPageSize, object : ConnectionCallBack<AgentCommissionResult> {
            override fun onSuccess(response: AgentCommissionResult) {
                try {
                    if (mPageindex == 1) {
                        response.totalSearchedRecords?.let {
                            pagination.totalRecords = it
                        }
                    }
                    if (response.results?.commissionHistory?.size ?: 0 > 0) {
                        response.results?.commissionHistory?.size?.let { pagination.stopPagination(it) }
                        response.results?.commissionHistory.let { dataList ->
                            commissionHistories.addAll(dataList!!)
                            requestPayoutAdapter.differ.submitList(commissionHistories)
                            requestPayoutAdapter.notifyDataSetChanged()
                            binding.rcvPayOutDetails.adapter = requestPayoutAdapter
                            pagination.setIsScrolled(false)
                            if (commissionHistories.isEmpty()) {
                                binding.rcvPayOutDetails.visibility = View.GONE
                                binding.txtNoDataFound.visibility = View.VISIBLE
                            } else {
                                binding.rcvPayOutDetails.visibility = View.VISIBLE
                                binding.txtNoDataFound.visibility = View.GONE
                            }
                        }
                    } else {
                        pagination.stopPagination(0)
                        if (mPageindex == 1) {
                            resetRecyclerAdapter()
                            binding.rcvPayOutDetails.visibility = View.GONE
                            binding.txtNoDataFound.visibility = View.VISIBLE
                        }
                    }

                } catch (ex: Exception) {
                    LogHelper.writeLog(exception = ex)
                }

                binding.ProgressBar.isVisible = false
                showViewsFirstTime()
                listener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                if (mPageindex == 1) {
                    resetRecyclerAdapter()
                    if (commissionHistories.isEmpty()) {
                        binding.rcvPayOutDetails.visibility = View.GONE
                        binding.txtNoDataFound.visibility = View.VISIBLE
                    }
                }
                binding.ProgressBar.isVisible = false
                listener?.dismissDialog()
                showViewsFirstTime()
                if (message.isNotEmpty())
                    listener?.showAlertDialog(message)
            }

        })
    }

    private fun resetRecyclerAdapter() {
        commissionHistories.clear()
        requestPayoutAdapter.differ.submitList(commissionHistories)
        requestPayoutAdapter.notifyDataSetChanged()
    }


    fun showViewsFirstTime() {
        if (prefHelper.isAssociationAgent() && prefHelper.allowCombinedPayoutRequest) {
            binding.llNewCancel.visibility = View.GONE
        } else {
            if (prefHelper.isSupervisorOrInspector()) {
                binding.llNewCancel.visibility = View.GONE
            } else binding.llNewCancel.visibility = View.VISIBLE

            if (commissionHistories.isNotEmpty()) {
                for (commissionHistory in commissionHistories) {
                    if (commissionHistory.statusCode.equals("ACC_AdvancePaid.Open")) {
                        binding.btnPayoutRequest.visibility = View.GONE
                    } else if (commissionHistory.statusCode.equals("ACC_AdvancePaid.Approved") && !prefHelper.isSupervisorOrInspector()) {
                        binding.llNewCancel.visibility = View.GONE
                    } else {
                        binding.btnPayoutCancel.visibility = View.GONE
                    }
                    break
                }
            } else {
                binding.btnPayoutCancel.visibility = View.GONE
            }
        }
    }

    private fun initialiseListeners() {

       /* binding.btnPayoutRequest.setOnClickListener {
            val mCommissionDetails = CommissionDetails()
            mCommissionDetails.advanceDate = formatDateTimeInMillisecond(Date())
            mCommissionDetails.accountId = prefHelper.accountId
            mCommissionDetails.netPayable = 0.0
            mCommissionDetails.statusCode = "ACC_AdvancePaid.Open"
            insertAgentCommission(mCommissionDetails, 0)
        }*/

        binding.btnPayoutRequest.setOnClickListener (object: OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val mCommissionDetails = CommissionDetails()
                mCommissionDetails.advanceDate = formatDateTimeInMillisecond(Date())
                mCommissionDetails.accountId = prefHelper.accountId
                mCommissionDetails.netPayable = 0.0
                mCommissionDetails.statusCode = "ACC_AdvancePaid.Open"
                insertAgentCommission(mCommissionDetails, 0)
            }
        })

        binding.btnPayoutCancel.setOnClickListener (object: OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val commissionDetails = CommissionDetails()
                for (commissionHistory in commissionHistories) {
                    if (commissionHistory.statusCode.equals("ACC_AdvancePaid.Open")) {
                        commissionDetails.advanceDate = formatDateTimeInMillisecond(Date())
                        commissionDetails.accountId = prefHelper.accountId
                        commissionDetails.referenceNo = commissionHistory.referenceNo
                        commissionDetails.referanceDate = commissionHistory.referanceDate
                        commissionDetails.netPayable = commissionHistory.netPayable
                        commissionDetails.advancePaidId = commissionHistory.advancePaidId
                        commissionDetails.approvedDate = commissionHistory.approvedDate
                        commissionDetails.approvedByAccountId = commissionHistory.approvedByAccountId
                        commissionDetails.statusCode = "ACC_AdvancePaid.Cancelled"
                        break
                    }
                }
                insertAgentCommission(commissionDetails, 0)
            }
        })

     /*   binding.btnPayoutCancel.setOnClickListener {
            val commissionDetails = CommissionDetails()
            for (commissionHistory in commissionHistories) {
                if (commissionHistory.statusCode.equals("ACC_AdvancePaid.Open")) {
                    commissionDetails.advanceDate = formatDateTimeInMillisecond(Date())
                    commissionDetails.accountId = prefHelper.accountId
                    commissionDetails.referenceNo = commissionHistory.referenceNo
                    commissionDetails.referanceDate = commissionHistory.referanceDate
                    commissionDetails.netPayable = commissionHistory.netPayable
                    commissionDetails.advancePaidId = commissionHistory.advancePaidId
                    commissionDetails.approvedDate = commissionHistory.approvedDate
                    commissionDetails.approvedByAccountId = commissionHistory.approvedByAccountId
                    commissionDetails.statusCode = "ACC_AdvancePaid.Cancelled"
                    break
                }
            }
            insertAgentCommission(commissionDetails, 0)
        }*/
    }

    private fun insertAgentCommission(commissionDetails: CommissionDetails?, position: Int) {
        listener?.showProgressDialog()
        APICall.insertAgentCommission(commissionDetails, object : ConnectionCallBack<String> {
            override fun onSuccess(response: String) {
                var message: String = response
                if (prefHelper.agentTypeCode == Constant.AgentTypeCode.ASO.name && prefHelper.allowCombinedPayoutRequest) {
                    message = context?.resources?.getString(R.string.msg_commission_generated_successfully_associate_associate_agent).toString()
                } else {
                    if (message == ("")) {
                        when (commissionDetails?.statusCode) {
                            "ACC_AdvancePaid.Open" -> message = getString(R.string.msg_commission_generated_successfully)
                            "ACC_AdvancePaid.Cancelled" -> message = getString(R.string.msg_request_cancelled)
                            "ACC_AdvancePaid.Approved" -> message = getString(R.string.approved_successfully)
                            "ACC_AdvancePaid.Rejected" -> message = getString(R.string.rejected)
                        }
                    }
                }

                listener?.dismissDialog()

                listener?.showAlertDialog(message, DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    if (isSupervisorOrInspector && commissionDetails?.statusCode == "ACC_AdvancePaid.Approved") {
                        bindData(true, position)
                    } else
                        bindData(false, position)
                })
            }

            override fun onFailure(message: String) {
                listener?.dismissDialog()
                listener?.showAlertDialog(message)
            }
        })
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val commissionHistory = obj as CommissionHistory
        val commissionDetails = CommissionDetails()
        commissionDetails.advanceDate = formatDateTimeInMillisecond(commissionHistory.advanceDate)
        commissionDetails.accountId = commissionHistory.accountId
        commissionDetails.referenceNo = commissionHistory.referenceNo
        commissionDetails.referanceDate = commissionHistory.referanceDate
        commissionDetails.netPayable = commissionHistory.netPayable
        commissionDetails.advancePaidId = commissionHistory.advancePaidId

        when (view.id) {
            R.id.btnPayoutApprove -> {
                commissionDetails.statusCode = "ACC_AdvancePaid.Approved"
                commissionDetails.approvedByAccountId = prefHelper.accountId
                commissionDetails.approvedDate = formatDateTimeInMillisecond(Date())
                insertAgentCommission(commissionDetails, position)
            }

            R.id.btnPayoutReject -> {
                val layoutInflater = LayoutInflater.from(context)
                val binding = DataBindingUtil.inflate<FragmentRejectionRemarksBinding>(layoutInflater, R.layout.fragment_rejection_remarks, null, false)
                listener?.showAlertDialog(resources.getString(R.string.remarks), DialogInterface.OnClickListener { dialog, _ ->
                    if (binding.edtRemarks.text.toString().isNotEmpty()) {
                        commissionDetails.remarks = binding.edtRemarks.text.toString()
                        dialog?.dismiss()
                        commissionDetails.approvedByAccountId = prefHelper.accountId
                        commissionDetails.approvedDate = formatCurrentDateTime(Date())
                        commissionDetails.statusCode = "ACC_AdvancePaid.Rejected"
                        insertAgentCommission(commissionDetails, position)
                    } else {
                        listener?.showSnackbarMsg(getString(R.string.msg_enter_remarks))
                    }
                }, null, binding.root)
            }

            R.id.btnPrint -> {
                listener?.printAgentDetails(commissionHistory)
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    interface Listener {
        fun finish()
        fun popBackStack()
        fun showProgressDialog()
        fun dismissDialog()
        fun showSnackbarMsg(message: String?)
        fun showAlertDialog(message: String)
        fun showAlertDialog(msg: String, okListener: DialogInterface.OnClickListener)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun printAgentDetails(commissionDetails: CommissionHistory)
        fun showAlertDialog(msg: String, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener?, view: View)
    }

}