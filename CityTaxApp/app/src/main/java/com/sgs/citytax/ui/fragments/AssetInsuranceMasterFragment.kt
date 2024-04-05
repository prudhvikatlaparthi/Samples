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
import com.sgs.citytax.api.response.AssetInsuranceData
import com.sgs.citytax.api.response.AssetInsuranceResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.adapter.AssetInsuranceAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class AssetInsuranceMasterFragment : BaseFragment(), IClickListener, View.OnClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var assetInsuranceData: ArrayList<AssetInsuranceData> = arrayListOf()

    companion object {
        var primaryKey = 0
    }

    override fun initComponents() {
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            primaryKey = arguments?.getInt(Constant.KEY_PRIMARY_KEY) ?: 0
        }
        setViews()
        bindData()
        setListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_list, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    private fun setViews() {
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        mBinding.recyclerView.adapter = AssetInsuranceAdapter(this, fromScreen)
    }

    private fun bindData() {
        if (primaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.getAssetCertificateDetailsInsurance(primaryKey, "VU_AST_AssetInsurances", object : ConnectionCallBack<AssetInsuranceResponse> {
                override fun onSuccess(response: AssetInsuranceResponse) {
                    mListener?.dismissDialog()
                    assetInsuranceData = response.assetInsurances
                    val adapter = (mBinding.recyclerView.adapter as AssetInsuranceAdapter)
                    adapter.clear()
                    adapter.update(assetInsuranceData)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    (mBinding.recyclerView.adapter as AssetInsuranceAdapter).clear()
                    if (message.isNotEmpty())
                        mListener?.showAlertDialog(message)
                }
            })
        }
    }

    private fun setListeners() {
        mBinding.fabAdd.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (R.id.fabAdd == v?.id) {
            val fragment = AssetInsuranceEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            //endregion

            fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_INSURANCE_ENTRY)

            mListener?.showToolbarBackButton(R.string.add_insurance_details)
            mListener?.addFragment(fragment, true)
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {

                    val fragment = AssetInsuranceEntryFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                    bundle.putParcelable(Constant.KEY_DOCUMENT, obj as AssetInsuranceData?)
                    fragment.arguments = bundle
                    //endregion

                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_INSURANCE_ENTRY)

                    mListener?.showToolbarBackButton(R.string.add_insurance_details)
                    mListener?.addFragment(fragment, true)
                }

                R.id.img_document -> {
                    val assetInsurance = obj as AssetInsuranceData
                    val comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()

                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    assetInsuranceData.remove(assetInsurance)
                    assetInsuranceData.add(0, assetInsurance)

                    for (assetInsuranceDocument in assetInsuranceData) {
                        val documentValues = COMDocumentReference()
                        documentValues.awsfile = assetInsuranceDocument.awsFile
                        documentValues.documentNo = assetInsuranceDocument.insuranceID.toString()
                        comDocumentReferences.add(documentValues)
                    }

                    intent.putExtra(Constant.KEY_DOCUMENT, comDocumentReferences)
                    startActivity(intent)
                }

                R.id.txtDelete -> {
                    deleteInsuranceDocument(obj as AssetInsuranceData)
                }
                else -> {

                }
            }
        }
    }

    private fun deleteInsuranceDocument(assetInsuranceData: AssetInsuranceData?) {
        if (null != assetInsuranceData?.insuranceID) {
            mListener?.showProgressDialog()
            APICall.deleteInsuranceDocument(assetInsuranceData.insuranceID!!, object : ConnectionCallBack<Boolean> {
                override fun onSuccess(response: Boolean) {
                    mListener?.dismissDialog()
                    bindData()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }


    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_ASSET_INSURANCE_ENTRY) {
            (mBinding.recyclerView.adapter as AssetInsuranceAdapter).clear()
            mListener?.showToolbarBackButton(R.string.add_insurance_details)
            bindData()
        }
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
    }
}