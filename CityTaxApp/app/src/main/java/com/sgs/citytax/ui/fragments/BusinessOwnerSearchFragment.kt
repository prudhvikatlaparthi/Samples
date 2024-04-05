package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.OwnerSearchFilter
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.BusinessOwnerResponse
import com.sgs.citytax.databinding.FragmentBusinessOwnerSearchBinding
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.ui.BusinessOwnerSearchActivity
import com.sgs.citytax.ui.adapter.BusinessOwnersListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Constant.CITIZEN_BUSINESS
import com.sgs.citytax.util.IClickListener

class BusinessOwnerSearchFragment : BaseFragment(), IClickListener, View.OnClickListener {
    private lateinit var mBinding: FragmentBusinessOwnerSearchBinding
    private var mListener: Listener? = null
    var pageIndex: Int = 1
    val pageSize: Int = 100
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    private var mCode: Constant.QuickMenu? = null

    /****
     * this logic is to return both business & citizen ids irrespective of mCode
     */
    private var showCitizenBusiness = ""

    private lateinit var mAdapter: BusinessOwnersListAdapter

    override fun initComponents() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if(it.containsKey(Constant.KEY_CITIZEN_BUSINESS))
                showCitizenBusiness = it.getString(Constant.KEY_CITIZEN_BUSINESS, "")

        }
        setViews()
        setListeners()
    }

    private fun setViews() {
        mBinding.rcvOwnerSearchResult.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mAdapter = BusinessOwnersListAdapter(this)
        mBinding.rcvOwnerSearchResult.adapter = mAdapter
        mCode?.let {
            mAdapter.setScreenCode(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_owner_search, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setListeners() {
        mBinding.btnSearch.setOnClickListener(this)
        mBinding.rcvOwnerSearchResult.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = recyclerView.layoutManager!!.childCount
                val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount: Int = linearLayoutManager.itemCount
                val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && hasMoreData && !isLoading) {
                    val data: String = mBinding.edtownersearch.text.toString()
                    mListener?.hideKeyBoard()
                    if (validateView()) {
                        mAdapter.clear()
                        getBusinessOwnerSearchResult(data)
                    }

                }
            }
        })
        mBinding.mainLayout.setOnClickListener {
            //to handle the background click
        }

    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnSearch) {
            val data: String = mBinding.edtownersearch.text.toString()
            mListener?.hideKeyBoard()
            if (validateView()) {
                mAdapter.clear()
                pageIndex=1
                getBusinessOwnerSearchResult(data)
            }
        }

    }

    private fun getBusinessOwnerSearchResult(data: String) {

        if(mCode== Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS || mCode== Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS){
            //Need to handle Inactive condition
        }

        val searchFilter = OwnerSearchFilter()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex
        searchFilter.query = null


        /* "Advsrchfilter": {
             "FilterColumns": [
             {
                 "colname": "AccountName",
                 "ColumnValue": "sa",
                 "SrchType": "Like"
             },
             {
                 "colname": "BusinessOwnerID",
                 "ColumnValue": "sa",
                 "SrchType": "Equal"
             },
             {
                 "colname": "CitizenID",
                 "ColumnValue": "sa",
                 "SrchType": "Equal"
             },
             {
                 "colname": "PhoneNumbers",
                 "ColumnValue": "sa",
                 "SrchType": "Like"
             },
             {
                 "colname": "Emails",
                 "ColumnValue": "sa",
                 "SrchType": "Like"
             }
             ],*/

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()

        var filterColumn = FilterColumn()
        filterColumn.columnName = "AccountName"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)

//        filterColumn = FilterColumn()
//        filterColumn.columnName = "Status"
//        filterColumn.columnValue = "Active"
//        filterColumn.srchType = "Equal"
//        listFilterColumn.add(listFilterColumn.size, filterColumn)

        if (mCode != Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
                && mCode != Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER
                && mCode != Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE
                && mCode != Constant.QuickMenu.QUICK_MENU_IMPONDMENT) {
            filterColumn = FilterColumn()
            filterColumn.columnName = "BusinessOwnerID"
            filterColumn.columnValue = data
            filterColumn.srchType = "Equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
        }

        filterColumn = FilterColumn()
        filterColumn.columnName = "CitizenID"
        filterColumn.columnValue = data
        filterColumn.srchType = "Equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)


        filterColumn = FilterColumn()
        filterColumn.columnName = "PhoneNumbers"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)


        filterColumn = FilterColumn()
        filterColumn.columnName = "Emails"
        filterColumn.columnValue = data
        filterColumn.srchType = "Like"
        listFilterColumn.add(listFilterColumn.size, filterColumn)


        searchFilter.filterColumns = listFilterColumn

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_CRM_OwnerDetails"
        tableDetails.primaryKeyColumnName = "AccountID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "OR"
        if (mCode == Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
                || mCode == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE || mCode == Constant.QuickMenu.QUICK_MENU_IMPONDMENT
                || mCode == Constant.QuickMenu.QUICK_MENU_PROPERTY_NOMINEE|| mCode==Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
            tableDetails.initialTableCondition = "AccountTypeCode = 'CUS' AND StatusCode = 'CRM_Contacts.Active'"
        else if(mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER){
            tableDetails.initialTableCondition = "AccountTypeCode = 'CUS'"
        }

        /****
         * this logic is to return both business & citizen ids irrespective of mCode
         */
        if(showCitizenBusiness.equals(CITIZEN_BUSINESS, ignoreCase = true)){
            filterColumn = FilterColumn()
            filterColumn.columnName = "BusinessOwnerID"
            filterColumn.columnValue = data
            filterColumn.srchType = "Equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            tableDetails.initialTableCondition = ""
        }


        searchFilter.tableDetails = tableDetails

        val businessOwners = BusinessOwnership()
        businessOwners.isLoading = true
        (mBinding.rcvOwnerSearchResult.adapter as BusinessOwnersListAdapter).add(businessOwners)
        isLoading = true

        APICall.getBusinessOwners(searchFilter, object : ConnectionCallBack<BusinessOwnerResponse> {
            override fun onSuccess(response: BusinessOwnerResponse) {
                val count: Int = response.results.businessOwner.size
                if (count < pageSize) {
                    hasMoreData = false
                } else
                    pageIndex += 1

                if (pageIndex == 1 && response.results.businessOwner.isEmpty())
                    mAdapter.clear()
                mAdapter.remove(businessOwners)
                mAdapter.addAll(response.results.businessOwner)

                if (count == 0)
                    Toast.makeText(context, R.string.msg_no_data, Toast.LENGTH_SHORT).show()
                isLoading = false


                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                mAdapter.remove(businessOwners)
                isLoading = false

            }
        })
    }

    private fun validateView(): Boolean {

        if (mBinding.edtownersearch.text.toString() != null && TextUtils.isEmpty(mBinding.edtownersearch.text.toString().trim())) {
            mListener?.showSnackbarMsg(getString(R.string.error_message))
            return false
        }
        return true
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }


    interface Listener {
        fun finish()
        fun popBackStack()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, negativeButton: Int, negativeListener: View.OnClickListener?)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val businessOwner: BusinessOwnership = obj as BusinessOwnership
        mBinding.edtownersearch.setText("")
        mAdapter.clear()
        val intent = Intent()
        if (activity is BusinessOwnerSearchActivity) {
            val fragment = BusinessOwnerEntryFragment()
            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER)
            bundle.putParcelable(Constant.KEY_BUSINESS_OWNER, businessOwner)
            bundle.putBoolean(Constant.KEY_EDIT, true)
            fragment.arguments = bundle
            //endregion
            fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER)
            mListener?.addFragment(fragment, true)
        } else if (businessOwner.citizenSycoTaxID.isNullOrEmpty() && businessOwner.accountTypeCode.equals("CUS")) {
            mListener?.showAlertDialog(R.string.msg_citizen_sycotax_not_mapped,
                    R.string.yes,
                    View.OnClickListener {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                        val fragment = BusinessOwnerEntryFragment()
                        //region SetArguments
                        val bundle = Bundle()
                        bundle.putSerializable(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_OWNER)
                        bundle.putParcelable(Constant.KEY_BUSINESS_OWNER, businessOwner)
                        bundle.putBoolean(Constant.KEY_EDIT, true)
                        fragment.arguments = bundle
                        //endregion
                        fragment.setTargetFragment(this, Constant.REQUEST_CODE_BUSINESS_OWNER_UPDATE)
                        mListener?.addFragment(fragment, true)
                    }, R.string.no,
                    View.OnClickListener {
                        val dialog = (it as Button).tag as AlertDialog
                        dialog.dismiss()
                    })
        } else {
            intent.putExtra(Constant.KEY_BUSINESS_OWNER, businessOwner)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
            mListener?.popBackStack()
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_BUSINESS_OWNER_UPDATE) {
            data?.let {
                var businessOwner: BusinessOwnership? = null
                if (it.hasExtra(Constant.KEY_BUSINESS_OWNER))
                    businessOwner = it.getParcelableExtra(Constant.KEY_BUSINESS_OWNER) as BusinessOwnership
                val intent = Intent()
                intent.putExtra(Constant.KEY_BUSINESS_OWNER, businessOwner)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                mListener?.popBackStack()
            }
        }
    }
}