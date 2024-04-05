package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.StockTransferListReturn
import com.sgs.citytax.databinding.StockTransferListItemBinding
import com.sgs.citytax.model.StockTransferListResults
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.formatDisplayDateTimeInMinutes
import java.math.BigDecimal

class StockTransferListAdapter(iClickListener: IClickListener) :
    RecyclerView.Adapter<StockTransferListAdapter.ViewHolder>() {
    private var mClickListener: IClickListener? = iClickListener
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StockTransferListAdapter.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.stock_transfer_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mList = differ.currentList[position]
        holder.bind(mList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateAdapter(list: List<StockTransferListResults>) {
        differ.submitList(list)
    }

    inner class ViewHolder(val mBinding: StockTransferListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(stockTransfer: StockTransferListResults) {

            if (stockTransfer.allocationDate != null && stockTransfer.allocationDate!!.isNotEmpty()) {
                mBinding.tvDateLayout.visibility = View.VISIBLE
                mBinding.tvDate.text =
                    formatDisplayDateTimeInMinutes(stockTransfer.allocationDate)
            } else {
                mBinding.tvDateLayout.visibility = View.GONE
            }

            if (stockTransfer.itemCode != null && stockTransfer.itemCode!!.isNotEmpty()) {
                mBinding.tvItemCodeLayout.visibility = View.VISIBLE
                mBinding.tvItemCode.text = stockTransfer.itemCode
            } else {
                mBinding.tvItemCodeLayout.visibility = View.GONE
            }

            if (stockTransfer.product != null && stockTransfer.product!!.isNotEmpty()) {
                mBinding.tvProductLayout.visibility = View.VISIBLE
                mBinding.tvProduct.text = stockTransfer.product
            } else {
                mBinding.tvProductLayout.visibility = View.GONE
            }

            if (stockTransfer.unit != null && stockTransfer.unit!!.toString().isNotEmpty()) {
                mBinding.tvUnitLayout.visibility = View.VISIBLE
                mBinding.tvUnit.text = stockTransfer.unit.toString()
            } else {
                mBinding.tvUnitLayout.visibility = View.GONE
            }

            if (stockTransfer.fromAccountName != null && stockTransfer.fromAccountName!!.isNotEmpty()) {
                mBinding.tvFromAccountLayout.visibility = View.VISIBLE
                mBinding.tvFromAccount.text = stockTransfer.fromAccountName
            } else {
                mBinding.tvFromAccountLayout.visibility = View.GONE
            }

            if (stockTransfer.toAccountName != null && stockTransfer.toAccountName!!.isNotEmpty()) {
                mBinding.tvToAccountLayout.visibility = View.VISIBLE
                mBinding.tvToAccount.text = stockTransfer.toAccountName
            } else {
                mBinding.tvToAccountLayout.visibility = View.GONE
            }

            if (stockTransfer.quantity != null && stockTransfer.quantity!!.toString()
                    .isNotEmpty()
            ) {
                mBinding.tvquantityLayout.visibility = View.VISIBLE
                mBinding.tvquantity.text =
                    BigDecimal.valueOf(stockTransfer.quantity?.toDouble() ?: 0.0).stripTrailingZeros()
                        .toPlainString()
            } else {
                mBinding.tvquantityLayout.visibility = View.GONE
            }

            if (stockTransfer.remarks != null && stockTransfer.remarks!!.isNotEmpty()) {
                mBinding.tvRemarksLayout.visibility = View.VISIBLE
                mBinding.tvRemarks.text = stockTransfer.remarks
            } else {
                mBinding.tvRemarksLayout.visibility = View.GONE
            }
            mBinding.llRootView.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    mClickListener?.onClick(v!!,adapterPosition,stockTransfer)
                }
            })
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<StockTransferListResults>() {
        override fun areItemsTheSame(
            oldItem: StockTransferListResults,
            newItem: StockTransferListResults
        ): Boolean {
            return oldItem.itemCode == newItem.itemCode
        }

        override fun areContentsTheSame(
            oldItem: StockTransferListResults,
            newItem: StockTransferListResults
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)

}