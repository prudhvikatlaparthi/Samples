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
import com.sgs.citytax.api.response.LicenseRenewalResp
import com.sgs.citytax.api.response.TransactionHistoryGenResp
import com.sgs.citytax.databinding.FragmentTransactionHistoryBinding
import com.sgs.citytax.model.TransactionHistoryGenModel
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.FragmentCommunicator
import com.sgs.citytax.ui.adapter.LicenseRenewalHistoryAdapter
import com.sgs.citytax.ui.adapter.PropertyTransactionHistoryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.KEY_ADVANCE_RECEIVED_ID
import com.sgs.citytax.util.Constant.KEY_TAX_RULE_BOOK_CODE
import com.sgs.citytax.util.IClickListener

class LicenseRenewalHistoryFragment : BaseFragment(), IClickListener {

    private lateinit var binding: FragmentTransactionHistoryBinding
    var pageIndex: Int = 1
    val pageSize: Int = 10
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    private var adapter: LicenseRenewalHistoryAdapter? = null
    private var mListener: FragmentCommunicator? = null
    private var mLicenseID: Int = 0
    private var mCode: Constant.QuickMenu? = null

    companion object {
        @JvmStatic
        fun newInstance(licenseID: Int, fromScreenMode: Constant.QuickMenu) = LicenseRenewalHistoryFragment().apply {
            mLicenseID = licenseID
            mCode = fromScreenMode

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
        //region getArguments
        arguments?.let {
            mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mLicenseID = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
        }

        initViews()
        initEvents()
        bindData()
    }

    private fun initViews() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = LicenseRenewalHistoryAdapter(this)
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

            mListener?.showProgressDialog()
            isLoading = true

            APICall.getLicenseRenewalHistory(mLicenseID.toInt(), object : ConnectionCallBack<LicenseRenewalResp> {
                override fun onSuccess(response: LicenseRenewalResp) {
                    mListener?.dismissDialog()
                    if (response.transactions.size > 0) {
                        /*response.transactions.let {
                            for ((index, value) in it.withIndex()) {
                                it[index].sycoTaxID = mSycoTaxID
                            }
                        }*/
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

    }

    override fun onClick(view: View, position: Int, obj: Any) {

    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

}