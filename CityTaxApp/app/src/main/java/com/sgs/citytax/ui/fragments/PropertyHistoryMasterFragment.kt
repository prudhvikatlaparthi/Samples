package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.GenericServiceResponse
import com.sgs.citytax.api.response.VuComPropertyMaster
import com.sgs.citytax.databinding.FragmentPropertyHistoryMasterListBinding
import com.sgs.citytax.ui.adapter.ProprtyHistoryAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class PropertyHistoryMasterFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentPropertyHistoryMasterListBinding
    private var mListener: Listener? = null
    private var propertyId: Int? = 0
    private var fromScreen: Constant.QuickMenu? = null
    private var propertyHistory: ArrayList<VuComPropertyMaster> = arrayListOf()
    private var adapter: ProprtyHistoryAdapter? = null
    var pageIndex: Int = 1
    val pageSize: Int = 10
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implemeent Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_history_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        processIntent()
        initViews()
        initEvents()
        bindData()
        setListeners()
    }

    private fun initViews() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = ProprtyHistoryAdapter(this)
        mBinding.recyclerView.adapter = adapter
    }

    private fun initEvents() {
        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PROPERTY_IMAGE) {
            bindData()
        }
    }

    private fun processIntent() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                propertyId = it.getInt(Constant.KEY_PRIMARY_KEY)

            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun bindData() {
        mListener?.showProgressDialog()

        val property = VuComPropertyMaster()
        property.isLoading = true
        adapter?.add(property)
        isLoading = true


        APICall.getTableOrViewData(getPropertyHistoryList(propertyId.toString()), object : ConnectionCallBack<GenericServiceResponse> {
            override fun onSuccess(response: GenericServiceResponse) {
                mListener?.dismissDialog()
                mBinding.recyclerView.visibility = View.VISIBLE
                val count: Int = response.result?.propertyMaster?.size ?: 0
                if (count < pageSize) {
                    hasMoreData = false
                } else
                    pageIndex += 1
                adapter?.remove(property)
                response.result?.propertyMaster?.let {
                    adapter?.addAll(it)
                }
                isLoading = false
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                adapter?.remove(property)
                isLoading = false
            }
        })
    }
//    }        APICall.getTableOrViewData(getPropertyHistoryList(propertyId.toString()), object : ConnectionCallBack<GenericServiceResponse> {
//            override fun onSuccess(response: GenericServiceResponse) {
//                mListener?.dismissDialog()
//                if (response.result != null && response.result.propertyMaster.size > 0) {
//                    val list = response.result.propertyMaster
//                    mBinding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
//                    mBinding.recyclerView.adapter = ProprtyHistoryAdapter(response.result.propertyMaster)
//                } else {
//                    mListener?.showAlertDialog(getString(R.string.no_records_found))
//                }
//            }
//
//            override fun onFailure(message: String) {
//                mListener?.dismissDialog()
//                mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
//            }
//        })
//    }

    private fun setListeners() {

    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {

            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }


    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)

    }

    private fun getPropertyHistoryList(query: String): AdvanceSearchFilter {
        val searchFilter = AdvanceSearchFilter()
        //region Apply Filters
        val filterList: ArrayList<FilterColumn> = arrayListOf()
        val filterColumn = FilterColumn()
        filterColumn.columnName = "LandPropertyID"
        filterColumn.columnValue = query
        filterColumn.srchType = "equal"
        filterList.add(filterColumn)
        searchFilter.filterColumns = filterList
        //endregion

        //region Apply Table/View details
        val tableDetails = TableDetails()
        tableDetails.primaryKeyColumnName = "PropertyID"
        tableDetails.selectColoumns = "PropertyName,PropertyType,SurveyNo"
        tableDetails.TableCondition = ""
        tableDetails.tableOrViewName = "VU_COM_PropertyMaster"
        tableDetails.sendCount = false
        searchFilter.tableDetails = tableDetails
        //endregion
        searchFilter.pageIndex = pageIndex
        searchFilter.pageSize = pageSize

        return searchFilter
    }


    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun showAlertDialog(message: String)
        fun showSnackbarMsg(message: String)
        fun showProgressDialog()
        fun showAlertDialogFailure(message: String, noRecordsFound: Int, onClickListener: DialogInterface.OnClickListener)
        fun dismissDialog()
        fun popBackStack()
        fun showToast(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
    }
}