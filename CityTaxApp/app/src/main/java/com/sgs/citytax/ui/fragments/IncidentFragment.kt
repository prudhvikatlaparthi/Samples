package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.GetTaskList
import com.sgs.citytax.api.response.VUCRMServiceRequest
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.databinding.FragmentIncidentBinding
import com.sgs.citytax.ui.adapter.IncidentAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class IncidentFragment : BaseFragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private lateinit var mBinding: FragmentIncidentBinding
    private var accountID: String? = null
    private var listener: Listener? = null
    private var searchView: SearchView? = null
    private var toDate: String? = null
    private var fromDate: String? = null
    private var fromScreen: Constant.QuickMenu? = null
    var pageIndex: Int = 1
    val pageSize: Int = 10
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    private lateinit var mAdapter: IncidentAdapter
    private var mIncidents: ArrayList<VUCRMServiceRequest> = arrayListOf()
    private val blockCharacterSet = "~#^|$%&*!."

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_incident, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        //region GetArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_ACCOUNT_ID))
                accountID = it.getString(Constant.KEY_ACCOUNT_ID)
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?
        }
        if (accountID.isNullOrEmpty())
            accountID = MyApplication.getPrefHelper().accountId.toString()
        //endregion

        initViews()
        bindData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    private fun initViews() {

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_AGENT_SUMMARY_DETAILS)
            mBinding.fabAdd.visibility = View.GONE

        mBinding.rcvIncMgmt.addItemDecoration(DividerItemDecoration(
                this@IncidentFragment.context,
                LinearLayoutManager.VERTICAL))
        mAdapter = IncidentAdapter {
            itemClicked(it)
        }
        mBinding.rcvIncMgmt.adapter = mAdapter
        mBinding.fabAdd.setOnClickListener {
            doAdd()
        }
        mBinding.rcvIncMgmt.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_search_incidents, menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView = (menu.findItem(R.id.action_search)?.actionView as SearchView)


        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        }
        searchView?.setOnQueryTextListener(this)
        searchView?.setOnCloseListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private val filter: InputFilter = InputFilter { source, start, end, dest, dstart, dend ->
        if (source != null && blockCharacterSet.contains("" + source)) {
            ""
        } else null
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_filter)
            showDateRangeSelection()
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            val re = Regex("[^A-Za-z0-9 ]")
            pageIndex = 1
            hasMoreData = true
            mIncidents = arrayListOf()
            mAdapter.clear()
            /**
             * Removing special characters
             */
            var splCharsRemoved = query?.let { re.replace(it, "") }
            bindData(splCharsRemoved)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onClose(): Boolean {
        pageIndex = 1
        hasMoreData = true
        mIncidents = arrayListOf()
        mAdapter.clear()
        bindData()

        return true
    }

    private fun itemClicked(serviceRequest: VUCRMServiceRequest) {
        val fragment = IncidentEntryFragment.newInstance(serviceRequest, fromScreen)
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_INCIDENT)
        listener?.addFragment(fragment, true)
    }
    private fun bindData(searchText: String = "") {
        val incident = VUCRMServiceRequest()
        incident.isLoading = true
        mAdapter.add(incident)
        isLoading = true

        val getTaskRequest = GetTaskORIncidentRequest()
        val searchFilter = AdvanceSearchFilterTask()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        val listSortColumn: ArrayList<SortBy> = arrayListOf()
        val listDateFilterColumn: ArrayList<DateFilter> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "AccountID"
        filterColumn.columnValue = accountID
        filterColumn.srchType = "equal"

        listFilterColumn.add(listFilterColumn.size, filterColumn)

        val filterColumnIncident = FilterColumn()
        filterColumnIncident.columnName = "Incident"
        filterColumnIncident.columnValue = "NULL"
        filterColumnIncident.srchType = "notequal"

        listFilterColumn.add(listFilterColumn.size, filterColumnIncident)

        searchFilter.filterColumns = listFilterColumn

        val filterColumnSort = SortBy()
        filterColumnSort.colname = "ServiceRequestNo"
        listSortColumn.add(listSortColumn.size, filterColumnSort)

        searchFilter.sortBy = listSortColumn


        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
            filterColumn = FilterColumn()
            filterColumn.columnName = "StatusCode"
            filterColumn.columnValue = "CRM_ServiceRequests.Closed"
            filterColumn.srchType = "notequal"

            listFilterColumn.add(listFilterColumn.size, filterColumn)
            searchFilter.filterColumns = listFilterColumn

        } else {
            //DateFilter
            val filterColumnDateFilterIncident = DateFilter()
            filterColumnDateFilterIncident.dateColumnName = "CreatedDate"

            val fromDateInMillis = fromDate?.let { getDate(it, displayDateFormat, DateTimeTimeZoneMillisecondFormat) }
            val toDateInMillis = toDate?.let { getDate(it, displayDateFormat, DateTimeTimeZoneMillisecondFormat) }


            filterColumnDateFilterIncident.startDate = fromDateInMillis.toString()
            filterColumnDateFilterIncident.endDate = toDateInMillis.toString()

            listDateFilterColumn.add(listDateFilterColumn.size, filterColumnDateFilterIncident)

            searchFilter.dateFilter = listDateFilterColumn
        }

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_ServiceRequests"
        tableDetails.primaryKeyColumnName = "ServiceRequestNo"
        tableDetails.selectColoumns = "StatusCode,ServiceRequestNo,ServiceRequestDate," +
                " Status, CreatedBy, Incident, Priority, IncidentSubtype," +
                " IssueDescription,IncidentID, IncidentSubtypeID"
        tableDetails.TableCondition = "AND"
        if(searchText.isNotEmpty())
            tableDetails.initialTableCondition = "Incident LIKE '%$searchText%' OR Status LIKE '%$searchText%' OR IncidentID LIKE '%$searchText%'"

        searchFilter.tableDetails = tableDetails
        getTaskRequest.advanceSearchFilter = searchFilter


        APICall.getTaskORIncidentDetails(getTaskRequest, object : ConnectionCallBack<GetTaskList> {
            override fun onSuccess(response: GetTaskList) {
                if (response.results?.serviceRequests != null && response.results?.serviceRequests!!.isNotEmpty()){
                    mIncidents = response.results?.serviceRequests!!
                    val count = mIncidents.size
                    if (count < pageSize)
                        hasMoreData = false
                    else
                        pageIndex+=1
                    mAdapter.remove(incident)
                    mAdapter.addAll(mIncidents)
                    isLoading = false
                }
                else{
                    mAdapter.remove(incident)
                    isLoading = false
                }
            }

            override fun onFailure(message: String) {
                listener?.showAlertDialog(message)
                mAdapter.remove(incident)
                hasMoreData =  false
                isLoading = false
            }
        })
    }
  /*  private fun bindData() {
        val incident = VUCRMServiceRequest()
        incident.isLoading = true
        mAdapter.add(incident)
        isLoading = true

        val getTaskRequest = GetTaskORIncidentRequest()
        val searchFilter = AdvanceSearchFilterTask()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        val listSortColumn: ArrayList<SortBy> = arrayListOf()
        val listDateFilterColumn: ArrayList<DateFilter> = arrayListOf()
        var filterColumn = FilterColumn()
        filterColumn.columnName = "AccountID"
        filterColumn.columnValue = accountID
        filterColumn.srchType = "equal"

        listFilterColumn.add(listFilterColumn.size, filterColumn)

        val filterColumnIncident = FilterColumn()
        filterColumnIncident.columnName = "Incident"
        filterColumnIncident.columnValue = "NULL"
        filterColumnIncident.srchType = "notequal"

        listFilterColumn.add(listFilterColumn.size, filterColumnIncident)

        searchFilter.filterColumns = listFilterColumn

        val filterColumnSort = SortBy()
        filterColumnSort.colname = "ServiceRequestNo"
        listSortColumn.add(listSortColumn.size, filterColumnSort)

        searchFilter.sortBy = listSortColumn


        if (fromDate.isNullOrEmpty() && toDate.isNullOrEmpty()) {
            filterColumn = FilterColumn()
            filterColumn.columnName = "StatusCode"
            filterColumn.columnValue = "CRM_ServiceRequests.Closed"
            filterColumn.srchType = "notequal"

            listFilterColumn.add(listFilterColumn.size, filterColumn)
            searchFilter.filterColumns = listFilterColumn

        } else {
            //DateFilter
            val filterColumnDateFilterIncident = DateFilter()
            filterColumnDateFilterIncident.dateColumnName = "CreatedDate"

            val fromDateInMillis = fromDate?.let { getDate(it, displayDateFormat, DateTimeTimeZoneMillisecondFormat) }
            val toDateInMillis = toDate?.let { getDate(it, displayDateFormat, DateTimeTimeZoneMillisecondFormat) }

            filterColumnDateFilterIncident.startDate = fromDateInMillis.toString()
            filterColumnDateFilterIncident.endDate = toDateInMillis.toString()

            listDateFilterColumn.add(listDateFilterColumn.size, filterColumnDateFilterIncident)

            searchFilter.dateFilter = listDateFilterColumn
        }

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_ServiceRequests"
        tableDetails.primaryKeyColumnName = "ServiceRequestNo"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "AND"

        searchFilter.tableDetails = tableDetails

        getTaskRequest.advanceSearchFilter = searchFilter


        APICall.getTaskORIncidentDetails(getTaskRequest, object : ConnectionCallBack<GetTaskList> {
            override fun onSuccess(response: GetTaskList) {
                var result: ArrayList<VUCRMServiceRequest> = arrayListOf()
                response.results?.serviceRequests?.let {
                    result = it
                }
                val count: Int = result.size
                if (count < pageSize) {
                    hasMoreData = false
                } else
                    pageIndex += 1

                if (pageIndex == 1 && result.isEmpty())
                    mAdapter.clear()
                mAdapter.remove(incident)
                for (serReq in result) {
                    mIncidents.add(serReq)
                }
                mAdapter.addAll(mIncidents)
                //mServiceRequests.addAll(response)

                if (count == 0)
                    Toast.makeText(context, R.string.msg_no_data, Toast.LENGTH_SHORT).show()
                isLoading = false
            }

            override fun onFailure(message: String) {
                listener?.showAlertDialog(message)
                mAdapter.remove(incident)
                hasMoreData =  false
                isLoading = false
            }
        })
    }*/

    private fun filterDateRange(fromDate: Long, toDate: Long) {
        mAdapter.clear()
        pageIndex=1
        bindData()
       /* mAdapter.addAll(mIncidents)
        val incidents: ArrayList<VUCRMServiceRequest> = ArrayList()
        for (item in mIncidents) {
            val serviceRequestDate = getTimeStampFromDate(item.serviceRequestDate ?: "")

            if (serviceRequestDate in fromDate..toDate)
                incidents.add(item)
        }
        if (incidents.isNotEmpty()) {
            mAdapter.clear()
            mAdapter.addAll(incidents)
        } else
            mAdapter.clear()*/
    }

    private fun showDateRangeSelection() {
        // region View
        val layoutInflater = LayoutInflater.from(mBinding.rcvIncMgmt.context)
        val binding = DataBindingUtil.inflate<FilterDateRangeBinding>(layoutInflater, R.layout.filter_date_range, date_dilog_linear_layout, false)
        val edtFromDate = binding.editTextFromDate
        val txtInputLayoutFromDate = binding.txtInpLayFromDate
        val txtInputLayoutToDate = binding.txtInpLayToDate
        val edtToDate = binding.editTextToDate
        edtToDate.setDisplayDateFormat(displayDateFormat)
        edtToDate.setMaxDate(Calendar.getInstance().timeInMillis) // setting max date to FromDate
        edtFromDate.setDisplayDateFormat(displayDateFormat)
        edtFromDate.setMaxDate(Calendar.getInstance().timeInMillis) // setting max date to ToDate

        // endregion

        // region Default dates
        val dateFormat = SimpleDateFormat(displayDateFormat, Locale.getDefault())
        if (fromDate.isNullOrEmpty()) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -7)
            fromDate = dateFormat.format(calendar.time)
        }
        if (toDate.isNullOrEmpty()) {
            val calendar = Calendar.getInstance()
            toDate = dateFormat.format(calendar.time)
        }
        edtFromDate.setText(fromDate)
        edtToDate.setText(toDate)
        // endregion

        listener?.showAlertDialog(R.string.title_select_date,
                R.string.ok,
                View.OnClickListener {
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
                    val fromDateInMillis = getTimeStampFromDate(serverFormatDate(edtFromDate.text.toString()))
                    val toDateInMillis = getTimeStampFromDate(serverFormatDate(edtToDate.text.toString()))
                    if (isValid && fromDateInMillis > toDateInMillis) {
                        isValid = false
                        txtInputLayoutFromDate.error = getString(R.string.msg_from_date_is_greater_than_to_date)
                    }
                    if (isValid) {
                        txtInputLayoutFromDate.error = null
                        txtInputLayoutToDate.error = null
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        filterDateRange(fromDateInMillis, toDateInMillis)
                    }
                },
                R.string.label_clear_filter,
            //added singleClickListener
               object : OnSingleClickListener(){
                   override fun onSingleClick(v: View?) {
                       fromDate = ""
                       toDate = ""
                       edtFromDate.setText(fromDate)
                       edtToDate.setText(toDate)
                       filterDateRange(0,0)
                   }
               },
                R.string.cancel,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                binding.root
        )
    }

    private fun doAdd() {
        val fragment = IncidentEntryFragment.newInstance(null, fromScreen)
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_INCIDENT)
        listener?.addFragment(fragment, true)
    }

    fun onBackPressed() {
        listener?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_INCIDENT) {
            pageIndex = 1
            hasMoreData = true
            mIncidents = arrayListOf()
            mAdapter.clear()
            bindData()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    interface Listener {
        fun popBackStack()
        fun finish()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun dismissDialog()
        fun showProgressDialog(message: Int)
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    }

}