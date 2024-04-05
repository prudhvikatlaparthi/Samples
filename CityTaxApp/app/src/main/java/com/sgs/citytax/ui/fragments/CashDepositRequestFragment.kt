package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.CRMAgentTransactionDetail
import com.sgs.citytax.api.response.CRMAgentTransactionResponse
import com.sgs.citytax.api.response.CashDepositResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentCashDepositRequestBinding
import com.sgs.citytax.model.VUAgentCashCollectionSummary
import com.sgs.citytax.ui.adapter.AgentCollectionHistoryAdapter
import com.sgs.citytax.ui.adapter.CashDepositAdapter
import com.sgs.citytax.util.LogHelper
import com.sgs.citytax.util.Pagination
import com.sgs.citytax.util.formatWithPrecision

class CashDepositRequestFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mBinding: FragmentCashDepositRequestBinding
    private var mListener: Listener? = null
    var pageIndex: Int = 1
    val pageSize: Int = 100

    private lateinit var pagination: Pagination
    internal var vuAgentCashCollectionSummary: MutableList<VUAgentCashCollectionSummary> = ArrayList()
    private val cashDepositAdapter: CashDepositAdapter by lazy {
        CashDepositAdapter(vuAgentCashCollectionSummary)
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cash_deposit_request, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun initComponents() {
        pagination = Pagination(1, 10, mBinding.rcvCashDepositRequest) { pageNumber, pageSize ->
            bindData(mPageindex =  pageNumber, mPageSize = pageSize)
        }
        pagination.setDefaultValues()
        getCashInHand()
//        bindData()
        setListeners()
    }

    private fun getCashInHand() {

        val searchFilter = AdvanceSearchFilter()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex
        searchFilter.query = null

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        val filterColumn = FilterColumn()

        filterColumn.columnName = "AccountID"
        filterColumn.columnValue = MyApplication.getPrefHelper().accountId.toString()
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)

        searchFilter.filterColumns = listFilterColumn

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_AgentCashCollectionSummary"
        tableDetails.primaryKeyColumnName = "AccountID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "OR"

        searchFilter.tableDetails = tableDetails

        mListener?.showProgressDialog()
        APICall.getCashDepositHistory(searchFilter, object : ConnectionCallBack<CashDepositResponse> {
            override fun onSuccess(response: CashDepositResponse) {
                response.results.let { results ->
                    results?.vuAgentCashCollectionSummaries.let {
                        it.let {
                            it?.forEach {
                                mBinding.tvCashInHand.text = formatWithPrecision(it.cashInHand)
                            }
                        }
                    }
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

//    private fun bindData() {
//        val searchFilter = AdvanceSearchFilter()
//        searchFilter.pageSize = pageSize
//        searchFilter.pageIndex = pageIndex

//        searchFilter.query = null
//
//        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
//        val filterColumn = FilterColumn()
//
//        filterColumn.columnName = "AccountID"
//        filterColumn.columnValue = MyApplication.getPrefHelper().accountId.toString()
//        filterColumn.srchType = "equal"
//        listFilterColumn.add(listFilterColumn.size, filterColumn)
//
//        searchFilter.filterColumns = listFilterColumn
//
//        val tableDetails = TableDetails()
//        tableDetails.tableOrViewName = "VU_ACC_CashDeposit"
//        tableDetails.primaryKeyColumnName = "CashDepositID"
//        tableDetails.selectColoumns = ""
//        tableDetails.TableCondition = "OR"
//
//        searchFilter.tableDetails = tableDetails
//
//        mListener?.showProgressDialog()
//        APICall.getCashDepositHistory(searchFilter, object : ConnectionCallBack<CashDepositResponse> {
//            override fun onSuccess(response: CashDepositResponse) {
//                response.results.let { results ->
//                    results?.vuAgentCashCollectionSummaries?.let {
//                        it.reverse()
//                        mBinding.rcvCashDepositRequest.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
//                        mBinding.rcvCashDepositRequest.adapter = CashDepositAdapter(it)
//                    }
//                }
//                /*if (response.results != null && !response.results?.vuAgentCashCollectionSummaries.isNullOrEmpty()) {
//                    mBinding.rcvCashDepositRequest.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
//                    response.results.vuAgentCashCollectionSummaries.reverse()
//                    mBinding.rcvCashDepositRequest.adapter = CashDepositAdapter(response.results.vuAgentCashCollectionSummaries)
//                }*/
//                mListener?.dismissDialog()
//            }
//
//            override fun onFailure(message: String) {
//                mListener?.dismissDialog()
//                mListener?.showAlertDialog(message)
//            }
//        })
//
//    }

    private fun bindData(mPageindex: Int = 1, mPageSize: Int = 10) {
        val searchFilter = AdvanceSearchFilter()
        searchFilter.pageSize = mPageSize
        searchFilter.pageIndex = mPageindex
        searchFilter.query = null

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        val filterColumn = FilterColumn()

        filterColumn.columnName = "AccountID"
        filterColumn.columnValue = MyApplication.getPrefHelper().accountId.toString()
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)

        searchFilter.filterColumns = listFilterColumn

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_ACC_CashDeposit"
        tableDetails.primaryKeyColumnName = "CashDepositID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "OR"

        searchFilter.tableDetails = tableDetails
        if (mPageindex == 1) {
            mBinding.rcvCashDepositRequest.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            resetRecyclerAdapter()
        } else {
            mBinding.ProgressBar.isVisible = true
        }

        mListener?.showProgressDialog()
        APICall.getCashDepositHistory(searchFilter, object : ConnectionCallBack<CashDepositResponse> {
            override fun onSuccess(response: CashDepositResponse) {
                try {
                    if (mPageindex == 1) {
                        response.totalRecordsCount?.let {
                            pagination.totalRecords = it
                        }
                    }
                    if (response.results?.vuAgentCashCollectionSummaries?.size ?: 0 > 0) {
                        pagination.stopPagination(response.results?.vuAgentCashCollectionSummaries?.size!!)
                        response.results?.vuAgentCashCollectionSummaries?.let { dataList ->
                            vuAgentCashCollectionSummary.addAll(dataList.reversed())
                            cashDepositAdapter.differ.submitList(vuAgentCashCollectionSummary)
                            cashDepositAdapter.notifyDataSetChanged()
                            mBinding.rcvCashDepositRequest.adapter = cashDepositAdapter
                            pagination.setIsScrolled(false)
                            if (vuAgentCashCollectionSummary.isEmpty()) {
                                mBinding.txtNoDataFound.visibility = View.VISIBLE
                                mBinding.rcvCashDepositRequest.visibility = View.GONE
                            } else {
                                mBinding.txtNoDataFound.visibility = View.GONE
                                mBinding.rcvCashDepositRequest.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        pagination.stopPagination(0)
                        if (mPageindex == 1) {
                            resetRecyclerAdapter()
                            mBinding.rcvCashDepositRequest.visibility = View.GONE
                            mBinding.txtNoDataFound.visibility = View.VISIBLE
                        }
                    }

                } catch (ex: Exception) {
                    LogHelper.writeLog(exception = ex)
                }
                mListener?.dismissDialog()
                mBinding.ProgressBar.isVisible = false
            }

            override fun onFailure(message: String) {
                if (mPageindex == 1) {
                    resetRecyclerAdapter()
//                        menu?.removeGroup(1)
                    if (vuAgentCashCollectionSummary.isEmpty()) {
                        mBinding.rcvCashDepositRequest.visibility = View.GONE
                        mBinding.txtNoDataFound.visibility = View.VISIBLE
                    }
                }
                mListener?.dismissDialog()
                mBinding.ProgressBar.isVisible = false
            }
        })

    }

    private fun resetRecyclerAdapter() {
        vuAgentCashCollectionSummary.clear()
        cashDepositAdapter.differ.submitList(vuAgentCashCollectionSummary)
        cashDepositAdapter.notifyDataSetChanged()
    }

    private fun setListeners() {
        mBinding.btnNewRequest.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view.let {
            when (view?.id) {
                R.id.btnNewRequest -> {
                    mListener?.showProgressDialog()
                    val accountId = MyApplication.getPrefHelper().accountId
                    APICall.insertRequestCashDeposit(accountId, object : ConnectionCallBack<String> {
                        override fun onSuccess(response: String) {
                            if (response.isNotEmpty()) {
                                mListener?.dismissDialog()
                                mListener?.showAlertDialog(getString(R.string.msg_cash_deposit_request), DialogInterface.OnClickListener { dialog, p1 ->
                                    dialog?.dismiss()
                                    mListener?.finish()
                                })

                            }
                        }

                        override fun onFailure(message: String) {
                            mListener?.dismissDialog()
                            mListener?.showAlertDialog(message)
                        }
                    })
                }
            }
        }
    }

    interface Listener {
        fun finish()
        fun popBackStack()
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: String, onClickListener: DialogInterface.OnClickListener)
    }


}