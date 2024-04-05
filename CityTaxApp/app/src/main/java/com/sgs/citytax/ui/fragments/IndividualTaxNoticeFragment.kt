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
import com.sgs.citytax.databinding.FragmentTaxNoticeBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.ui.adapter.IndividualTaxNoticePreviewAdapter

class IndividualTaxNoticeFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentTaxNoticeBinding
    private var mTaxDetails: List<SAL_TaxDetails> = listOf()
    private lateinit var listener: Listener
    private var sycoTaxID: String? = null

    companion object {
        @JvmStatic
        fun newInstance(taxDetails: List<SAL_TaxDetails>, msycoTaxID : String?) = IndividualTaxNoticeFragment().apply {
            mTaxDetails = taxDetails
            sycoTaxID = msycoTaxID
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tax_notice, container, false)
        initComponents()
        return binding.root
    }

    override fun initComponents() {
        bindData()
        initEvents()
    }

    private fun bindData() {
        binding.btnGenerate.visibility = View.VISIBLE
        binding.btnGenerateAll.visibility = View.GONE
        sycoTaxID?.let {
            binding.recyclerView.adapter = IndividualTaxNoticePreviewAdapter(mTaxDetails, it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnClose -> {
                onBackPressed()
            }
            R.id.btnGenerate -> {
                listener.onGenTaxInvoiceClick(mTaxDetails)
            }
        }
    }

    fun onBackPressed() {
        listener.popBackStack()
    }

    private fun initEvents() {
        binding.btnClose.setOnClickListener(this)
        binding.btnGenerate.setOnClickListener(this)
    }

    interface Listener {
        fun dismissDialog()
        fun showProgressDialog()
        fun showProgressDialog(message: Int)
        fun popBackStack()
        fun finish()
        fun onGenTaxInvoiceClick(taxNotices: List<SAL_TaxDetails>)
        fun showAlertDialog(message: String?)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun replaceFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: String?, listener: DialogInterface.OnClickListener)
    }

}