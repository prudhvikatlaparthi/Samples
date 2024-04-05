package com.sgs.citytax.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemInventoryStatusBinding
import com.sgs.citytax.model.InventoryStatus
import com.sgs.citytax.util.formatWithPrecisionCustomDecimals
import com.sgs.citytax.util.getQuantity

class InventoryStatusAdapter(private val inventoryStatus: List<InventoryStatus>) : RecyclerView.Adapter<InventoryStatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_inventory_status,
                parent, false))
    }

    override fun getItemCount(): Int {
        return inventoryStatus.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(inventoryStatus[position])
    }

    class ViewHolder(var binding: ItemInventoryStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(inventoryStatus: InventoryStatus) {
            binding.txtProductName.text = inventoryStatus.product
            binding.txtStockInHand.text = inventoryStatus.stockInHand?.stripTrailingZeros()?.toPlainString()

            if(inventoryStatus.itemCode == null || TextUtils.isEmpty(inventoryStatus.itemCode)){
                binding.llItemCode.visibility = View.GONE
            }else{
                binding.llItemCode.visibility = View.VISIBLE
                binding.txtItemCode.text = inventoryStatus.itemCode
            }

            if(inventoryStatus.item == null || TextUtils.isEmpty(inventoryStatus.item)){
                binding.llItem.visibility = View.GONE
            }else{
                binding.llItem.visibility = View.VISIBLE
                binding.txtItem.text = inventoryStatus.item
            }
        }
    }
}