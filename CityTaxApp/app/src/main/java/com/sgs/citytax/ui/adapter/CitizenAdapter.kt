package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CitizenDataTable
import com.sgs.citytax.databinding.CitizenLayoutItemBinding


class CitizenAdapter(
    private val mList: MutableList<CitizenDataTable>,
    private val clickListener: (CitizenDataTable) -> Unit
) :
    RecyclerView.Adapter<CitizenAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CitizenAdapter.ProductViewHolder {
        val mBinding: CitizenLayoutItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.citizen_layout_item, parent, false
        )
        return ProductViewHolder(mBinding, parent.context)
    }

    override fun onBindViewHolder(holder: CitizenAdapter.ProductViewHolder, position: Int) {
        holder.bind(mList[position])

    }

    override fun getItemCount(): Int {

        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ProductViewHolder(val binding: CitizenLayoutItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CitizenDataTable,
        ) {
            binding.title.text = item.Number
            binding.body.text = item.acctname

            binding.root.setOnClickListener {
                clickListener.invoke(item)
            }
        }
    }
}