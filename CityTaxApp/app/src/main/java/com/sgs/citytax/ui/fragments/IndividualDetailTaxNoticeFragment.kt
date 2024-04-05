package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.CartSycoTax
import com.sgs.citytax.databinding.FragmentTaxDetailsBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.ui.adapter.TaxDetailAdapter
import com.sgs.citytax.util.Constant

class IndividualDetailTaxNoticeFragment : BaseFragment(), View.OnClickListener {

    private var taxDetails: ArrayList<SAL_TaxDetails> = arrayListOf()
    private var mCode = Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE
    private var mListener: Listener? = null
    private var sycoTaxID: String? = null
    private lateinit var adapter: TaxDetailAdapter
    private lateinit var binding: FragmentTaxDetailsBinding

    companion object {
        @JvmStatic
        fun newInstance(code: Constant.QuickMenu, mSycoTaxID: String?) = IndividualDetailTaxNoticeFragment().apply {
            mCode = code
            sycoTaxID = mSycoTaxID
        }
    }

    override fun initComponents() {
        initEvents()
        bindData()
        sycoTaxID?.let {
            getIndividualTaxDetails(it)
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
    
    private fun initEvents() {
        binding.btnGenerateAll.visibility = View.GONE
        binding.llTotalDue.visibility = View.GONE
        binding.llTotalPenaltyTax.visibility = View.GONE
        binding.llTotalTax.visibility = View.GONE
        binding.view.visibility = View.GONE
        binding.btnClose.setOnClickListener(this)
        binding.btnGenerateAll.setOnClickListener(this)
    }

    private fun bindData() {
        binding.txtSycoTaxID.text = sycoTaxID
        if (mCode == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION)
            binding.btnClose.visibility = View.GONE
        else
            binding.btnClose.visibility = View.VISIBLE

        adapter = TaxDetailAdapter(object : TaxDetailAdapter.DetailsListener {
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
        }, mCode)
        binding.rvTaxDetails.adapter = adapter
    }

    fun getIndividualTaxDetails(sycoTaxID: String?) {
        mListener?.showProgressDialog(R.string.msg_please_wait)
        val cartSycoTax = CartSycoTax()
        cartSycoTax.sycoTaxID = sycoTaxID
        APICall.getIndividualTaxDetails(cartSycoTax, object : ConnectionCallBack<List<SAL_TaxDetails>> {
            override fun onSuccess(response: List<SAL_TaxDetails>) {
                mListener?.dismissDialog()
                taxDetails = response as ArrayList<SAL_TaxDetails>
                bindTaxDetails(taxDetails)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                bindTaxDetails(null)
                mListener?.showAlertDialog(getString(R.string.no_record), DialogInterface.OnClickListener { _, _ ->
                    activity?.finish()
                })
            }
        })
    }


    private fun navigateToTaxNotice(taxes: List<SAL_TaxDetails>) {
        val fragment = IndividualTaxNoticeFragment.newInstance(taxes,sycoTaxID)
        mListener?.addFragment(fragment, true)
    }

    fun bindTaxDetails(taxDetails: List<SAL_TaxDetails>?) {
        binding.txtName.text = taxDetails?.get(0)?.product
        adapter.updateTaxDetails(taxDetails)
    }

    fun onBackPressed() {
        mListener?.popBackStack()
        mListener?.finish()
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
        fun showAlertDialog(message: String?, listener: DialogInterface.OnClickListener)
    }
}