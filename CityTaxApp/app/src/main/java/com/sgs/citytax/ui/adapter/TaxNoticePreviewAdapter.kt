package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemTaxNoticePreviewBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatWithPrecision

class TaxNoticePreviewAdapter(private val taxNotices: List<SAL_TaxDetails>) : RecyclerView.Adapter<TaxNoticePreviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_tax_notice_preview, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taxNotices[position])
    }

    override fun getItemCount(): Int {
        return taxNotices.size
    }

    class ViewHolder(var binding: ItemTaxNoticePreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taxNotice: SAL_TaxDetails) {
            binding.txtSycoTaxID.text = taxNotice.taxPayer?.sycoTaxID ?: ""
            binding.txtBusinessName.text = taxNotice.taxPayer?.customer ?: ""
            binding.txtPhoneNumber.text = taxNotice.taxPayer?.number ?: ""
            binding.txtEmailID.text = taxNotice.taxPayer?.email ?: ""
            binding.txtTaxAmount.text = formatWithPrecision(taxNotice.TotalDue)
            binding.txtEstimatedTax.text = formatWithPrecision(taxNotice.estimatedTax)
            var product = taxNotice.product ?: ""
            taxNotice.occupancy?.let {
                product = "$product ($it)"
            }
            taxNotice.taxSubTypeName?.let {
                binding.txtShowName.text = it
            }
            binding.txtTaxType.text = product
            if (taxNotice.taxRuleBookCode == Constant.TaxRuleBook.SHOW.Code){
                binding.txtShowNameLabel.visibility = View.VISIBLE
                binding.txtShowName.visibility = View.VISIBLE
                binding.txtShowNameLabel.text = binding.txtShowNameLabel.context.getString(R.string.show_name)
                binding.txtEstimatedTax.visibility=View.GONE
            }else if (taxNotice.taxRuleBookCode == Constant.TaxRuleBook.HOTEL.Code){
                binding.txtShowNameLabel.visibility = View.VISIBLE
                binding.txtShowName.visibility = View.VISIBLE
                binding.txtShowNameLabel.text = binding.txtShowNameLabel.context.getString(R.string.hotel_name)
                binding.txtEstimatedTax.visibility=View.GONE
            }else{
                binding.txtEstimatedTax.visibility=View.VISIBLE
                binding.txtShowNameLabel.visibility = View.GONE
                binding.txtShowName.visibility = View.GONE
            }
        }
    }

}