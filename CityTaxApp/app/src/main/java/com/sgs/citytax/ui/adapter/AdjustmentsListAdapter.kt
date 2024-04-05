package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.AdjustmentsListBinding
import com.sgs.citytax.model.AdjustmentsListResults
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDisplayDateTimeInMinutes
import java.math.BigDecimal

class AdjustmentsListAdapter(iClickListener: IClickListener) :
    RecyclerView.Adapter<AdjustmentsListAdapter.ViewHolder>() {
    private var mClickListener: IClickListener? = iClickListener
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdjustmentsListAdapter.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adjustments_list,
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

    fun updateAdapter(list: List<AdjustmentsListResults>) {
        differ.submitList(list)
    }

    inner class ViewHolder(val mBinding: AdjustmentsListBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(adjustmentsListItem: AdjustmentsListResults) {
            mBinding.tvAdjustmentDate.text =
                formatDisplayDateTimeInMinutes(adjustmentsListItem.AdjustmentDate)
            mBinding.tvAdjustmentType.text = adjustmentsListItem.AdjustmentType
            mBinding.tvAdministrativeOffc.text = adjustmentsListItem.AccountName
            mBinding.tvTaxCode.text = adjustmentsListItem.TaxCode
            mBinding.tvTaxName.text = adjustmentsListItem.Product
            mBinding.tvItemCode.text = adjustmentsListItem.ItemCode
            mBinding.tvItem.text = adjustmentsListItem.Item
            mBinding.tvUnit.text = adjustmentsListItem.Unit
            mBinding.tvStockInOut.text = adjustmentsListItem.StockInOut
            mBinding.tvquantity.text = BigDecimal.valueOf(adjustmentsListItem.Quantity ?: 0.0).stripTrailingZeros().toPlainString()
            mBinding.tvRemarks.text = adjustmentsListItem.Remarks

            if(mClickListener!= null){
                mBinding.llRootView.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION){
                        mClickListener?.onClick(it,adapterPosition,adjustmentsListItem)
                    }
                }
            }

        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<AdjustmentsListResults>() {
        override fun areItemsTheSame(
            oldItem: AdjustmentsListResults,
            newItem: AdjustmentsListResults
        ): Boolean {
            return oldItem.AdjustmentID == newItem.AdjustmentID
        }

        override fun areContentsTheSame(
            oldItem: AdjustmentsListResults,
            newItem: AdjustmentsListResults
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)

}