package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
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
import com.sgs.citytax.api.response.ShowTaxListResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.ShowsDetailsTable
import com.sgs.citytax.model.TaskCode
import com.sgs.citytax.ui.adapter.ShowListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class ShowTaxMasterFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu? = null
    private var isMultiple = false
    private var mTaskCode: String? = ""
    private var mNoOfShows: Int = 0
    private var adapter: ShowListAdapter? = null
    private var showList: ArrayList<ShowsDetailsTable> = arrayListOf()
    private var mTaxRuleBookCode: String? = ""

    var pageIndex: Int = 1
    val pageSize: Int = 50
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            arguments?.getParcelable<TaskCode>(Constant.KEY_TASK_CODE).apply {
                this?.IsMultiple?.let {
                    isMultiple = it == 'Y' || it == ' '
                }
                this?.taskCode?.let {
                    mTaskCode = it
                }
            }
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mTaxRuleBookCode = arguments?.getString(Constant.KEY_TAX_RULE_BOOK_CODE)!!
        }
        setViews()
        bindData()
        setListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_SHOWS) {
                adapter?.clear()
                bindData()
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = ShowListAdapter(this, mListener?.screenMode)
        mBinding.recyclerView.adapter = adapter
    }

    private fun bindData() {
        if (null != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId && 0 != ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId) {
            mListener?.showProgressDialog()

            val details = ShowsDetailsTable()
            details.isLoading = true
            adapter?.add(details)
            isLoading = true

            APICall.getShowsDetails(ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId, pageSize, pageIndex, object : ConnectionCallBack<ShowTaxListResponse> {
                override fun onSuccess(response: ShowTaxListResponse) {
                    mListener?.dismissDialog()
                    if (response.taxDetails?.showDetails != null && response.taxDetails?.showDetails!!.isNotEmpty()) {
                        showList = response.taxDetails?.showDetails!!
                        val count: Int = showList.size
                        if (count < pageSize) {
                            hasMoreData = false
                        } else
                            pageIndex += 1
                        adapter?.remove(details)
                        adapter!!.addAll(showList)
                        isLoading = false

                        mNoOfShows = showList.size

                        //Hide Validation on FabAdd click --> new change -- allow always
//                        if (!isMultiple && showList.size > 0)
//                            mBinding.fabAdd.visibility = View.GONE
//                        else
//                            mBinding.fabAdd.visibility = View.VISIBLE
//
//                        if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                            mBinding.fabAdd.visibility = View.GONE
//                        }
                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                        {
                            mBinding.fabAdd.visibility = View.GONE
                        }
                    } else {
                        adapter?.remove(details)
                        isLoading = false
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    adapter?.remove(details)
                    isLoading = false
                    mNoOfShows = showList.size
//                    mBinding.fabAdd.visibility = View.VISIBLE
//
//                    if (mListener?.screenMode == Constant.ScreenMode.VIEW) {
//                        mBinding.fabAdd.visibility = View.GONE
//                    }
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)
                    {
                        mBinding.fabAdd.visibility = View.GONE
                    }
                }
            })
        }

    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(object :OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                showEntryScreen(null)
            }
        })
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

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.txtEdit -> {
                    showEntryScreen(obj as ShowsDetailsTable)
                }
                R.id.txtDelete -> {
                    val show = obj as ShowsDetailsTable
                    deleteShow(show.showID?:0)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    private fun showEntryScreen(showsDetailsTable: ShowsDetailsTable?) {
        val fragment = ShowTaxEntryFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_SHOW, showsDetailsTable)
        bundle.putInt(Constant.KEY_NUMBER_OF_SHOWS, mNoOfShows)
        bundle.putString(Constant.KEY_TASK_CODE, mTaskCode)
        if (showsDetailsTable == null)
        {
            mListener?.screenMode = Constant.ScreenMode.ADD
        }
        else{
            bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
        }
        fragment.arguments = bundle
        fragment.setTargetFragment(this, Constant.REQUEST_CODE_SHOWS)
        mListener?.showToolbarBackButton(R.string.title_shows)
        mListener?.addFragment(fragment, true)
    }

    private fun deleteShow(showID: Int) {
        mListener?.showProgressDialog()
        APICall.deleteShow(showID, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                adapter?.clear()
                bindData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showToolbarBackButton(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        var screenMode: Constant.ScreenMode
    }
}