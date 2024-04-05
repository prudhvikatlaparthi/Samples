package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.GetAgentTransactionDetails
import com.sgs.citytax.api.response.CRMAgentTransactionDetail
import com.sgs.citytax.api.response.CRMAgentTransactionResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.databinding.FragmentAgentCommissionReportBinding
import com.sgs.citytax.ui.adapter.AgentCommissionReportAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.util.*
import kotlin.collections.ArrayList

class AgentCommissionReportFragment : BaseFragment() {

    private lateinit var pagination: Pagination
    private var rootView: View? = null
    private lateinit var binding: FragmentAgentCommissionReportBinding
    private var listener: Listener? = null
    private var accountId: String? = ""

    internal var crmAgentTransactionList: MutableList<CRMAgentTransactionDetail> = ArrayList()
    private var prefHelper = MyApplication.getPrefHelper()

    private var menuPayout: MenuItem? = null
    private var fromDate: String? = null
    private var toDate: String? = null

    private val agentCommissionReportAdapter: AgentCommissionReportAdapter by lazy { AgentCommissionReportAdapter() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: Exception) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    fun handleBackClick() {
        listener?.finish()
        listener?.popBackStack()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_commission, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        menuPayout = menu.findItem(R.id.action_request_payout)
        menuPayout?.isVisible = false

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_filter) {
            showDateRangeSelection()
        } else if (id == R.id.action_request_payout) {
            listener?.replaceFragment(RequestForPayoutFragment(), true)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_agent_commission_report, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    override fun initComponents() {
        binding.rcvAgentCommissionReport.adapter = agentCommissionReportAdapter
        binding.rcvAgentCommissionReport.addItemDecoration(DividerItemDecoration(
                this@AgentCommissionReportFragment.context,
                LinearLayoutManager.VERTICAL))
        pagination = Pagination(1, 10, binding.rcvAgentCommissionReport) { pageNumber, PageSize ->
            bindData(pageindex = pageNumber, pageSize = PageSize)
        }
        bindData(callPayoutMenu = true)
    }

    private fun showDateRangeSelection() {
        val layoutInflater = LayoutInflater.from(activity)
        val binding = DataBindingUtil.inflate<FilterDateRangeBinding>(layoutInflater, R.layout.filter_date_range, date_dilog_linear_layout, false)
        val edtFromDate = binding.editTextFromDate
        val edtToDate = binding.editTextToDate
        edtToDate.isEnabled = false
        edtToDate.setDisplayDateFormat(displayDateFormat)
        edtFromDate.setDisplayDateFormat(displayDateFormat)
        edtFromDate.setText(fromDate)
        edtToDate.setText(toDate)
        binding.editTextFromDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                edtFromDate.text?.toString()?.let {
                    if (it.isNotEmpty()) {
                        edtToDate.isEnabled = true
                        edtToDate.setText("")
                        edtToDate.setMinDate(parseDate(it, displayDateFormat).time)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.txtInpLayFromDate.error = null
                }
            }
        })

        binding.editTextToDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    binding.txtInpLayToDate.error = null
                }
            }
        })
        listener?.showAlertDialog(R.string.title_select_date, R.string.ok, View.OnClickListener {
            val dialog = (it as Button).tag as AlertDialog
            var isValid = true
            fromDate = edtFromDate.text.toString()
            toDate = edtToDate.text.toString()
            if (TextUtils.isEmpty(fromDate)) {
                isValid = false
                binding.txtInpLayFromDate.error = getString(R.string.msg_from_date)
                edtFromDate.requestFocus()
            } else if (TextUtils.isEmpty(toDate)) {
                isValid = false
                binding.txtInpLayToDate.error = getString(R.string.msg_to_date)
                edtToDate.requestFocus()
            }
            if (isValid) {
                bindData()
                dialog.dismiss()
            }
        }, 0, null, R.string.cancel, View.OnClickListener {
            val dialog = (it as Button).tag as AlertDialog
            dialog.dismiss()
        }, binding.root)

    }

    fun bindData(pageindex: Int = 1, pageSize: Int = 10, callPayoutMenu: Boolean = false) {
        if (pageindex == 1) {
            binding.rcvAgentCommissionReport.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            resetRecyclerAdapter()
            listener?.showProgressDialog()
        } else {
            binding.ProgressBar.isVisible = true
        }
        accountId = prefHelper.accountId.toString()
        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
            val cal = Calendar.getInstance(Locale.getDefault())
            cal.add(Calendar.DAY_OF_YEAR, -1)
            fromDate = getDate(cal.time, displayDateFormat)
            toDate = getDate(Date(), displayDateFormat)
        }
        val getAgentTransactionDetails = GetAgentTransactionDetails(context = SecurityContext(), fromDate = serverFormatDate(fromDate),
                toDate = serverFormatDate(toDate), acctid = accountId, pageindex = pageindex, pageSize = pageSize)
        APICall.getAgentTransactionDetailsNew(getAgentTransactionDetails, object : ConnectionCallBack<CRMAgentTransactionResponse> {
            override fun onSuccess(response: CRMAgentTransactionResponse) {
                response.totalRecordsCount?.let {
                    pagination.totalRecords = it
                }
                if (response.agentTransactionsList?.size ?: 0 > 0) {
                    pagination.stopPagination(response.agentTransactionsList?.size!!)
                    response.agentTransactionsList?.let { data ->
                        crmAgentTransactionList.addAll(data)
                        if (crmAgentTransactionList.isNotEmpty()) {
                            agentCommissionReportAdapter.differ.submitList(crmAgentTransactionList)
                            agentCommissionReportAdapter.notifyDataSetChanged()
                            pagination.setIsScrolled(false)
                        }
                    }
                } else {
                    pagination.stopPagination(0)
                    if (pageindex == 1) {
                        resetRecyclerAdapter()
                        listener?.showAlertDialog(getString(R.string.msg_no_data))
                    }
                }

                if (callPayoutMenu) {
                    showPayOutMenu(accountId?.toInt())
                }
                listener?.dismissDialog()
                binding.ProgressBar.isVisible = false
            }

            override fun onFailure(message: String) {
                if (pageindex == 1) {
                    resetRecyclerAdapter()
                    listener?.dismissDialog()
                    if (message.isNotEmpty())
                        listener?.showAlertDialog(message)
                }
                if (callPayoutMenu) {
                    showPayOutMenu(accountId?.toInt())
                }
                binding.ProgressBar.isVisible = false
            }
        })
    }

    private fun resetRecyclerAdapter() {
        crmAgentTransactionList.clear()
        agentCommissionReportAdapter.differ.submitList(crmAgentTransactionList)
        agentCommissionReportAdapter.notifyDataSetChanged()
    }

    private fun showPayOutMenu(accountId: Int?) {

        APICall.getAgentCommissionBalance(accountId ?: 0, object : ConnectionCallBack<Double> {
            override fun onSuccess(response: Double) {
                val commissionBalance: Double?
                commissionBalance = response
                menuPayout?.isVisible = commissionBalance > 0
            }

            override fun onFailure(message: String) {

            }
        })
    }

    interface Listener {
        fun finish()
        fun popBackStack()
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    }

}