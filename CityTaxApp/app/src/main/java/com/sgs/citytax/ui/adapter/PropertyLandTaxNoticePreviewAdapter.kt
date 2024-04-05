package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemTaxNoticePreviewBinding
import com.sgs.citytax.model.SAL_TaxDetails
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatWithPrecision

class PropertyLandTaxNoticePreviewAdapter(private val taxNotices: List<SAL_TaxDetails>, private val sycoTaxID: String, private val fromScreen: Constant.QuickMenu) : RecyclerView.Adapter<PropertyLandTaxNoticePreviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_tax_notice_preview, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taxNotices[position], sycoTaxID, fromScreen)
    }

    override fun getItemCount(): Int {
        return taxNotices.size
    }

    class ViewHolder(var binding: ItemTaxNoticePreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taxNotice: SAL_TaxDetails, sycoTaxID: String, fromScreen: Constant.QuickMenu) {
            binding.txtSycoTaxID.text = sycoTaxID
            binding.txtBusinessName.text = taxNotice.taxPayer?.vuCrmAccounts?.accountName ?: ""
            binding.txtPhoneNumber.text = taxNotice.taxPayer?.number ?: ""
            binding.txtEmailID.text = taxNotice.taxPayer?.email ?: ""
            binding.txtTaxAmount.text = formatWithPrecision(taxNotice.TotalDue)
            binding.txtEstimatedTax.text = formatWithPrecision(taxNotice.estimatedTax)
            var product = taxNotice.product ?: ""

            taxNotice.occupancy?.let {
                product = "$product ($it)"
            }
            binding.txtTaxType.text = product
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_NOTICE)
                binding.txtBusinessNameLabel.text = binding.txtBusinessName.context.resources.getString(R.string.land_owner_name)
            else
                binding.txtBusinessNameLabel.text = binding.txtBusinessName.context.resources.getString(R.string.property_owner_name)

        }
    }

}