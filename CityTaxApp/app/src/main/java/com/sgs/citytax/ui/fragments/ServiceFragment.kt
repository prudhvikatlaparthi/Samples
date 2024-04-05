package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetServiceTaxRequests
import com.sgs.citytax.api.payload.NewServiceRequest
import com.sgs.citytax.api.payload.SaveServiceTaxRequest
import com.sgs.citytax.api.response.ServiceRequestResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FilterDateRangeBinding
import com.sgs.citytax.databinding.FragmentServiceBinding
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.adapter.ServiceAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.REQUEST_CODE_ADD_SERVICE
import com.sgs.citytax.util.Constant.REQUEST_CODE_EDIT_SERVICE
import com.sgs.citytax.util.Constant.REQUEST_CODE_PAYMENT_SUCCESS
import kotlinx.android.synthetic.main.filter_date_range.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ServiceFragment : BaseFragment(), IClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private lateinit var mBinding: FragmentServiceBinding
    private var mListener: Listener? = null
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE

    /*  var pageIndex: Int = 1
      val pageSize: Int = 10*/
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    private var toDate: String? = null
    private var fromDate: String? = null
    private var startDateInMillis: String? = ""
    private var endDateInMillis: String? = ""
    private var searchView: SearchView? = null
    private var mServiceRequests: ArrayList<NewServiceRequest> = arrayListOf()

    lateinit var pagination: Pagination
    private var mAdapter: ServiceAdapter? = null
    private var searchText = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_service, container, false)
        initComponents()

        pagination = Pagination(1, 10, mBinding.recyclerView) { pageIndex, PageSize ->
            bindData(pageIndex, PageSize)
        }
        pagination.setDefaultValues()
        return mBinding.root
    }

    override fun initComponents() {
        //region GetArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        //endregion
        setViews()
        //bindData()
        setEvents()
    }

    private fun setViews() {
        setHasOptionsMenu(true)
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        /* mAdapter = ServiceAdapter(this)
         mBinding.recyclerView.adapter = mAdapter*/

        if (mCode == Constant.QuickMenu.QUICK_MENU_SERVICE_REQUEST_MASTER) {
            mBinding.fabAdd.visibility = View.GONE
        } else {
            mBinding.fabAdd.visibility = View.VISIBLE
        }
    }

    private fun showDateRangeSelection() {
        // region View
        val layoutInflater = LayoutInflater.from(mBinding.recyclerView.context)
        val binding = DataBindingUtil.inflate<FilterDateRangeBinding>(layoutInflater, R.layout.filter_date_range, date_dilog_linear_layout, false)
        val edtFromDate = binding.editTextFromDate
        val txtInputLayoutFromDate = binding.txtInpLayFromDate
        val txtInputLayoutToDate = binding.txtInpLayToDate
        val edtToDate = binding.editTextToDate
        edtToDate.setDisplayDateFormat(displayDateFormat)
        edtFromDate.setDisplayDateFormat(displayDateFormat)
        // endregion

        // region Default dates
        val dateFormat = SimpleDateFormat(displayDateFormat, Locale.getDefault())
        if (fromDate.isNullOrEmpty()) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -7)
            fromDate = dateFormat.format(calendar.time)
        }
        val calendar = Calendar.getInstance()
        if (toDate.isNullOrEmpty()) {
            toDate = dateFormat.format(calendar.time)
        }
        edtFromDate.setText(fromDate)
        edtToDate.setText(toDate)
        edtToDate.setMaxDate(calendar.timeInMillis)
        // endregion

        mListener?.showAlertDialog(R.string.title_select_date,
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
                    /*  val fromDateInMillis = serverFormatDateTimeInMilliSecond(serverFormatDate(edtFromDate.text.toString()))
                      val toDateInMillis = serverFormatDateTimeInMilliSecond(serverFormatDate(edtToDate.text.toString()))*/

                    startDateInMillis = fromDate?.let { getDate(it, displayDateFormat, DateTimeTimeZoneMillisecondFormat) }
                    endDateInMillis = toDate?.let { getDate(it, displayDateFormat, DateTimeTimeZoneMillisecondFormat) }


                    if (isValid && formatDate(startDateInMillis) > formatDate(endDateInMillis)) {
                        isValid = false
                        txtInputLayoutFromDate.error = getString(R.string.msg_from_date_is_greater_than_to_date)
                    }
                    if (isValid) {
                        txtInputLayoutFromDate.error = null
                        txtInputLayoutToDate.error = null
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        /**
                         *  calling api with startDate & endDate
                         */
                        clearAndCallAPI()
                        // filterDateRange()
                    }
                },
                R.string.label_clear_filter,
            // adding SingleClickListener
                object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        fromDate = ""
                        toDate = ""
                        /**
                         * clearing the date here to call the api with stDate and endDate
                         * with empty
                         */
                        startDateInMillis = ""
                        endDateInMillis = ""
                        edtFromDate.setText(fromDate)
                        edtToDate.setText(toDate)

                        clearAndCallAPI()                    }
                },
                R.string.cancel,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                binding.root
        )
    }

    private fun clearAndCallAPI() {
/*        pageIndex = 1
        hasMoreData = true
        mServiceRequests = arrayListOf()
        mAdapter.clear()
        bindData(pageIndex, PageSize)*/

        mAdapter?.clear()
        pagination.setDefaultValues()
    }

    private fun bindData(pageIndex: Int, pageSize: Int) {
/*        val serviceRequest = NewServiceRequest()
        serviceRequest.isLoading = true
        mAdapter!!.add(serviceRequest)
        isLoading = true*/

        val getServiceTaxRequests = GetServiceTaxRequests()
        getServiceTaxRequests.pageIndex = pageIndex
        getServiceTaxRequests.pageSize = pageSize
        getServiceTaxRequests.startDate = startDateInMillis.toString()
        getServiceTaxRequests.endDate = endDateInMillis.toString()
        if (searchText.isNotEmpty()) {
            getServiceTaxRequests.filterString = searchText
        }
        APICall.getServiceTaxRequests(getServiceTaxRequests, object : ConnectionCallBack<ServiceRequestResponse> {
            override fun onSuccess(response: ServiceRequestResponse) {
                /* val count: Int = response.size
                 if (count < this@ServiceFragment.pageSize) {
                     hasMoreData = false
                 } else
                     this@ServiceFragment.pageIndex += 1

                 if (this@ServiceFragment.pageIndex == 1 && response.isEmpty())
                     mAdapter.clear()
                 mAdapter.remove(serviceRequest)
                 *//*for(serReq in response){
                    if(serReq.statusCode != null && serReq.statusCode == "CRM_ServiceRequests.New")
                        mServiceRequests.add(serReq)
                }*//*
                mServiceRequests.addAll(response)


                mAdapter.addAll(response)

                if (count == 0)
                    Toast.makeText(context, R.string.msg_no_data, Toast.LENGTH_SHORT).show()
                isLoading = false*/

                isLoading = false
                pagination.totalRecords = response.totalSearchedRecords
                setData(response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                // mAdapter!!.remove(serviceRequest)
                isLoading = false
            }
        })
    }

    private fun setData(response: ServiceRequestResponse) {
        pagination.setIsScrolled(false)
        if (response.results?.serviceRequests != null) {
            pagination.stopPagination(response.results?.serviceRequests!!.size)
        } else {
            pagination.stopPagination(0)
        }

        if (mAdapter == null) {
            mAdapter = ServiceAdapter(this)
            mBinding?.recyclerView?.adapter = mAdapter
        }

        if(response.results==null){
            mListener?.showAlertDialog(getString(R.string.msg_no_data))
        }else{
            val responseList = response.results?.serviceRequests
            if(responseList!=null) {
                mAdapter!!.addAll(responseList as List<NewServiceRequest>)
            }
        }
    }

    private fun setEvents() {
        mBinding.fabAdd.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = ServiceEntryFragment.newInstance()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                fragment.arguments = bundle
                fragment.setTargetFragment(this@ServiceFragment, Constant.REQUEST_CODE_ADD_SERVICE)
                mListener?.addFragment(fragment, true)
            }
        })
        }
        /* mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
             override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                 super.onScrolled(recyclerView, dx, dy)
                 val visibleItemCount: Int = recyclerView.layoutManager!!.childCount
                 val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                 val totalItemCount: Int = linearLayoutManager.itemCount
                 val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                 if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && hasMoreData && !isLoading) {
                     bindData(pageIndex, PageSize)
                 }
             }
         })*/
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun filterDateRange(fromDate: Long, toDate: Long) {
        mAdapter!!.clear()
        mAdapter!!.addAll(mServiceRequests)
        val serviceRequests: ArrayList<NewServiceRequest> = ArrayList()
        for (item in mServiceRequests) {
            val serviceRequestDate = getTimeStampFromDate(item.serviceRequestDate ?: "")

            if (serviceRequestDate in fromDate..toDate)
                serviceRequests.add(item)
        }
        if (serviceRequests.isNotEmpty()) {
            mAdapter!!.clear()
            mAdapter!!.addAll(serviceRequests)
        } else
            mAdapter!!.clear()
    }

    override fun onClick(view: View, position: Int, obj: Any) {

        when (view.id) {
            R.id.ll_root_view -> {
                val newServiceRequest: NewServiceRequest = obj as NewServiceRequest
                if (mCode == Constant.QuickMenu.QUICK_MENU_SERVICE_REQUEST_MASTER) {
                    Log.e("this is ser req", ">>>>>>>>>" + newServiceRequest.serviceRequestNo)
                    if ((newServiceRequest.advanceAmount != null && newServiceRequest.advanceAmount.toString().toBigDecimal() > BigDecimal.ZERO)) {
                        mListener?.showAlertDialog(getString(R.string.confirm_req_with_amount)
                                + " ${formatWithPrecision(newServiceRequest.advanceAmount.toString())}?", DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                            navigateToPaymentScreen(newServiceRequest)
                        }, DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                        })

                    } else {
                        mListener?.showAlertDialog(getString(R.string.confirm_req), DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                            callSaveAPI(newServiceRequest)
                        }, DialogInterface.OnClickListener { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                        })
                    }
                }
            }
            R.id.txtEdit -> {
                val fragment = ServiceEntryFragment.newInstance()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                bundle.putParcelable(Constant.KEY_SERVICE_TAX, obj as NewServiceRequest)
                fragment.arguments = bundle
                fragment.setTargetFragment(this, Constant.REQUEST_CODE_EDIT_SERVICE)
                mListener?.addFragment(fragment, true)
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    private fun callSaveAPI(newServiceRequest: NewServiceRequest) {
        mListener?.showProgressDialog()
        val saveServiceTaxRequest = SaveServiceTaxRequest()
        newServiceRequest.statusCode = "CRM_ServiceRequests.Confirmed"
        saveServiceTaxRequest.serviceRequest = newServiceRequest


        APICall.saveServiceTaxRequest(saveServiceTaxRequest, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                mListener?.dismissDialog()
                if (mAdapter != null)
                    mAdapter!!.clear()
                pagination.setDefaultValues()
                // bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_service_tax, menu)
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView = (menu.findItem(R.id.action_search)?.actionView as SearchView)

        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        }
        searchView?.setOnQueryTextListener(this)
        searchView?.setOnCloseListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun navigateToPaymentScreen(obj: Any) {
        val newServiceRequest: NewServiceRequest = obj as NewServiceRequest
        //region Payment object preparation for navigating to payment screen
        val payment = MyApplication.resetPayment()
        var netReceivable = BigDecimal.ZERO
        newServiceRequest.advanceAmount?.let {
            netReceivable = it
        }
        payment.amountDue = netReceivable
        payment.amountTotal = netReceivable
        payment.minimumPayAmount = netReceivable
        newServiceRequest.accountID?.let {
            payment.customerID = it.toInt()
        }
        payment.paymentType = Constant.PaymentType.SERVICE_REQUEST
        payment.serviceRequestNo = newServiceRequest.serviceRequestNo!!.toInt()
        //endregion

        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
        startActivityForResult(intent, REQUEST_CODE_PAYMENT_SUCCESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && (requestCode == REQUEST_CODE_ADD_SERVICE || requestCode == REQUEST_CODE_EDIT_SERVICE)) {
            clearAndCallAPI()
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.extras?.let {
                val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                if (it.containsKey(Constant.KEY_ADVANCE_RECEIVED_ID))
                    intent.putExtra(Constant.KEY_ADVANCE_RECEIVED_ID, it.getInt(Constant.KEY_ADVANCE_RECEIVED_ID))
                intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.SERVICE_BOOKING_ADVANCE.Code)
                startActivity(intent)
                activity?.finish()
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter)
            showDateRangeSelection()
        return true
    }

    fun onBackPressed() {
        mListener?.finish()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            val re = Regex("[^A-Za-z0-9 ]")
            /**
             * Removing special characters
             */
            searchText = query?.let { re.replace(it, "") }
            clearAndCallAPI()
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        //mAdapter.filter.filter(newText)
        return true
    }

    override fun onClose(): Boolean {
        searchText = ""
        clearAndCallAPI()
        return true
    }

    interface Listener {
        fun popBackStack()
        fun finish()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun dismissDialog()
        fun showProgressDialog(message: Int)
        fun showProgressDialog()
        fun showSnackbarMsg(message: String?)
        fun showSnackbarMsg(message: Int)
        fun showAlertDialog(message: String)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener, view: View)
    }

}