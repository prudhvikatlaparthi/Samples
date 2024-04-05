package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.AssetPrePostCheckListSummaryResponse
import com.sgs.citytax.databinding.FragmentAssetPreCheckListSummaryBinding
import com.sgs.citytax.model.AssetRentalSpecificationsList
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.displayFormatDate
import com.sgs.citytax.util.formatDate
import com.sgs.citytax.util.getString
import kotlinx.android.synthetic.main.fragment_complaint_incident_search_info.*

class AssetPreCheckListSummaryFragment : BaseFragment() {

    private lateinit var mBinding: FragmentAssetPreCheckListSummaryBinding
    private var assetRentID: Int? = 0
    private var mListener: Listener? = null
    private var isMovable:Boolean?=false

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_asset_pre_check_list_summary, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        processIntent()
        bindData()
        setListeners()
    }

    private fun processIntent() {
        arguments?.let {
            assetRentID = it.getInt(Constant.KEY_ASSET_RENT_ID)
            isMovable = it.getBoolean(Constant.KEY_IS_MOVABLE)
        }
    }

    private fun bindData() {
        mListener?.showProgressDialog()
        APICall.getPreCheckListSummaryData(assetRentID,object:ConnectionCallBack<AssetPrePostCheckListSummaryResponse>{
            override fun onSuccess(response: AssetPrePostCheckListSummaryResponse) {
                mListener?.dismissDialog()
                if (response.prePostCheckListData?.isNotEmpty() == true){
                    bindPreCheckListData(response.prePostCheckListData!!)
                }
                response.aswPath?.let {
                    mBinding.cardSignature.visibility = View.VISIBLE
                    Glide.with(requireContext()).load(it).into(mBinding.imgSignature)
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(getString(R.string.msg_no_data))
            }
        })
    }

    private fun bindPreCheckListData(assetRentalSpecifications: ArrayList<AssetRentalSpecificationsList>) {
        for (assetRentalSpecification in assetRentalSpecifications) {
            val linearLayout = LinearLayout(requireContext())
            linearLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            val textView = TextView(requireContext())
            textView.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
            textView.text = assetRentalSpecification.specification
            textView.setPadding(4, 0, 0, 0)
            linearLayout.addView(textView)


            val valueTextView = TextView(requireContext())
            valueTextView.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f)
            valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
            if (assetRentalSpecification.dataType == Constant.DynamicFormDataTypes.Date.value
                    || assetRentalSpecification.dataType == Constant.DynamicFormDataTypes.DateTime.value) {
                if (!assetRentalSpecification.dateValue.isNullOrEmpty() && assetRentalSpecification.dateValue!!.contains(" ")) {
                    val value = assetRentalSpecification.dateValue!!.split(" ")
                    valueTextView.text = displayFormatDate(value[0])
                } else
                    valueTextView.text = displayFormatDate(assetRentalSpecification.dateValue)
            } else if (assetRentalSpecification.dataType == Constant.DynamicFormDataTypes.Spinner.value) {
                valueTextView.text = assetRentalSpecification.specificationValue
            } else {
                valueTextView.text = assetRentalSpecification.value
            }
            valueTextView.setPadding(4, 0, 0, 0)
            linearLayout.addView(valueTextView)

            mBinding.llDynamicPreCheckListData.addView(linearLayout)

        }
    }


    private fun setListeners() {
        mBinding.btnSave.setOnClickListener {
            navigateToReceiptsScreen(assetRentID?:0)
        }
    }

    private fun navigateToReceiptsScreen(assetRentId:Int){
        val intent = Intent(requireContext(),AllTaxNoticesActivity::class.java)
        intent.putExtra(Constant.KEY_ASSET_RENT_ID,assetRentId)
        intent.putExtra(Constant.KEY_IS_MOVABLE,isMovable)
        intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, Constant.TaxRuleBook.ASSET_ASSIGNMENT.Code)
        startActivity(intent)
        activity?.finish()
    }

    interface Listener {
        fun popBackStack()
        fun showProgressDialog()
        fun dismissDialog()
        fun showToast(message: String)
        fun showToolbarBackButton(title: Int)
        fun finish()
        fun showSnackbarMsg(message: Int)
        fun showAlertDialog(message: String)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog(message: Int)
    }
}