package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GenericGetDetailsBySycotax
import com.sgs.citytax.api.response.PropertyDetailsBySycoTax
import com.sgs.citytax.databinding.FragmentPropertyInfoMapBinding
import com.sgs.citytax.model.PropertyDetailLocation
import com.sgs.citytax.ui.LandTaxActivity
import com.sgs.citytax.ui.PropertyLandTaxDetailsActivity
import com.sgs.citytax.ui.PropertyTaxActivity
import com.sgs.citytax.ui.PropertyTaxSummaryActivity
import com.sgs.citytax.util.Constant

class PropertyLocationInfoDialogFragment : DialogFragment(), View.OnClickListener {
    private lateinit var mBinding: FragmentPropertyInfoMapBinding
    private var mListener: Listener? = null
    private var propertyLocation: PropertyDetailLocation? = null
    private var fromScreenCode: Any? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(propertyLocation: PropertyDetailLocation?, fromScreen: Any?) = PropertyLocationInfoDialogFragment().apply {
            this.propertyLocation = propertyLocation
            this.fromScreenCode = fromScreen
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_info_map, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AlertDialogTheme)
    }

    fun initComponents() {
        bindData()
        setViewsVisibility()
        setListeners()
    }

    private fun setViewsVisibility() {
        if (propertyLocation?.taxRuleBookCode == Constant.TaxRuleBook.LAND_PROP.Code || propertyLocation?.taxRuleBookCode == Constant.TaxRuleBook.LAND_CONTRIBUTION.Code) {
            mBinding.btnPropertySummary.setText(getString(R.string.land_summary))
            mBinding.btnPropertyRecord.setText(getString(R.string.title_land_record))
            mBinding.type.setText(getString(R.string.land_type))
        }
        if (propertyLocation?.taxRuleBookCode == Constant.TaxRuleBook.COM_PROP.Code
                || propertyLocation?.taxRuleBookCode == Constant.TaxRuleBook.RES_PROP.Code) {
            mBinding.btnPropertySummary.setText(getString(R.string.title_property_summary))
            mBinding.btnPropertyRecord.setText(getString(R.string.title_property_record))
            mBinding.type.setText(getString(R.string.property_type))
        }
    }

    private fun bindData() {
        mBinding.propertyDetailsVM = propertyLocation
        mBinding.executePendingBindings()
    }

    private fun setListeners() {
        mBinding.btnPropertySummary.setOnClickListener(this)
        mBinding.btnPropertyRecord.setOnClickListener(this)
        mBinding.btnGeneratePropertyTaxNotice.setOnClickListener(this)
        mBinding.btnPropertyCollectTax.setOnClickListener(this)
    }

    interface Listener {
        fun showAlertDialog(message: String)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String?, okListener: DialogInterface.OnClickListener)
        fun onClick()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnPropertySummary -> {
                if (propertyLocation?.taxRuleBookCode == Constant.TaxRuleBook.LAND_PROP.Code || propertyLocation?.taxRuleBookCode == Constant.TaxRuleBook.LAND_CONTRIBUTION.Code) {
                    val intent = Intent(context, PropertyTaxSummaryActivity::class.java)
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, propertyLocation?.taxRuleBookCode)
                    intent.putExtra(Constant.KEY_PRIMARY_KEY, propertyLocation?.propertyID)
                    startActivity(intent)
                } else {
                    val intent = Intent(context, PropertyTaxSummaryActivity::class.java)
                    intent.putExtra(Constant.KEY_TAX_RULE_BOOK_CODE, propertyLocation?.taxRuleBookCode)
                    intent.putExtra(Constant.KEY_PRIMARY_KEY, propertyLocation?.propertyID)
                    startActivity(intent)
                }
                dismiss()
            }
            R.id.btnPropertyRecord -> {

                searchPropertyDetailsBySycoTax(propertyLocation?.PropertySycotaxID)
            }
            R.id.btnGeneratePropertyTaxNotice -> {
                val intent = Intent(context, PropertyLandTaxDetailsActivity::class.java)
                intent.putExtra(Constant.KEY_SYCO_TAX_ID, propertyLocation?.PropertySycotaxID)
                intent.putExtra(Constant.KEY_PRIMARY_KEY, propertyLocation?.propertyID)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE)
                startActivity(intent)
                dismiss()
            }
            R.id.btnPropertyCollectTax -> {
                val intent = Intent(context, PropertyLandTaxDetailsActivity::class.java)
                intent.putExtra(Constant.KEY_SYCO_TAX_ID, propertyLocation?.PropertySycotaxID)
                intent.putExtra(Constant.KEY_PRIMARY_KEY, propertyLocation?.propertyID)
                intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION)
                startActivity(intent)
                dismiss()
            }
        }
    }

    private fun searchPropertyDetailsBySycoTax(sycoTaxID: String?) {
        val genericGetDetailsBySycotax = GenericGetDetailsBySycotax()
        genericGetDetailsBySycotax.sycoTaxId = sycoTaxID
        mListener?.showProgressDialog()
        APICall.searchPropertyDetailsBySycoTax(genericGetDetailsBySycotax, object : ConnectionCallBack<PropertyDetailsBySycoTax> {
            override fun onSuccess(response: PropertyDetailsBySycoTax) {
                mListener?.dismissDialog()
                navigateToPropertyTax(sycoTaxID, response)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
            }
        })
    }

    private fun navigateToPropertyTax(sycoTaxID: String?, propertyDetailsBySycoTax: PropertyDetailsBySycoTax) {
        if (propertyLocation?.taxRuleBookCode == Constant.TaxRuleBook.LAND_PROP.Code) {

            val intent = Intent(context, LandTaxActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_LAND)
            intent.putExtra(Constant.KEY_PROPERTY_DETAILS, propertyDetailsBySycoTax)
            if (propertyDetailsBySycoTax.address.size>0) {
                intent.putParcelableArrayListExtra(Constant.KEY_ADDRESS, propertyDetailsBySycoTax.address)
            }
            intent.putExtra(Constant.KEY_EDIT, false)
            intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
            intent.putExtra(Constant.KEY_GEO_SPATIAL_VIEW, true)
            startActivity(intent)

        } else {
            val intent = Intent(context, PropertyTaxActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY)
            intent.putExtra(Constant.KEY_PROPERTY_DETAILS, propertyDetailsBySycoTax)
            if (propertyDetailsBySycoTax.address.size>0) {
                intent.putParcelableArrayListExtra(Constant.KEY_ADDRESS, propertyDetailsBySycoTax.address)
            }
            intent.putExtra(Constant.KEY_EDIT, false)
            intent.putExtra(Constant.KEY_SYCO_TAX_ID, sycoTaxID)
            intent.putExtra(Constant.KEY_GEO_SPATIAL_VIEW, true)
            startActivity(intent)
        }
        dismiss()
    }

}