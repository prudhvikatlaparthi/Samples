package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetChildAgentSummary
import com.sgs.citytax.api.response.AgentCollectionSummaryResponse
import com.sgs.citytax.api.response.ChildAgentSummary
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.databinding.FragmentAgentCollectionSummaryBinding
import com.sgs.citytax.ui.adapter.ChildAgentSummaryAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.text.SimpleDateFormat
import java.util.*

class AgentCollectionSummaryFragment : BaseFragment() {

    var rootView: View? = null
    lateinit var binding: FragmentAgentCollectionSummaryBinding
    var listener: Listener? = null
    private val childAgentSummaries: MutableList<ChildAgentSummary> = mutableListOf()
    private var fromDate: String? = null
    private var toDate: String? = null
    private val childAgentSummaryAdapter: ChildAgentSummaryAdapter by lazy { ChildAgentSummaryAdapter() }
    private lateinit var pagination: Pagination

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: Exception) {
            throw ClassCastException("$context must implement Listener")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_agent_collection_summary, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun initComponents() {
        binding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        binding.recyclerView.adapter = childAgentSummaryAdapter
        setHasOptionsMenu(true)
        pagination = Pagination(1, 10, binding.recyclerView) { pageNumber, PageSize ->
            bindData(pageNumber, pageSize = PageSize)
        }
        bindData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_filter) {
            showDateRangeSelection()
        } else if (item.itemId == android.R.id.home)
            listener?.finish()
        return super.onOptionsItemSelected(item)
    }

    private fun getFromAndToDate(): Map<String, String> {
        val dateFormat = SimpleDateFormat(DateFormat, Locale.getDefault())
        val fromCal = Calendar.getInstance()
        val toCal = Calendar.getInstance()
        fromCal.add(Calendar.DATE, -1)
        val map = HashMap<String, String>()
        map["from_date"] = dateFormat.format(fromCal.time)
        map["to_date"] = dateFormat.format(toCal.time)
        return map
    }

    private fun showDateRangeSelection() {
        val layoutInflater = LayoutInflater.from(activity)
        val binding = DataBindingUtil.inflate<FilterDateRangeBinding>(layoutInflater, R.layout.filter_date_range, date_dilog_linear_layout, false)
        val edtFromDate = binding.editTextFromDate
        val edtToDate = binding.editTextToDate
        edtToDate.isEnabled = false
        edtToDate.setDisplayDateFormat(displayDateFormat)
        edtFromDate.setDisplayDateFormat(displayDateFormat)
        if (fromDate.isNullOrBlank() && fromDate.isNullOrBlank()) {
            val map = getFromAndToDate()
            fromDate = map["from_date"]
            toDate = map["to_date"]
        }
       /* edtFromDate.setText(fromDate)
        edtToDate.setText(toDate)*/
/*        Log.e("fromDate", ">>>>>>>>>>>$fromDate")
        Log.e("toDate", ">>>>>>>>>>>$toDate")*/

        edtFromDate.setText(fromDate?.let {
            displayFormatDate(formatDates(it)) })
        edtToDate.setText(toDate?.let {
            displayFormatDate(formatDates(it)) })

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
            fromDate = serverFormatDate(edtFromDate.text.toString())
            toDate = serverFormatDate(edtToDate.text.toString())
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

    fun bindData(pageindex: Int = 1, pageSize: Int = 10) {
        if (pageindex == 1) {
            binding.recyclerView.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            listener?.showProgressDialog()
        }
        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
            val cal = Calendar.getInstance(Locale.getDefault())
            cal.add(Calendar.DAY_OF_YEAR, -1)
            fromDate = getDate(cal.time, DateFormat)
            toDate = getDate(Date(), DateFormat)
        }
        val childAgentSummary = GetChildAgentSummary(fromDate = fromDate, toDate = toDate, pageindex = pageindex, pageSize = pageSize)
        APICall.getChildAgentSummary(childAgentSummary, object : ConnectionCallBack<AgentCollectionSummaryResponse> {
            override fun onSuccess(response: AgentCollectionSummaryResponse) {
                if (pageindex == 1) {
                    response.totalSearchedRecords?.let {
                        pagination.totalRecords = it
                    }

                    if(response.totalCollectionAmount != null){
                        binding.tvTotalCollectionAmount.text = formatWithPrecision(response.totalCollectionAmount)
                    }else{
                        binding.tvTotalCollectionAmount.text = formatWithPrecision(0.0)
                    }

                    childAgentSummaries.clear()
                }
                if (response.results?.childAgentSummaryList?.size ?: 0 > 0) {
                    pagination.setIsScrolled(false)
                    response.results?.childAgentSummaryList?.let { list ->
                        childAgentSummaries.addAll(list)
                        childAgentSummaryAdapter.updateAdapter(childAgentSummaries.toList())
                    }
                } else {
                    pagination.stopPagination(0)
                    if (pageindex == 1) {
                        listener?.showAlertDialog(getString(R.string.msg_no_data))
                    }
                }
                listener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                listener?.dismissDialog()
                if (pageindex == 1) {
                    listener?.showAlertDialog(message)
                }
            }
        })
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun finish()
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    }

}