package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FilterDateTaskTypeBinding
import com.sgs.citytax.databinding.FragmentTaskBinding
import com.sgs.citytax.model.CRMServiceSubType
import com.sgs.citytax.model.CRMServiceType
import com.sgs.citytax.model.CartTax
import com.sgs.citytax.ui.adapter.CartAdapter
import com.sgs.citytax.ui.adapter.TaskAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.filter_date_range.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentTaskBinding
    internal var tasks: ArrayList<VUCRMServiceRequest> = ArrayList()
    private var listener: Listener? = null

    private var toDate: String? = null
    private var fromDate: String? = null
    private var mTypeName: String? = null
    private var mTypeID: Int? = 0
    private var mServiceTypes: ArrayList<CRMServiceType> = arrayListOf()
    private var mServiceSubTypes: ArrayList<CRMServiceSubType> = arrayListOf()
    var mIncidentMasterTypes: ArrayList<CRMIncidentMaster>? = arrayListOf()
    var mIncidentSubTypes: ArrayList<CRMIncidentSubtype>? = arrayListOf()
    var mCRMComplaintMaster: ArrayList<CRMComplaintMaster>? = arrayListOf()
    var mCRMComplaintSubtype: ArrayList<CRMComplaintSubtype>? = arrayListOf()

    private var typeList: ArrayList<TypeMaster> = arrayListOf()
    lateinit var pagination: Pagination
    private var mAdapter: TaskAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_task, container, false)
        initComponents()
        pagination = Pagination(1, 10, mBinding.rcvTaskMgmt) { pageNumber, PageSize ->
            bindData(null, pageNumber, PageSize)
        }
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        if(mAdapter != null)
            mAdapter!!.clear()
        pagination.setDefaultValues()
    }

    override fun initComponents() {
        var type: TypeMaster = TypeMaster()
        typeList.clear()
        typeList.add(0, TypeMaster(getString(R.string.select), 0))
        typeList.add(1, TypeMaster(getString(R.string.incident), 1))
        typeList.add(2, TypeMaster(getString(R.string.complaint), 2))
        typeList.add(3, TypeMaster(getString(R.string.service), 3))
        initViews()
        bindSpinner()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_filter_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_filter)
            showDateRangeSelection()
        return true
    }

    private fun initViews() {
       /* mBinding.swipeRefreshLayout.setOnRefreshListener {
            bindData(null)
            mBinding.swipeRefreshLayout.isRefreshing = false
        }*/
        mBinding.rcvTaskMgmt.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        //bindData(null)
    }

    private fun bindSpinner() {
        listener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues("CRM_ServiceRequestsTask", object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                listener?.dismissDialog()
                if (response.serviceTypes.isNullOrEmpty())
                else {
                    mServiceTypes.add(CRMServiceType(-1, getString(R.string.select), "", ""))
                    mServiceTypes.addAll(response.serviceTypes)
                }

                mServiceTypes = response.serviceTypes as ArrayList<CRMServiceType>
                mServiceSubTypes = response.serviceSubTypes as ArrayList<CRMServiceSubType>
                mIncidentMasterTypes = response.incidentMgmtType as ArrayList<CRMIncidentMaster>
                mIncidentSubTypes = response.incidentSubType as ArrayList<CRMIncidentSubtype>
                mCRMComplaintMaster = response.complaintMaster as ArrayList<CRMComplaintMaster>
                mCRMComplaintSubtype = response.complaintSubtype as ArrayList<CRMComplaintSubtype>
            }

            override fun onFailure(message: String) {

                listener?.dismissDialog()
                listener?.showAlertDialog(message)
            }
        })

    }


    private fun bindData(type: TypeMaster?, pageNumber: Int, pageSize: Int) {
        mBinding?.rcvTaskMgmt?.adapter = mAdapter
        listener?.showProgressDialog()

        val getTaskRequest = GetTaskORIncidentRequest()
        val searchFilter = AdvanceSearchFilterTask()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageNumber

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
        val listSortColumn: java.util.ArrayList<SortBy> = arrayListOf()
        val listDateFilterColumn: ArrayList<DateFilter> = arrayListOf()
        var filterColumn = FilterColumn()

        filterColumn.columnName = "AssignedToUserID"
        filterColumn.columnValue = MyApplication.getPrefHelper().loggedInUserID
        filterColumn.srchType = "equal"

        listFilterColumn.add(listFilterColumn.size, filterColumn)
        searchFilter.filterColumns = listFilterColumn

        val filterColumnSort = SortBy()
        filterColumnSort.colname = "ComplaintDate"
        filterColumnSort.IsASC=false
        listSortColumn.add(listSortColumn.size, filterColumnSort)

        searchFilter.sortBy = listSortColumn


        if (mTypeName != null && type?.typId != 0) {
            val filterColumn1 = FilterColumn()
            filterColumn1.columnName = "TaskType"
            filterColumn1.columnValue = mTypeName
            filterColumn1.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn1)
            searchFilter.filterColumns = listFilterColumn
        }




        val filterColumn2 = FilterColumn()
        filterColumn2.columnName = "ParentServiceRequestNo"
        filterColumn2.columnValue = ""
        filterColumn2.srchType = "notnull"

        listFilterColumn.add(listFilterColumn.size, filterColumn2)
        searchFilter.filterColumns = listFilterColumn

        if (!fromDate.isNullOrEmpty() && !toDate.isNullOrEmpty()) {
            val filterColumnDateFilterIncident = DateFilter()
            filterColumnDateFilterIncident.dateColumnName = "CreatedDate"

            val fromDateInMillis = fromDate?.let { getDate(it, displayDateFormat, DateTimeTimeZoneMillisecondFormat) }
            val toDateInMillis = toDate?.let { getDate(it, displayDateFormat, DateTimeTimeZoneMillisecondFormat) }

            filterColumnDateFilterIncident.startDate = fromDateInMillis.toString()
            filterColumnDateFilterIncident.endDate = toDateInMillis.toString()

            listDateFilterColumn.add(listDateFilterColumn.size, filterColumnDateFilterIncident)

            searchFilter.dateFilter = listDateFilterColumn
        }else{
            filterColumn = FilterColumn()
            filterColumn.columnName = "StatusCode"
            filterColumn.columnValue = "CRM_ServiceRequests.Closed"
            filterColumn.srchType = "notequal"

            listFilterColumn.add(listFilterColumn.size, filterColumn)
            searchFilter.filterColumns = listFilterColumn
        }

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_ServiceRequests"
        tableDetails.primaryKeyColumnName = "ServiceRequestNo"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "AND"
        tableDetails.selectColoumns = "StatusCode,ComplaintDate,Status,CreatedBy,TaskType,Priority,ServiceRequestNo,TaskSubCategory, ParentServiceRequestNo"

        searchFilter.tableDetails = tableDetails

        getTaskRequest.advanceSearchFilter = searchFilter

        APICall.getTaskORIncidentDetails(getTaskRequest, object : ConnectionCallBack<GetTaskList> {
            override fun onFailure(message: String) {
                tasks = ArrayList()
                mBinding.rcvTaskMgmt.adapter = null
                listener?.dismissDialog()
            }

            override fun onSuccess(response: GetTaskList) {
                listener?.dismissDialog()
                pagination.totalRecords = response.totalSearchedRecords
                setData(response)

            }
        })
    }

    private fun setData(response: GetTaskList) {
        pagination.setIsScrolled(false)
        if (response.results?.serviceRequests != null) {
            pagination.stopPagination(response.results?.serviceRequests!!.size)
        } else {
            pagination.stopPagination(0)
        }

        if(mAdapter == null) {
            mAdapter = TaskAdapter(this)
            mBinding?.rcvTaskMgmt?.adapter = mAdapter
        }

        val responseList = response.results?.serviceRequests
        mAdapter!!.update(responseList as List<VUCRMServiceRequest>)

/*
        response.results?.serviceRequests?.let {
            it.reverse()
            tasks = it
            mBinding.rcvTaskMgmt.adapter = TaskAdapter(tasks) { item: VUCRMServiceRequest -> itemClicked(item) }
        }*/
    }

    override fun onClick(view: View, position: Int, obj: Any) {

        var serviceRequest = obj as VUCRMServiceRequest
        val fragment = TaskEntryFragment.newInstance(serviceRequest)
        listener?.showToolbarBackButton(R.string.title_tasks)
        listener?.replaceFragment(fragment, true)
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("Not yet implemented")
    }


    private fun showDateRangeSelection() {
        // region View
        val layoutInflater = LayoutInflater.from(mBinding.rcvTaskMgmt.context)
        val binding = DataBindingUtil.inflate<FilterDateTaskTypeBinding>(layoutInflater, R.layout.filter_date_task_type, date_dilog_linear_layout, false)
        val edtFromDate = binding.editTextFromDate
        val txtInputLayoutFromDate = binding.txtInpLayFromDate
        val txtInputLayoutToDate = binding.txtInpLayToDate
        val edtToDate = binding.editTextToDate
        edtToDate.setDisplayDateFormat(displayDateFormat)
        edtFromDate.setDisplayDateFormat(displayDateFormat)
        // endregion
        val adapter = ArrayAdapter<TypeMaster>(requireContext(), android.R.layout.simple_spinner_dropdown_item, typeList)
        binding.spnType.adapter = adapter

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

        binding.spnType.setSelection(mTypeID ?: 0)

        binding.spnType.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position==0) {
                 mTypeName=""
                }else{   mTypeName = binding.spnType.selectedItem.toString()}
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }


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
                    val fromDateInMillis = getTimeStampFromDate(serverFormatDate(fromDate) ?: "")
                    val toDateInMillis = getTimeStampFromDate(serverFormatDate(toDate) ?: "")
                    if (isValid && fromDateInMillis > toDateInMillis) {
                        isValid = false
                        txtInputLayoutFromDate.error = getString(R.string.msg_from_date_is_greater_than_to_date)
                    }
                    if (isValid) {
                        txtInputLayoutFromDate.error = null
                        txtInputLayoutToDate.error = null
                        val dialog = (it as Button).tag as AlertDialog
                        val type = binding.spnType.selectedItem as TypeMaster?
                        dialog.dismiss()
                        mTypeID = type?.typId

                        if(mAdapter != null)
                            mAdapter!!.clear()
                        pagination.setDefaultValues()
                    }
                },
                R.string.label_clear_filter,
                View.OnClickListener {
                    fromDate = ""
                    toDate = ""
                    edtFromDate.setText(fromDate)
                    edtToDate.setText(toDate)
                    binding.llSpnType.visibility = View.GONE
                    binding.llSpnSubType.visibility = View.GONE
                    binding.spnType.setSelection(0)

                    if(mAdapter != null)
                        mAdapter!!.clear()
                    pagination.setDefaultValues()

                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                R.string.cancel,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                binding.root
        )
    }


    fun onBackPressed() {
        listener?.popBackStack()
        listener?.finish()
    }

    interface Listener {
        fun popBackStack()
        fun showProgressDialog()
        fun dismissDialog()
        fun showToolbarBackButton(title: Int)
        fun finish()
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
        fun showAlertDialog(message: String)

    }
}