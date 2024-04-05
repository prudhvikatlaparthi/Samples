package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetIndividualBusinessTransactionHistory
import com.sgs.citytax.api.payload.GetIndividualTaxNoticeHistory
import com.sgs.citytax.api.payload.SearchFilter
import com.sgs.citytax.api.response.BusinessTransaction
import com.sgs.citytax.api.response.GetIndividualBusinessTransactionHistoryResults
import com.sgs.citytax.api.response.GetSearchIndividualTaxDetails
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentBusinessTransactionHistoryBinding
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.adapter.BusinessTransactionHistoryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_ADVANCE_RECEIVED_ID
import com.sgs.citytax.util.Constant.KEY_TAX_RULE_BOOK_CODE
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.getString

class BusinessTransactionHistoryFragment : BaseFragment(), IClickListener {

    private lateinit var binding: FragmentBusinessTransactionHistoryBinding
    var pageIndex: Int = 1
    val pageSize: Int = 10
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    private var adapter: BusinessTransactionHistoryAdapter? = null
    private var mListener: Listener? = null
    private lateinit var mSycoTaxID: String
    private var mCode: Constant.QuickMenu ?= null
    private var getSearchIndividualTaxDetails : GetSearchIndividualTaxDetails ?= null

    companion object {
        @JvmStatic
        fun newInstance(sycoTaxID: String,fromScreenMode: Constant.QuickMenu,getSearchIndividualTaxDetail : GetSearchIndividualTaxDetails?) = BusinessTransactionHistoryFragment().apply {
            mSycoTaxID = sycoTaxID
            mCode = fromScreenMode
            getSearchIndividualTaxDetails = getSearchIndividualTaxDetail
        }
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_transaction_history, container, false)
        initComponents()
        return binding.root
    }

    override fun initComponents() {
        initViews()
        initEvents()
        bindData()
    }

    private fun initViews() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = BusinessTransactionHistoryAdapter(this)
        binding.recyclerView.adapter = adapter
    }

    private fun initEvents() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = recyclerView.layoutManager!!.childCount
                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount: Int = linearLayoutManager.itemCount
                val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && hasMoreData && !isLoading) {
                    bindData()
                }
            }
        })
    }

    private fun bindData() {
        if(mCode == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_BUSINESS_TRANSACTION_HISTORY){
            mListener?.showProgressDialog()
            isLoading = true
            val getIndividualTaxNoticeHistory = GetIndividualBusinessTransactionHistory()
            getIndividualTaxNoticeHistory.sycoTaxID = getSearchIndividualTaxDetails?.sycotaxID
            getIndividualTaxNoticeHistory.pagesize = pageSize
            getIndividualTaxNoticeHistory.pageindex = pageIndex

            APICall.getIndividualTaxTransactions(getIndividualTaxNoticeHistory, object : ConnectionCallBack<GetIndividualBusinessTransactionHistoryResults> {
                override fun onSuccess(response:GetIndividualBusinessTransactionHistoryResults) {
                    mListener?.dismissDialog()

                    if (response.businessTransactionHistoryList.size > 0) {
                        response.businessTransactionHistoryList.let {
                            for ((index, value) in it.withIndex()) {
                                it[index].sycoTaxID = mSycoTaxID
                            }
                        }
                        adapter?.addAll(response.businessTransactionHistoryList)
                    }else {
                        mListener?.showAlertDialog(getString(R.string.msg_no_data), DialogInterface.OnClickListener { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            activity?.finish()
                        })
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })

        }else {
            val searchFilter = SearchFilter()
            searchFilter.pageSize = pageSize
            searchFilter.pageIndex = pageIndex
            searchFilter.filterColumns = arrayListOf("SycotaxID")
            searchFilter.query = mSycoTaxID

            val transaction = BusinessTransaction()
            transaction.isLoading = true
            adapter?.add(transaction)
            isLoading = true

            APICall.getBusinessTransactionHistory(searchFilter, object : ConnectionCallBack<List<BusinessTransaction>> {
                override fun onSuccess(response: List<BusinessTransaction>) {
                    val count: Int = response.size
                    if (count < pageSize) {
                        hasMoreData = false
                    } else
                        pageIndex += 1
                    adapter?.remove(transaction)
                    adapter?.addAll(response)
                    isLoading = false
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    adapter?.remove(transaction)
                    isLoading = false
                }
            })
        }
    }

    interface Listener {
        fun finish()
        fun showProgressDialog()
        fun popBackStack()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val businessTransaction: BusinessTransaction = obj as BusinessTransaction
        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
        intent.putExtra(KEY_ADVANCE_RECEIVED_ID, businessTransaction.advancerecievedid)
        intent.putExtra(KEY_TAX_RULE_BOOK_CODE, businessTransaction.taxRuleBookCode)
        startActivity(intent)
        MyApplication.getPrefHelper().isFromHistory = true
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}