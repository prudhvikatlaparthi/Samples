package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.databinding.FragmentTaxDetailsBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.ui.adapter.PropertyLandTaxDetailAdapter
import com.sgs.citytax.ui.adapter.PropertyLandTaxDetailAdapter.DetailsListener
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatWithPrecision

class PropertyLandTaxDetailsFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentTaxDetailsBinding
    private var mListener: Listener? = null
    private lateinit var fromScreen: Constant.QuickMenu
    private var mSycoTaxId: String? = null
    private lateinit var adapter: PropertyLandTaxDetailAdapter

    companion object {
        @JvmStatic
        fun newInstance(code: Constant.QuickMenu,sycoTaxID: String?) = PropertyLandTaxDetailsFragment().apply {
            fromScreen = code
            mSycoTaxId = sycoTaxID
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else context as Listener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tax_details, container, false)
        initComponents()
        return binding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun initComponents() {
        initEvents()
        bindData()
        getTaxDetails(mSycoTaxId)
    }

    private fun initEvents() {
        binding.btnClose.setOnClickListener(this)
        binding.btnGenerateAll.setOnClickListener(this)
    }

    private fun bindData() {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_COLLECTION)
            binding.btnClose.visibility = GONE
        else
            binding.btnClose.visibility = VISIBLE
        adapter = PropertyLandTaxDetailAdapter(object : DetailsListener {
            override fun collectPayment(SALTaxDetails: SAL_TaxDetails) {
                mListener?.onCollectPaymentClick(SALTaxDetails)
            }

            override fun paymentHistory(SALTaxDetails: SAL_TaxDetails) {
                mListener?.onPaymentHistoryClick(SALTaxDetails)
            }

            override fun genTaxInvoice(SALTaxDetails: SAL_TaxDetails) {
                navigateToTaxNotice(listOf(SALTaxDetails))
            }

            override fun showAlertDialog(msg: String) {
                mListener?.showAlertDialog(msg)
            }
        }, fromScreen)
        binding.rvTaxDetails.adapter = adapter
    }

    fun getTaxDetails(sycoTaxID: String?) {
        mListener?.showProgressDialog(R.string.msg_tax_details)
        APICall.getPropertyLandTaxDetails(sycoTaxID, object : ConnectionCallBack<List<SAL_TaxDetails>> {
            override fun onSuccess(taxDetails: List<SAL_TaxDetails>) {
                // region Total Tax and Current Due
                var currentDue = 0.0
                var totalTax = 0.0
                var penaltyDue = 0.0
                for (taxDetail in taxDetails) {
                    currentDue = currentDue + taxDetail.TotalDue.toDouble()
                    totalTax = totalTax + (taxDetail.currentDue.toDouble() + taxDetail.previousDue.toDouble())
                    penaltyDue = penaltyDue + taxDetail.penaltyDue.toDouble()
                }
                binding.txtTotalDue.text = formatWithPrecision(currentDue)
                binding.txtTotalTax.text = formatWithPrecision(totalTax)
                binding.txtPenaltyDue.text = formatWithPrecision(penaltyDue)
                // endregion
                for (taxDetail in taxDetails) {
                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE) {
                        binding.txtSycoTaxID.text = mSycoTaxId
                        binding.txtName.text = taxDetail.propertyName
                    }else {
                        binding.txtSycoTaxID.text = mSycoTaxId
                        binding.txtName.text = taxDetail.propertyName
                    }
                }
                bindTaxDetails(taxDetails)
                setVisibility()
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                bindTaxDetails(null)
                binding.txtTotalDue.text = ""
                binding.txtTotalTax.text = ""
                binding.txtTotalDue.text = formatWithPrecision(0.0)
                setVisibility()
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message, DialogInterface.OnClickListener { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    mListener?.finish()
                })

            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnClose -> {
                onBackPressed()
            }
            R.id.btnGenerateAll -> {
                val taxes = adapter.getTaxDetails()
                navigateToTaxNotice(taxes)
            }
        }
    }

    private fun navigateToTaxNotice(taxes: List<SAL_TaxDetails>) {
        val fragment = PropertyLandTaxNoticeFragment.newInstance(taxes,mSycoTaxId, fromScreen)
        mListener?.addFragment(fragment, true)
    }

    private fun setVisibility() {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_NOTICE || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE) {
            binding.btnGenerateAll.visibility = GONE
        }
    }

    fun bindTaxDetails(taxDetails: List<SAL_TaxDetails>?) {
        adapter.updateTaxDetails(taxDetails)
    }

    fun onBackPressed() {
        mListener?.popBackStack()
        mListener?.finish()
    }

    interface Listener {
        fun onPaymentHistoryClick(taxDetails: SAL_TaxDetails)
        fun onCollectPaymentClick(taxDetails: SAL_TaxDetails)
        fun dismissDialog()
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun popBackStack()
        fun finish()
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: String?, listener: DialogInterface.OnClickListener)
    }

}