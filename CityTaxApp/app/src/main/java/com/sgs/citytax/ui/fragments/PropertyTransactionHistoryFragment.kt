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
import com.sgs.citytax.api.response.TransactionHistoryGenResp
import com.sgs.citytax.databinding.FragmentTransactionHistoryBinding
import com.sgs.citytax.model.TransactionHistoryGenModel
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.FragmentCommunicator
import com.sgs.citytax.ui.adapter.PropertyTransactionHistoryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_ADVANCE_RECEIVED_ID
import com.sgs.citytax.util.Constant.KEY_TAX_RULE_BOOK_CODE
import com.sgs.citytax.util.IClickListener

class PropertyTransactionHistoryFragment : BaseFragment(), IClickListener {

    private lateinit var binding: FragmentTransactionHistoryBinding
    var pageIndex: Int = 1
    val pageSize: Int = 10
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    private var adapter: PropertyTransactionHistoryAdapter? = null
    private var mListener: FragmentCommunicator? = null
    private lateinit var mSycoTaxID: String
    private var mCode: Constant.QuickMenu? = null
    private var vuComProperties: VuComProperties? = null

    companion object {
        @JvmStatic
        fun newInstance(sycoTaxID: String, fromScreenMode: Constant.QuickMenu, getVuComProperties: VuComProperties?) = PropertyTransactionHistoryFragment().apply {
            mSycoTaxID = sycoTaxID
            mCode = fromScreenMode
            vuComProperties = getVuComProperties
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as FragmentCommunicator
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_history, container, false)
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
        adapter = PropertyTransactionHistoryAdapter(this)
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
        if (mCode == Constant.QuickMenu.QUICK_MENU_PROPERTY_TRANSACTION_HISTORY
                || mCode == Constant.QuickMenu.QUICK_MENU_LAND_TRANSACTION_HISTORY) {
            mListener?.showProgressDialog()
            isLoading = true
//            val getIndividualTaxNoticeHistory = GetIndividualBusinessTransactionHistory()
//            if (getSearchIndividualTaxDetails != null) {
//                getIndividualTaxNoticeHistory.sycoTaxID = getSearchIndividualTaxDetails?.sycotaxID
//            } else {
//                getIndividualTaxNoticeHistory.sycoTaxID = mSycoTaxID
//            }
//            getIndividualTaxNoticeHistory.pagesize = 10
//            getIndividualTaxNoticeHistory.pageindex = 1

            APICall.getPropertyTaxTransactions(vuComProperties?.propertySycotaxID, object : ConnectionCallBack<TransactionHistoryGenResp> {
                override fun onSuccess(response: TransactionHistoryGenResp) {
                    mListener?.dismissDialog()
                    if (response.transactions.size > 0) {
                        response.transactions.let {
                            for ((index, value) in it.withIndex()) {
                                it[index].sycoTaxID = mSycoTaxID
                            }
                        }
                        adapter?.addAll(response.transactions)
                    } else {
                        mListener?.showAlertDialogFailure("", R.string.msg_no_data, DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                            mListener?.finish()
                        })
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialogFailure("", R.string.msg_no_data, DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                        mListener?.finish()
                    })
                }
            })

        } else {
//            val searchFilter = SearchFilter()
//            searchFilter.pageSize = pageSize
//            searchFilter.pageIndex = pageIndex
//            searchFilter.filterColumns = arrayListOf("SycotaxID")
//            searchFilter.query = mSycoTaxID
//
//            val transaction = TransactionHistoryGenModel()
//            transaction.isLoading = true
//            adapter?.add(transaction)
//            isLoading = true
//
//            APICall.getBusinessTransactionHistory(searchFilter, object : ConnectionCallBack<List<TransactionHistoryGenModel>> {
//                override fun onSuccess(response: List<TransactionHistoryGenModel>) {
//                    val count: Int = response.size
//                    if (count < pageSize) {
//                        hasMoreData = false
//                    } else
//                        pageIndex += 1
//                    adapter?.remove(transaction)
//                    adapter?.addAll(response)
//                    isLoading = false
//                }
//
//                override fun onFailure(message: String) {
//                    mListener?.dismissDialog()
//                    mListener?.showAlertDialog(message)
//                    adapter?.remove(transaction)
//                    isLoading = false
//                }
//            })
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val businessTransaction: TransactionHistoryGenModel = obj as TransactionHistoryGenModel
        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
        intent.putExtra(KEY_ADVANCE_RECEIVED_ID, businessTransaction.advancerecievedid)
        intent.putExtra(KEY_TAX_RULE_BOOK_CODE, businessTransaction.taxRuleBookCode)
        startActivity(intent)
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}