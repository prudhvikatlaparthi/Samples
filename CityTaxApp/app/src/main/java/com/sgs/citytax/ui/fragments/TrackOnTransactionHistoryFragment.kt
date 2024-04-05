package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.sgs.citytax.api.payload.GetTrackOnTransactionHistory
import com.sgs.citytax.api.response.TrackOnTransaction
import com.sgs.citytax.api.response.TrackOnTransactionHistory
import com.sgs.citytax.databinding.FragmentTrackonTransactionHistoryBinding
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.adapter.TrackOnTransactionHistoryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_ADVANCE_RECEIVED_ID
import com.sgs.citytax.util.Constant.KEY_TAX_RULE_BOOK_CODE
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.Pagination

class TrackOnTransactionHistoryFragment : BaseFragment(), IClickListener {

    private lateinit var binding: FragmentTrackonTransactionHistoryBinding
    private lateinit var adapter: TrackOnTransactionHistoryAdapter
    private var mListener: Listener? = null
    private lateinit var pagination: Pagination
    private val resultList: MutableList<TrackOnTransaction> = mutableListOf()

    companion object {
        @JvmStatic
        fun newInstance() = TrackOnTransactionHistoryFragment().apply {

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trackon_transaction_history, container, false)
        initComponents()
        return binding.root
    }

    override fun initComponents() {
        initViews()
        bindData()
    }

    private fun initViews() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = TrackOnTransactionHistoryAdapter(this)
        binding.recyclerView.adapter = adapter
        pagination = Pagination(1, 10, binding.recyclerView) { pageNumber, PageSize ->
            bindData(pageNumber, pageSize = PageSize)
        }
    }

    private fun bindData(pageindex: Int = 1, pageSize: Int = 10) {
        if (pageindex == 1) {
            resultList.clear()
            binding.recyclerView.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            mListener?.showProgressDialog()
        } else {
            binding.ProgressBar.isVisible = true
        }
        val history = GetTrackOnTransactionHistory(pageIndex = pageindex, pageSize = pageSize)
        APICall.getTrackOnTransactionHistory(history, object : ConnectionCallBack<TrackOnTransactionHistory> {
            override fun onSuccess(response: TrackOnTransactionHistory) {
                mListener?.dismissDialog()
                response.totalSearchedRecords?.let {
                    pagination.totalRecords = it
                }
                if (response.results?.transactions?.size ?: 0 > 0) {
                    response.results?.transactions?.let {
                        resultList.addAll(it)
                        pagination.stopPagination(it.size)
                        adapter.differ.submitList(resultList)
                        adapter.notifyDataSetChanged()
                        pagination.setIsScrolled(false)
                    }
                } else {
                    pagination.stopPagination(0)
                    if (pageindex == 1) {
                        resetRecyclerAdapter()
                        mListener?.showAlertDialog(getString(R.string.msg_no_data))
                    }
                }
                binding.ProgressBar.isVisible = false
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (pageindex == 1) {
                    mListener?.showAlertDialog(message)
                }
                binding.ProgressBar.isVisible = false
            }
        })

    }

    private fun resetRecyclerAdapter() {
        resultList.clear()
        adapter.differ.submitList(resultList)
        adapter.notifyDataSetChanged()
    }

    interface Listener {
        fun finish()
        fun showProgressDialog()
        fun popBackStack()
        fun dismissDialog()
        fun showAlertDialog(message: String)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val trackOnTransaction: TrackOnTransaction = obj as TrackOnTransaction
        val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
        intent.putExtra(Constant.KEY_STOP_API_4_PRINT_ALLOW, trackOnTransaction.allowAutoReceiptPrint == "Y")
        intent.putExtra(KEY_ADVANCE_RECEIVED_ID, trackOnTransaction.advancerecievedid)
        intent.putExtra(KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.TICKET_PAYMENT_TRANSACTION.Code)
        startActivity(intent)
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}