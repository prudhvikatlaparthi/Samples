package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemStockInReportBinding
import com.sgs.citytax.model.AllocatedStock
import com.sgs.citytax.util.formatDateTimeInMillisecond
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.getQuantity

class StockInReportAdapter(private var list: List<AllocatedStock>) : RecyclerView.Adapter<StockInReportAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_stock_in_report, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(var binding: ItemStockInReportBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(stock: AllocatedStock) {
            stock.allocationDate?.let {
                binding.txtAllocationDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            binding.txtProduct.text = stock.product
            binding.txtProductCode.text = stock.productCode
            binding.txtFromAccountName.text = stock.fromAccountName
            binding.txtToAccountName.text = stock.toAccountName
            stock.quantity?.let {
                binding.txtQuantity.text = getQuantity(it)
            }

            stock.photo?.let {
                Glide.with(context).load(it).placeholder(R.drawable.ic_place_holder).override(92, 92).into(binding.imgProduct)
            }
        }

    }
}
