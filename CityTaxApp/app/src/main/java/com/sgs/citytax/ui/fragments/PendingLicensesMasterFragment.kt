package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetPendingLicenses4Agent
import com.sgs.citytax.api.response.GetPendingLicenses4AgentResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.PendingLicenseMasterListBinding
import com.sgs.citytax.model.PendingLicenses4Agent
import com.sgs.citytax.ui.TaxDetailsActivity
import com.sgs.citytax.ui.adapter.PendingLicensesMasterAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.Pagination

class PendingLicensesMasterFragment : BaseFragment(), IClickListener, View.OnClickListener {

    private lateinit var mBinding: PendingLicenseMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE

    private lateinit var mAdapter: PendingLicensesMasterAdapter
    private lateinit var pagination: Pagination

    private val resultList: MutableList<PendingLicenses4Agent> = mutableListOf()

    companion object {
        var primaryKey = 0
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
        }
        //endregion
        setViews()
        setListeners()
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

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.pending_license_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mAdapter = PendingLicensesMasterAdapter(this)
        mBinding.recyclerView.adapter = mAdapter
        pagination = Pagination(1, 10, mBinding.recyclerView) { pageNumber, PageSize ->
            bindData(pageIndex = pageNumber, pageSize = PageSize)
        }
    }

    private fun bindData(pageIndex: Int = 1, pageSize: Int = 10) {
        if (pageIndex == 1) {
            mBinding.recyclerView.scrollToPosition(0)
            pagination.resetInitialPageNumber()
            resetRecyclerAdapter()
            mListener?.showProgressDialog()
            mBinding.txtNoDataFound.isVisible = false
        } else {
            mBinding.ProgressBar.isVisible = true
        }
        val getPendingLicenses4Agent = GetPendingLicenses4Agent(pageIndex = pageIndex, pageSize = pageSize, agentAccountId = MyApplication.getPrefHelper().loggedInUserID.toInt())
        APICall.getPendingLicenses4Agent(getPendingLicenses4Agent, object : ConnectionCallBack<GetPendingLicenses4AgentResponse> {
            override fun onSuccess(response: GetPendingLicenses4AgentResponse) {
                mListener?.dismissDialog()
                response.totalRecordCounts?.let {
                    pagination.totalRecords = it
                }
                if (response.PendingLicenses4Agent?.size ?: 0 > 0) {
                    pagination.stopPagination(response.PendingLicenses4Agent?.size!!)
                    response.PendingLicenses4Agent?.let { data ->
                        resultList.addAll(data)
                        mAdapter.differ.submitList(resultList)
                        mAdapter.notifyDataSetChanged()
                        pagination.setIsScrolled(false)
                    }
                } else {
                    pagination.stopPagination(0)
                    if (pageIndex == 1) {
                        resetRecyclerAdapter()
                        mBinding.txtNoDataFound.isVisible = true
                    }
                }
                mBinding.ProgressBar.isVisible = false
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if (pageIndex == 1) {
                    mBinding.txtNoDataFound.isVisible = true
                   // mBinding.txtNoDataFound.text = message
                }
                mBinding.ProgressBar.isVisible = false
            }
        })


    }

    private fun resetRecyclerAdapter() {
        resultList.clear()
        mAdapter.differ.submitList(resultList)
        mAdapter.notifyDataSetChanged()
    }

    private fun setListeners() {
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.llContainer -> {
                    val pendingLicenses = obj as PendingLicenses4Agent

                    val intent = Intent(context, TaxDetailsActivity::class.java)
                    intent.putExtra(Constant.KEY_CUSTOMER_ID, pendingLicenses.accountId)
                    intent.putExtra(Constant.KEY_LICENSE_NUMBER, pendingLicenses.licenseNumber)
                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL)
                    startActivity(intent)

                }

//                R.id.btnCollect -> {
//                    val pendingLicenses = obj as PendingLicenses4Agent
//
//                    val intent = Intent(context, TaxDetailsActivity::class.java)
//                    intent.putExtra(Constant.KEY_CUSTOMER_ID, pendingLicenses.accountId)
//                    intent.putExtra(Constant.KEY_LICENSE_NUMBER, pendingLicenses.licenseNumber)
//                    intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_COLLECTION)
//                    startActivity(intent)
//                }

                else -> {

                }
            }
        }
    }


    override fun onLongClick(view: View, position: Int, obj: Any) {


    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    override fun onResume() {
        super.onResume()
        bindData()
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }


}