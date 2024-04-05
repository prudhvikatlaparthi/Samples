package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.RowTaxDetailBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatWithPrecision
import com.sgs.citytax.util.getString


class TaxDetailAdapter(val listener: DetailsListener, private val mCode: Constant.QuickMenu) : RecyclerView.Adapter<TaxDetailAdapter.ViewHolder>() {

    private var taxDetails: ArrayList<SAL_TaxDetails> = ArrayList()
    private val taxes: ArrayList<SAL_TaxDetails> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.row_tax_detail, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taxDetails[position], listener, mCode)
    }

    override fun getItemCount(): Int {
        return taxDetails.size
    }

    class ViewHolder(var binding: RowTaxDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taxDetails: SAL_TaxDetails, listener: DetailsListener, code: Constant.QuickMenu) {
            if (taxDetails.taxTypeName.isNullOrEmpty()) {
                binding.txtTaxName.text = if (taxDetails.occupancy == null) taxDetails.product
                        ?: "" else taxDetails.occupancy
            } else {
                binding.txtTaxName.text = taxDetails.taxTypeName.toString()
            }
            binding.txtTaxProductCode.text = taxDetails.productCode.toString()
            binding.txtPreviousDueValue.text = formatWithPrecision(taxDetails.previousDue.toDouble())
            binding.txtCurrentChargesValue.text = formatWithPrecision(taxDetails.currentDue.toDouble())
            binding.txtBalanceValue.text = formatWithPrecision(taxDetails.TotalDue.toDouble())
            binding.txtPenaltyDue.text = formatWithPrecision(taxDetails.penaltyDue.toDouble())

            // region Visibility
            binding.btnCollect.visibility = GONE
            binding.btnPaymentHistory.visibility = GONE
            binding.btnTaxNotice.visibility = GONE

            if (code == Constant.QuickMenu.QUICK_MENU_LICENSE_RENEWAL) {
                if (taxDetails.currentTaxInvoiceNo == 0) {
                    binding.btnTaxNotice.visibility = VISIBLE
                    binding.btnCollect.visibility = GONE
                } else {
                    binding.btnTaxNotice.visibility = GONE
                    if (taxDetails.TotalDue.toDouble() > 0)
                        binding.btnCollect.visibility = VISIBLE
                    else
                        binding.btnCollect.visibility = GONE
                }

            } else {
                if (code == Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE) {
                    if (taxDetails.showGenerateInvoice)
                        binding.btnTaxNotice.visibility = VISIBLE
                    else
                        binding.btnTaxNotice.visibility = GONE
                }

                if (code == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_NOTICE) {
                    if (taxDetails.currentTaxInvoiceNo == 0)
                        binding.btnTaxNotice.visibility = VISIBLE
                    else
                        binding.btnTaxNotice.visibility = GONE
                }

                if (code == Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_COLLECTION || code == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_COLLECTION) {
                    binding.btnPaymentHistory.visibility = VISIBLE
                    if (taxDetails.TotalDue.toDouble() > 0)
                        binding.btnCollect.visibility = VISIBLE
                    else
                        binding.btnCollect.visibility = GONE
                }
            }
            // endregion

            // region Events
            binding.btnTaxNotice.setOnClickListener {
                listener.genTaxInvoice(taxDetails)
            }
            binding.btnCollect.setOnClickListener {
                // if (taxDetails.currentTaxInvoiceNo > 0)
                if (taxDetails.isPaymentSettledByCheque && taxDetails.chequeStatus == Constant.CheckStatus.NEW.value) {
                    listener.showAlertDialog(getString(R.string.cheque_payment_inprogress))
                } else {
                    listener.collectPayment(taxDetails)
                }
            }
            binding.btnPaymentHistory.setOnClickListener {
                listener.paymentHistory(taxDetails)
            }
            // endregion
        }
    }

    fun updateTaxDetails(details: List<SAL_TaxDetails>?) {
        if (details == null) taxDetails.clear() else taxDetails = details as ArrayList<SAL_TaxDetails>
        notifyDataSetChanged()
    }

    /**
     * Get Tax Notice only those to be generated
     * */
    fun getTaxDetails(): List<SAL_TaxDetails> {
        taxes.clear()
        for (taxDetail in taxDetails) {
            if (taxDetail.currentTaxInvoiceNo <= 0)
                taxes.add(taxDetail)
        }
        return taxes
    }

    fun getNoOfNoticesToBeGenerate(): Int {
        getTaxDetails()
        return taxes.size
    }

    interface DetailsListener {
        fun collectPayment(SALTaxDetails: SAL_TaxDetails)
        fun paymentHistory(SALTaxDetails: SAL_TaxDetails)
        fun genTaxInvoice(SALTaxDetails: SAL_TaxDetails)
        fun showAlertDialog(msg: String)
    }

}