package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemTaxNoticePreviewBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.util.formatWithPrecision

class IndividualTaxNoticePreviewAdapter(private val taxNotices: List<SAL_TaxDetails>,private val sycoTaxID : String) : RecyclerView.Adapter<IndividualTaxNoticePreviewAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_tax_notice_preview, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taxNotices[position],sycoTaxID)
    }

    override fun getItemCount(): Int {
        return taxNotices.size
    }

    class ViewHolder(var binding: ItemTaxNoticePreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taxNotice: SAL_TaxDetails,sycoTaxID: String) {
            binding.txtSycoTaxID.text = sycoTaxID
            binding.txtBusinessName.text = taxNotice.taxPayer?.vuCrmAccounts?.accountName ?: ""
            binding.txtPhoneNumber.text = taxNotice.taxPayer?.vuCrmAccounts?.phone ?: ""
            binding.txtEmailID.text = taxNotice.taxPayer?.vuCrmAccounts?.email ?: ""
            binding.txtTaxAmount.text = formatWithPrecision(taxNotice.TotalDue)
            binding.txtEstimatedTax.text = formatWithPrecision(taxNotice.estimatedTax)
            var product = taxNotice.product ?: ""

            binding.txtBusinessNameLabel.text = binding.txtBusinessName.context.resources.getString(R.string.owner_name)
            taxNotice.occupancy?.let {
                product = "$product ($it)"
            }
            binding.txtTaxType.text = product
        }
    }

}