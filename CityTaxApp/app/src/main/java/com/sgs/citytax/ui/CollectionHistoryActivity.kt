package com.sgs.citytax.ui

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.GetAgentTransactionDetails
import com.sgs.citytax.api.response.CRMAgentTransactionDetail
import com.sgs.citytax.api.response.CRMAgentTransactionResponse
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication.getPrefHelper
import com.sgs.citytax.databinding.ActivityCollectionHistroyBinding
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.databinding.FragmentCollectionHistroyBinding
import com.sgs.citytax.ui.adapter.AgentCollectionHistoryAdapter
import com.sgs.citytax.ui.fragments.BaseFragment
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CollectionHistoryActivity : BaseActivity() {
    private var binding: ActivityCollectionHistroyBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collection_histroy)
        val bundle: Bundle? = intent.extras
        if (intent.hasExtra("mode")) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, CollectionHistoryFragment.newInstance(prefHelper.agentCollectionFromDate, prefHelper.agentCollectionToDate, bundle?.getBoolean("mode", false)!!, bundle.getString("s_agent_acctid", null)!!))
                    .commit()
        } else {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, CollectionHistoryFragment.newInstance(prefHelper.agentCollectionFromDate, prefHelper.agentCollectionToDate, false, null))
                    .commit()
        }

    }

    class CollectionHistoryFragment : BaseFragment() {
        private var selectedTaxtypefilter: String? = null
        private var rootView: View? = null
        private lateinit var binding: FragmentCollectionHistroyBinding
        private var parentActivity: CollectionHistoryActivity? = null
        internal var crmAgentTransactionList: MutableList<CRMAgentTransactionDetail> = ArrayList()

        private var fromDate: String? = null
        private var toDate: String? = null
        private var s_agent_acctid: String? = null
        private var mode: Boolean? = false

        private var menu: Menu? = null

        private lateinit var pagination: Pagination

        private val agentCollectionHistoryAdapter: AgentCollectionHistoryAdapter by lazy {
            AgentCollectionHistoryAdapter()
        }

        companion object {
            @JvmStatic
            fun newInstance(fromDate: String, toDate: String, mode: Boolean, s_agent_acctid: String?) = CollectionHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString("fromDate", fromDate)
                    putString("toDate", toDate)
                    putBoolean("mode", mode)
                    putString("s_agent_acctid", s_agent_acctid)
                }
            }
        }

        override fun onAttach(context: Context) {
            super.onAttach(context)
            arguments?.getString("fromDate")?.let {
                fromDate = it
            }
            arguments?.getString("toDate")?.let {
                toDate = it
            }
            arguments?.getString("s_agent_acctid")?.let {
                s_agent_acctid = it
            }
            arguments?.getBoolean("mode")?.let {
                mode = it
            }

        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_collection_histroy, container, false)
            rootView = binding.root
            initComponents()
            return rootView
        }

        override fun initComponents() {
            if (getPrefHelper().agentCollectionFromDate.isEmpty() && getPrefHelper().agentCollectionToDate.isEmpty()) {
                showDateRangeSelection()
            } else {
                bindData()
            }
            binding.rcvAgentCollectionHistroy.adapter = agentCollectionHistoryAdapter
            /*  binding.swpRefreshLayout.setOnRefreshListener {
                  bindData()
                  binding.swpRefreshLayout.isRefreshing = false
              }*/
            pagination = Pagination(1, 10, binding.rcvAgentCollectionHistroy) { pageNumber, PageSize ->
                bindData(pageNumber, pageSize = PageSize)
            }
            binding.rcvAgentCollectionHistroy.addItemDecoration(DividerItemDecoration(
                    this@CollectionHistoryFragment.context,
                    LinearLayoutManager.VERTICAL
            ))
        }

        private fun bindData(pageindex: Int = 1, pageSize: Int = 10) {
            if (pageindex == 1) {
                binding.rcvAgentCollectionHistroy.scrollToPosition(0)
                pagination.resetInitialPageNumber()
                resetRecyclerAdapter()
                parentActivity?.showProgressDialog(R.string.msg_please_wait)
            } else {
                binding.ProgressBar.isVisible = true
            }
            val getAgentTransactionDetails = GetAgentTransactionDetails(context = SecurityContext(), fromDate = serverFormatDate(fromDate),
                    toDate = serverFormatDate(toDate), acctid = s_agent_acctid, pageindex = pageindex, pageSize = pageSize, taxtypefilter = selectedTaxtypefilter)
            APICall.getAgentTransactionDetailsNew(getAgentTransactionDetails, object : ConnectionCallBack<CRMAgentTransactionResponse> {
                override fun onSuccess(response: CRMAgentTransactionResponse) {
                    try {
                        if (pageindex == 1) {
                            response.totalRecordsCount?.let {
                                pagination.totalRecords = it
                            }

                            if(response.totalCollectionAmount != null){
                                binding.tvTotalCollectionAmount.text = formatWithPrecision(response.totalCollectionAmount)
                            }else{
                                binding.tvTotalCollectionAmount.text = formatWithPrecision(0.0)
                            }
                        }

                        if (response.agentTransactionsList?.size ?: 0 > 0) {
                            pagination.stopPagination(response.agentTransactionsList?.size!!)
                            response.agentTransactionsList?.let { dataList ->
                                crmAgentTransactionList.addAll(dataList)
                                agentCollectionHistoryAdapter.differ.submitList(crmAgentTransactionList)
                                agentCollectionHistoryAdapter.notifyDataSetChanged()
                                pagination.setIsScrolled(false)
                                if (crmAgentTransactionList.isEmpty()) {
                                    binding.txtNoDataFound.visibility = View.VISIBLE
                                } else
                                    binding.txtNoDataFound.visibility = View.GONE
                            }
                        } else {
                            pagination.stopPagination(0)
                            if (pageindex == 1) {
                                resetRecyclerAdapter()
                                binding.txtNoDataFound.visibility = View.VISIBLE
                            }
                        }

                        response.taxTypes?.let { list ->
                            if (list.isNotEmpty()) {
                                var index = 0
                                menu?.removeGroup(1)
                                for (it in list) {
                                    index++
                                    menu?.add(1, index, Menu.NONE, it.taxType)
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        LogHelper.writeLog(exception = ex)
                    }
                    parentActivity?.dismissDialog()
                    binding.ProgressBar.isVisible = false
                }

                override fun onFailure(message: String) {
                    if (pageindex == 1) {
                        resetRecyclerAdapter()
//                        menu?.removeGroup(1)
                        if (crmAgentTransactionList.isEmpty()) {
                            binding.txtNoDataFound.visibility = View.VISIBLE
                        }
                    }
                    parentActivity?.dismissDialog()
                    binding.ProgressBar.isVisible = false
                }
            })
        }

        private fun resetRecyclerAdapter() {
            crmAgentTransactionList.clear()
            agentCollectionHistoryAdapter.differ.submitList(crmAgentTransactionList)
            agentCollectionHistoryAdapter.notifyDataSetChanged()
        }

        override
        fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setHasOptionsMenu(true)
            parentActivity = activity as CollectionHistoryActivity
        }

        override fun onResume() {
            super.onResume()
            parentActivity?.showToolbarBackButton(R.string.title_collection_history)
        }

        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.menu_filter_search, menu)
            this.menu = menu
            super.onCreateOptionsMenu(menu, inflater)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            when {
                id == R.id.action_filter -> {
                    showDateRangeSelection()
                }
                item.itemId == android.R.id.home -> parentActivity?.finish()
                item.groupId == 1 -> {
                    selectedTaxtypefilter = item.title.toString()
                    bindData()
                }
            }
            return super.onOptionsItemSelected(item)
        }

        private fun getFromAndToDate(): Map<String, String> {
            val dateFormat = SimpleDateFormat(displayDateFormat, Locale.getDefault())
            val fromCal = Calendar.getInstance()
            val toCal = Calendar.getInstance()
            fromCal.add(Calendar.DATE, -7)
            val map = HashMap<String, String>()
            map["from_date"] = dateFormat.format(fromCal.time)
            map["to_date"] = dateFormat.format(toCal.time)
            return map
        }


        private fun showDateRangeSelection() {
            val layoutInflater = LayoutInflater.from(parentActivity)
            val mbinding = DataBindingUtil.inflate<FilterDateRangeBinding>(layoutInflater, R.layout.filter_date_range, date_dilog_linear_layout, false)
            val edtFromDate = mbinding.editTextFromDate
            val txtInputLayoutFromDate = mbinding.txtInpLayFromDate
            val txtInputLayoutToDate = mbinding.txtInpLayToDate
            val edtToDate = mbinding.editTextToDate
            edtToDate.isEnabled = false
            edtToDate.setDisplayDateFormat(displayDateFormat)
            edtToDate.setMaxDate(Calendar.getInstance().timeInMillis)
            edtFromDate.setDisplayDateFormat(displayDateFormat)
            edtFromDate.setMaxDate(Calendar.getInstance().timeInMillis)
            if (fromDate.isNullOrBlank() && fromDate.isNullOrBlank()) {
                val map = getFromAndToDate()
                fromDate = map["from_date"]
                toDate = map["to_date"]
            }
            edtFromDate.setText(fromDate)
            edtToDate.setText(toDate)

            edtFromDate.addTextChangedListener(object : TextWatcher {
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
                }

            })

            edtToDate.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (count > 0) {
                        txtInputLayoutToDate.error = null
                    }
                }
            })

            parentActivity?.showAlertDialog(R.string.title_select_date, R.string.ok, {
                val dialog = (it as Button).tag as AlertDialog
                var isValid = true
                fromDate = edtFromDate.text.toString()
                toDate = edtToDate.text.toString()
                if (TextUtils.isEmpty(fromDate)) {
                    isValid = false
                    txtInputLayoutFromDate.error = getString(R.string.msg_from_date)
                } else if (TextUtils.isEmpty(toDate)) {
                    isValid = false
                    txtInputLayoutToDate.error = getString(R.string.msg_to_date)
                }
                if (isValid) {
                    bindData()
                    dialog.dismiss()
                }
            }, 0, null, R.string.cancel, {
                val dialog = (it as Button).tag as AlertDialog
                dialog.dismiss()
            }, mbinding.root)
        }

    }

}