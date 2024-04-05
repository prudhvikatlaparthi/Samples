package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.SalesRepaymentItem
import com.sgs.citytax.databinding.LayoutSalesRepaymentItemBinding
import com.sgs.citytax.ui.custom.HorizontalHeaderValueItem
import com.sgs.citytax.util.CommonLogicUtils.convertObjectToMap


class SalesRepaymentAdapter(private val listener: ((item: SalesRepaymentItem) -> Unit)? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ChequeBounceViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_sales_repayment_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChequeBounceViewHolder -> {
                val item = differ.currentList[position]
                holder.bind(item)
                holder.itemView.setOnClickListener {
                    listener?.invoke(item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val diffCallback = object : DiffUtil.ItemCallback<SalesRepaymentItem>() {

        override fun areItemsTheSame(
            oldItem: SalesRepaymentItem,
            newItem: SalesRepaymentItem
        ): Boolean {
            return oldItem.sales_order_no == newItem.sales_order_no
        }

        override fun areContentsTheSame(
            oldItem: SalesRepaymentItem,
            newItem: SalesRepaymentItem
        ): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: MutableList<SalesRepaymentItem>) {
        differ.submitList(ArrayList(list))
    }

    inner class ChequeBounceViewHolder
    constructor(
        private val binding: LayoutSalesRepaymentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SalesRepaymentItem) = with(binding) {
            val map = binding.llSubItems.context.convertObjectToMap(obj = item,ignoreZeroItems = true)
            binding.llSubItems.removeAllViews()
            map.forEach { entry ->
                val subItem =
                    HorizontalHeaderValueItem(
                        context = binding.llSubItems.context,
                        attrs = null
                    )
                subItem.updateView(entry.value)
                binding.llSubItems.addView(subItem)
            }
        }
    }
}

