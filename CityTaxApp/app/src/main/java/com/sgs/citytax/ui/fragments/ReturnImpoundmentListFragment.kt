package com.sgs.citytax.ui.fragments

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
import com.sgs.citytax.api.payload.GetImpondmentReturn
import com.sgs.citytax.api.response.GetImpondmentReturnResponse
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.api.response.PropertyLandTaxDetailsList
import com.sgs.citytax.databinding.FragmentReturnImpoudmentListBinding
import com.sgs.citytax.model.PropertyTax4Business
import com.sgs.citytax.ui.ImpoundmentReturnHistoryActivity
import com.sgs.citytax.ui.adapter.PropertyTaxMasterAdapter
import com.sgs.citytax.ui.adapter.ReturnImpoundmentListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Pagination

class ReturnImpoundmentListFragment : BaseFragment(), ReturnImpoundmentListAdapter.Listener {
    private lateinit var mBinding: FragmentReturnImpoudmentListBinding
    private var mListener: Listener? = null

    private var adapter: ReturnImpoundmentListAdapter? = null
    private var mImpondmentReturnList: List<ImpondmentReturn> = arrayListOf()
    private var fromScreen: Any? = null
    var pageIndex: Int = 1
    val pageSize: Int = 10
    var hasMoreData: Boolean = true
    var isLoading: Boolean = false
    lateinit var pagination: Pagination

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_return_impoudment_list, container, false)
        initComponents()
        pagination = Pagination(1, 10, mBinding.rcvImpundmentsList) { pageNumber, PageSize ->
            bindData(pageNumber, PageSize)
        }
        pagination.setDefaultValues()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        setViews()
        //initialiseListeners()


    }

    private fun setViews() {
        mBinding.rcvImpundmentsList.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        adapter = ReturnImpoundmentListAdapter(this,"1")
        mBinding.rcvImpundmentsList.adapter = adapter

    }

   /* private fun initialiseListeners() {
        mBinding.rcvImpundmentsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
    }*/

    private fun bindData(pageNumber: Int, pageSize: Int) {
        mListener?.showProgressDialog()
        val getImpondmentReturn = GetImpondmentReturn()
        getImpondmentReturn.columnName = ""
        getImpondmentReturn.columnValue = ""
        getImpondmentReturn.pageIndex = pageNumber
        getImpondmentReturn.pageSize = pageSize

        val mImpondmentReturn = ImpondmentReturn()
        mImpondmentReturn.isLoading = true
        adapter?.add(mImpondmentReturn)
        isLoading = true

        APICall.getImpondmentReturnList(getImpondmentReturn, object : ConnectionCallBack<GetImpondmentReturnResponse> {
            override fun onSuccess(response: GetImpondmentReturnResponse) {
                mListener?.dismissDialog()
                if(response.results!=null){
                    mImpondmentReturnList = response.results!!.getImpondmentReturnList
                }
                if(pageNumber==1){
                    adapter?.let {
                        it.clear()
                    }
                }
                    pagination.totalRecords = response.totalRecords
                    setData(response)
                    isLoading = false

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                adapter?.remove(mImpondmentReturn)
                isLoading = false
            }
        })
    }
    private fun setData(taxDetails: GetImpondmentReturnResponse) {
        pagination.setIsScrolled(false)
        if (taxDetails.results?.getImpondmentReturnList != null) {
            pagination.stopPagination(taxDetails.results?.getImpondmentReturnList!!.size)
        } else {
            pagination.stopPagination(0)
        }


        if(adapter == null) {
            adapter = ReturnImpoundmentListAdapter(this,"1")
            mBinding.rcvImpundmentsList.adapter = adapter
        }


        val specificationValueSets = taxDetails.results?.getImpondmentReturnList
        adapter!!.update(specificationValueSets as List<ImpondmentReturn>)

    }
    interface Listener {
        fun finish()
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener)
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun hideKeyBoard()
        fun showSnackbarMsg(message: String?)
    }

    override fun onItemClick(impondmentReturn: ImpondmentReturn, position: Int)
    {
        val intent = Intent(context, ImpoundmentReturnHistoryActivity::class.java)
        intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        intent.putExtra(Constant.KEY_IMPOUNDMENT_RETURN, impondmentReturn)
        startActivity(intent)
        activity?.finish()

    }

}