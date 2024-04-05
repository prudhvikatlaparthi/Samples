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
import com.sgs.citytax.api.response.AssetFitnessesData
import com.sgs.citytax.api.response.AssetFitnessesResponse
import com.sgs.citytax.databinding.FragmentMasterListBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.adapter.AssetFitnessAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class AssetFitnessMasterFragment : BaseFragment(), IClickListener, View.OnClickListener {

    private lateinit var mBinding: FragmentMasterListBinding
    private var mListener: Listener? = null
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER
    private var assetFitnessesData: ArrayList<AssetFitnessesData> = arrayListOf()

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
        mBinding.recyclerView.adapter = AssetFitnessAdapter(this, fromScreen)
    }

    private fun bindData() {
        if (primaryKey != 0) {
            mListener?.showProgressDialog()
            APICall.getAssetCertificateDetailsFitness(primaryKey, "VU_AST_AssetFitnesses", object : ConnectionCallBack<AssetFitnessesResponse> {
                override fun onSuccess(response: AssetFitnessesResponse) {
                    mListener?.dismissDialog()
                    assetFitnessesData = response.assetFitnessesData
                    val adapter = (mBinding.recyclerView.adapter as AssetFitnessAdapter)
                    adapter.clear()
                    adapter.update(assetFitnessesData)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    (mBinding.recyclerView.adapter as AssetFitnessAdapter).clear()
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
            val fragment = AssetFitnessEntryFragment()

            //region SetArguments
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            //endregion

            fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_FITNESS_ENTRY)

            mListener?.showToolbarBackButton(R.string.add_fitness_details)
            mListener?.addFragment(fragment, true)
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.txtEdit -> {

                    val fragment = AssetFitnessEntryFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                    bundle.putParcelable(Constant.KEY_DOCUMENT, obj as AssetFitnessesData?)
                    fragment.arguments = bundle
                    //endregion

                    fragment.setTargetFragment(this, Constant.REQUEST_CODE_ASSET_FITNESS_ENTRY)

                    mListener?.showToolbarBackButton(R.string.add_fitness_details)
                    mListener?.addFragment(fragment, true)
                }

                R.id.img_document -> {
                    val assetFitness = obj as AssetFitnessesData
                    val comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()

                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    assetFitnessesData.remove(assetFitness)
                    assetFitnessesData.add(0, assetFitness)

                    for (assetFitnessDocument in assetFitnessesData) {
                        val documentValues = COMDocumentReference()
                        documentValues.awsfile = assetFitnessDocument.awsPath
                        documentValues.documentNo = assetFitnessDocument.fitnessID.toString()
                        comDocumentReferences.add(documentValues)
                    }

                    intent.putExtra(Constant.KEY_DOCUMENT, comDocumentReferences)
                    startActivity(intent)
                }

                R.id.txtDelete -> {
                    deleteFitnessDocument(obj as AssetFitnessesData)
                }
                else -> {

                }
            }
        }
    }

    private fun deleteFitnessDocument(assetFitness: AssetFitnessesData?) {
        if (null != assetFitness?.fitnessID) {
            mListener?.showProgressDialog()
            APICall.deleteFitnessDocument(assetFitness.fitnessID!!, object : ConnectionCallBack<Boolean> {
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
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_ASSET_FITNESS_ENTRY) {
            (mBinding.recyclerView.adapter as AssetFitnessAdapter).clear()
            mListener?.showToolbarBackButton(R.string.add_fitness_details)
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