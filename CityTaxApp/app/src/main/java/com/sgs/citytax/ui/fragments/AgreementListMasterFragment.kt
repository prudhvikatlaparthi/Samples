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
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.AgreementDetailsList
import com.sgs.citytax.api.response.AgreementResultsList
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.ui.adapter.AgreementListAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.Pagination
import java.util.*

class AgreementListMasterFragment : BaseFragment(), IClickListener, View.OnClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var accId: Int = 0
    private var mAdapter: AgreementListAdapter? = null
    lateinit var pagination: Pagination
    private var agreementResultsList: ArrayList<AgreementResultsList> = arrayListOf()
    private var lockSwipe: Boolean = true
    private var setViewForGeoSpatial: Boolean? = false  //todo New key to Hide views for geo spacial- Busianess Record - 15/3/2022, not used fromScreen, to not to disturb th flow

    companion object {
        var primaryKey = 0
    }

    override fun initComponents() {
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
            setViewForGeoSpatial = arguments?.getBoolean(Constant.KEY_GEO_SPATIAL_VIEW) ?: false
        }
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        pagination = Pagination(1, 10, mBinding.recyclerView) { pageNumber, PageSize ->
            bindData(pageNumber, PageSize)
        }
        return mBinding.root
    }

    private fun setViews() {

        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        if (MyApplication.getPrefHelper().isSupervisor() || MyApplication.getPrefHelper().isInspector() )
        {
            mBinding.fabAdd.visibility = View.VISIBLE
            lockSwipe = false
        }
        else
        {
            mBinding.fabAdd.visibility = View.GONE
            lockSwipe = true
        }

        //todo Hide views for geo spacial- Busianess Record - 15/3/2022
        if (setViewForGeoSpatial == true){
            mBinding.fabAdd.visibility = View.GONE
            mBinding.fabSearch.visibility = View.GONE
        }

        mAdapter = AgreementListAdapter(this, fromScreen, mListener?.screenMode,lockSwipe)

    }

    fun bindData(pageNumber: Int = 1, pageSize: Int = 10) {

        mBinding.recyclerView.adapter = mAdapter
        mListener?.showProgressDialog()

        APICall.getAgreementList4Business(primaryKey, pageNumber, pageSize,object : ConnectionCallBack<AgreementDetailsList> {
            override fun onSuccess(taxDetails: AgreementDetailsList) {
                mListener?.dismissDialog()

                pagination.totalRecords = taxDetails.totalRecordsFound
                setData(taxDetails)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()

            }
        })


    }

    private fun setData(taxDetails: AgreementDetailsList) {
        pagination.setIsScrolled(false)
        if (taxDetails.agreementResults != null) {
            pagination.stopPagination(taxDetails.agreementResults?.size)
        } else {
            pagination.stopPagination(0)
        }

        if(mAdapter == null) {
            mAdapter = AgreementListAdapter(this, fromScreen, mListener?.screenMode, lockSwipe)
            mBinding?.recyclerView?.adapter = mAdapter
        }

        val specificationValueSets = taxDetails.agreementResults?: arrayListOf()
        mAdapter!!.update(specificationValueSets)


    }

    private fun setListeners() {
       // mBinding.fabAdd.setOnClickListener(this)
        mBinding.fabAdd.setOnClickListener(object : OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                val fragment = AgreementEntryFragment()

                //region SetArguments
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putSerializable(Constant.KEY_QUICK_ACCTID, primaryKey)
                fragment.arguments = bundle
                //endregion

                fragment.setTargetFragment(this@AgreementListMasterFragment, Constant.REQUEST_CODE_EXAMPLE)

                mListener?.screenMode=Constant.ScreenMode.ADD
                mListener?.showToolbarBackButton(R.string.agreementText)
                mListener?.addFragment(fragment, true)
            }

        })
    }

    override fun onClick(v: View?) {

    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.imgAgreement -> {
//                    val agreementDetailsList = obj as AgreementResultsList
//                    val intent = Intent(context, AgreementPreviewActivity::class.java)
//                    agreementResultsList.remove(agreementDetailsList)
//                    agreementResultsList.add(0, agreementDetailsList)
//                    intent.putExtra(Constant.KEY_AGREEMENT_DOCUMENT, agreementResultsList)
//                    startActivity(intent)
                }
                R.id.txtEdit -> {
                    val agreementDetailsList = obj as AgreementResultsList
                    if(agreementDetailsList.allowEdit == "Y" && setViewForGeoSpatial == false){
                        mListener?.screenMode = Constant.ScreenMode.EDIT
                    }else{
                        mListener?.screenMode = Constant.ScreenMode.VIEW
                    }
                    val fragment = AgreementEntryFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                    bundle.putSerializable(Constant.KEY_QUICK_ACCTID, primaryKey)
                    bundle.putParcelable(Constant.KEY_DOCUMENT, obj as AgreementResultsList?)

                    fragment.arguments = bundle
                    //endregion

                    fragment.setTargetFragment(this@AgreementListMasterFragment, Constant.REQUEST_CODE_EXAMPLE)

                    mListener?.showToolbarBackButton(R.string.agreementText)
                    mListener?.addFragment(fragment, true)
                }


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
        var screenMode: Constant.ScreenMode

    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_EXAMPLE) {
            mAdapter!!.clear()
            bindData()
        }
    }
    override fun onResume() {
        super.onResume()
        if(mAdapter != null)
            mAdapter!!.clear()
        pagination.setDefaultValues()
    }


}