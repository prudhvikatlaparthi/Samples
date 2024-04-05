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
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.ListDueNoticeResponse
import com.sgs.citytax.api.response.ListDueNoticeResult
import com.sgs.citytax.databinding.FragmentListDueNoticeMasterBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.SignaturePreviewActivity
import com.sgs.citytax.ui.adapter.ListDueNoticeAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.Pagination


class ListofDueNoticeMasterFragment : BaseFragment(), IClickListener {

    private var mListener: Listener? = null
    private lateinit var mBinding: FragmentListDueNoticeMasterBinding
    private var accId: Int = 0
    lateinit var pagination: Pagination
    private var adapter: ListDueNoticeAdapter? = null
    companion object {
        var primaryKey = 0
    }

    override fun initComponents() {
        arguments?.let {
            primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
        }
        setViews()
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

    override fun onResume() {
        super.onResume()
        if (adapter != null)
            adapter!!.clear()
        pagination.setDefaultValues()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list_due_notice_master,
            container,
            false
        )
        initComponents()
        return mBinding.root
    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )
        mBinding.recyclerView.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        adapter = ListDueNoticeAdapter(this)

        pagination = Pagination(1, 10, mBinding.recyclerView) { pageNumber, pageSize ->
            bindData(pageNumber, pageSize)
        }
    }

    private fun bindData(pageNumber: Int, pageSize: Int) {
        mListener?.showProgressDialog()

        APICall.getListDueNotice(
            primaryKey,
            pageSize,
            pageNumber,
            object : ConnectionCallBack<ListDueNoticeResponse> {
                override fun onSuccess(response: ListDueNoticeResponse) {
                    mListener?.dismissDialog()
                    pagination.totalRecords = response.totalRecords!!
                    setData(response)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                }

            })

    }

    private fun setData(listDueNotice: ListDueNoticeResponse) {
        pagination.setIsScrolled(false)

        if (listDueNotice.results?.size != null) {
            pagination.stopPagination(listDueNotice.results?.size!!)
        } else {
            pagination.stopPagination(0)
        }

        if (adapter == null) {
            adapter = ListDueNoticeAdapter(this)
            mBinding.recyclerView.adapter = adapter
        }

        adapter!!.update(listDueNotice.results?: arrayListOf())
        mBinding.recyclerView.adapter = adapter
    }

    private fun clearAdapter(): ListDueNoticeAdapter {
        var adapter = mBinding.recyclerView.adapter

        if (adapter == null) {
            adapter = ListDueNoticeAdapter(this)
        }

        (adapter as ListDueNoticeAdapter).clear()
        return adapter
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {

                R.id.btnSignature -> {

                    val listDueNoticeResult: ListDueNoticeResult = obj as ListDueNoticeResult

//                    listDueNoticeResult.awsPath?.let {
                        var comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()
                        var mDocumentReference: COMDocumentReference? = COMDocumentReference()

//                        mDocumentReference?.awsfile = it
                        mDocumentReference?.awsfile = listDueNoticeResult.signatureAWSPath
                        mDocumentReference?.documentNo = listDueNoticeResult.signatureID

                        val intent = Intent(context, SignaturePreviewActivity::class.java)
                        comDocumentReferences.remove(mDocumentReference)
                        if (mDocumentReference != null) {
                            comDocumentReferences.add(0, mDocumentReference)
                        }
                        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_SIGNATURE )
                        intent.putExtra(Constant.KEY_DOCUMENT, comDocumentReferences)
                        startActivity(intent)
//                    }
                }
                R.id.btnImages -> {

                    val listDueNoticeResult: ListDueNoticeResult = obj as ListDueNoticeResult
                    val fragment = DueNoticeImagesFragment()
                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU,Constant.QuickMenu.QUICK_MENU_HANDOVER_DUE_IMAGES)
                    bundle.putString(Constant.KEY_PRIMARY_KEY, listDueNoticeResult.dueNoticeId.toString() ?: "")
                    fragment.arguments = bundle
                    //endregion
                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                    mListener?.showToolbarBackButton(R.string.handover_due_notice_images)
                    mListener?.addFragment(fragment, true)
                }
                else -> {
                }

            }
        }
    }
    override fun onLongClick(view: View, position: Int, obj: Any) {
    }
}


/*
class ListofDueNoticeMasterFragment : BaseFragment() {

    private var mListener: Listener? = null
    private lateinit var mBinding: FragmentListDueNoticeMasterBinding
    private var accId: Int = 0
    lateinit var pagination:Pagination
    private var adapter:ListDueNoticeAdapter?=null

    override fun initComponents() {



        setViews()

    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity,LinearLayoutManager.VERTICAL))
        adapter= ListDueNoticeAdapter()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list_due_notice_master,
            container,
            false
        )
        initComponents()
        pagination= Pagination(1,10,mBinding.recyclerView){ pageNumber, pageSize ->
            bindData(pageNumber,pageSize)
        }
        return mBinding.root
    }

    private fun bindData(pageNumber: Int, pageSize: Int) {

        mBinding.recyclerView.adapter=adapter
        mListener?.showProgressDialog()

        APICall.getListDueNotice(accId,pageSize,pageNumber,object:ConnectionCallBack<ListDueNoticeResponse>{
            override fun onSuccess(response: ListDueNoticeResponse) {
                mListener?.dismissDialog()
                pagination.totalRecords=response.totalRecords!!
                setData(response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }

        })

    }

    fun setData(listDueNotice:ListDueNoticeResponse){
        pagination.setIsScrolled(false)

        if(listDueNotice.results?.size!=null){
            pagination.stopPagination(listDueNotice.results?.size!!)
        }else{
            pagination.stopPagination(0)
        }

        if(adapter==null){
            adapter= ListDueNoticeAdapter()
            mBinding.recyclerView.adapter=adapter
        }

        adapter!!.update(listDueNotice.results!!)

    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        var screenMode: Constant.ScreenMode

    }
    override fun onResume() {
        super.onResume()
        if(adapter != null)
            adapter!!.clear()
        pagination.setDefaultValues()
    }
}*/
