package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.*
import com.sgs.citytax.databinding.ItemSalesReportBinding
import com.sgs.citytax.ui.custom.HorizontalReportItem
import com.sgs.citytax.util.formatDisplayDateMonth


class SalesReportAdapter(private var getGroupingSalesReportResponse: GetGroupingSalesReportResponse) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return SalesReportViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_sales_report,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SalesReportViewHolder -> {
                val item = getGroupingSalesReportResponse.headerT?.get(position)
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return getGroupingSalesReportResponse.headerT?.size ?: 0
    }

    fun submitList(
        getGroupingSalesReportResponse: GetGroupingSalesReportResponse
    ) {
        this.getGroupingSalesReportResponse = getGroupingSalesReportResponse
        notifyDataSetChanged()
    }

    inner class SalesReportViewHolder
    constructor(
        private val binding: ItemSalesReportBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(headerT: HeaderT?) {
            headerT?.let { item ->
                getGroupingSalesReportResponse.let { getGroupingSalesReportResponse ->
                    binding.levelWrapper.removeAllViews()

                    // First Level
                    val level = HorizontalReportItem(binding.levelWrapper.context)
                    level.updateView(
                        value = formatDisplayDateMonth(item.saldt),
                        qty = item.qty,
                        price =
                            item.salesAmount,
                        removeIcon = false,
                        margin = null,
                        bgColor = R.drawable.level_drawable
                    )

                    getGroupingSalesReportResponse.headerT1?.let { headerT1: List<HeaderT1> ->
                        headerT1.forEach { h1 ->
                            if (h1.saldt == item.saldt) {

                                // Second Level
                                val level1Item =
                                    HorizontalReportItem(binding.levelWrapper.context)
                                level1Item.updateView(
                                    value = h1.parentItemCategory,
                                    qty = h1.qty,
                                    price =
                                        h1.salesAmount,
                                    margin = 48,
                                    bgColor = R.drawable.level1_drawable
                                )

                                getGroupingSalesReportResponse.headerT2?.let { headerT2: List<HeaderT2> ->
                                    headerT2.forEach { h2 ->
                                        if (h2.saldt == item.saldt && h1.parentItemCategoryCode == h2.parentItemCategoryCode) {

                                            // Third Level
                                            val level2Item =
                                                HorizontalReportItem(binding.levelWrapper.context)
                                            level2Item.updateView(
                                                value = h2.itemCategory,
                                                qty = h2.qty,
                                                price =
                                                    h2.salesAmount,
                                                margin = 96,
                                                bgColor = R.drawable.level2_drawable
                                            )

                                            getGroupingSalesReportResponse.headerT3?.let { headerT3: List<HeaderT3> ->
                                                headerT3.forEach { h3 ->
                                                    if (h3.saldt == item.saldt && h1.parentItemCategoryCode == h2.parentItemCategoryCode && h2.itemCategoryCode == h3.itemCategoryCode) {

                                                        // Fourth Level
                                                        val level3Item =
                                                            HorizontalReportItem(binding.levelWrapper.context)
                                                        level3Item.updateView(
                                                            value = h3.item,
                                                            qty = h3.qty,
                                                            price =
                                                                h3.salesAmount,
                                                            removeIcon = true,
                                                            margin = 168,
                                                            bgColor = R.drawable.level3_drawable
                                                        )
                                                        level2Item.addView(level3Item)
                                                    }
                                                }
                                            }
                                            level1Item.addView(level2Item)
                                            level2Item.setOnClickListener {
                                                (0 until level2Item.childCount).forEach { i ->
                                                    val view = level2Item.getChildAt(i)
                                                    if (view is HorizontalReportItem) {
                                                        val child: HorizontalReportItem =
                                                            level2Item.getChildAt(i) as HorizontalReportItem
                                                        child.isVisible = !child.isVisible
                                                        level2Item.toggleItem(child.isVisible)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                level.addView(level1Item)

                                level1Item.setOnClickListener {
                                    (0 until level1Item.childCount).forEach { i ->
                                        val view = level1Item.getChildAt(i)
                                        if (view is HorizontalReportItem) {
                                            val child: HorizontalReportItem =
                                                level1Item.getChildAt(i) as HorizontalReportItem
                                            child.isVisible = !child.isVisible
                                            level1Item.toggleItem(child.isVisible)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    binding.levelWrapper.addView(level)

                    level.setOnClickListener {
                        (0 until level.childCount).forEach { i ->
                            val view = level.getChildAt(i)
                            if (view is HorizontalReportItem) {
                                val child: HorizontalReportItem =
                                    level.getChildAt(i) as HorizontalReportItem
                                child.isVisible = !child.isVisible
                                level.toggleItem(child.isVisible)
                            }
                        }
                    }
                }
            }
        }
    }
}
