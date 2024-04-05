package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemCorporateTurnoverBinding
import com.sgs.citytax.model.VUCRMCorporateTurnover
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*

class CorporateTurnOverAdapter(private var showProportionalDutyOnCP: Boolean, private var arrayList: ArrayList<VUCRMCorporateTurnover>, iClickListener: IClickListener, private val edit: Constant.ScreenMode?, private var mProduct: String?) : RecyclerView.Adapter<CorporateTurnOverAdapter.CorporateTurOverViewHolder>() {

    private val binderHelper = ViewBinderHelper()
    private var iClickListener: IClickListener? = iClickListener

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CorporateTurOverViewHolder {
        return CorporateTurOverViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_corporate_turnover,
                        parent, false)
        )
    }

    override fun onBindViewHolder(holder: CorporateTurOverViewHolder, position: Int) {
        if (edit == Constant.ScreenMode.VIEW) {
            holder.binding.txtDelete.visibility = View.GONE
        }

        binderHelper.bind(holder.binding.swipeLayout, position.toString())
        binderHelper.closeAll()
        holder.bind(showProportionalDutyOnCP, arrayList[position], iClickListener, mProduct)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class CorporateTurOverViewHolder(var binding: ItemCorporateTurnoverBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(showProportionalDutyOnCP: Boolean, crmCorporateTurnover: VUCRMCorporateTurnover, iClickListener: IClickListener?, mProduct: String?) {
            binding.tvAmount.text = formatWithPrecision(crmCorporateTurnover.amount)

            crmCorporateTurnover.actualAmount?.let {
                binding.tvActualTurnOver.text = formatWithPrecision(it)
            }
            if(crmCorporateTurnover.allowDelete=="N") {
                binding.txtDelete.visibility = View.GONE
            }

            if (showProportionalDutyOnCP)
                crmCorporateTurnover.rentPerRateCycle.let {
                    binding.llRentAmount.visibility = VISIBLE
                    if (!mProduct.isNullOrEmpty()) {
                        binding.tvRentAmount.text = binding.root.context.getString(R.string.rent_per_rate_cycle, mProduct)
                    } else {
                        binding.tvRentAmount.text = binding.root.context.getString(R.string.rent_per_rate_cycle, "")
                    }
                    binding.tvRentPerAmount.text = formatWithPrecision(crmCorporateTurnover.rentPerRateCycle)
                }

            binding.llFromDate.visibility = GONE
            binding.llToDate.visibility = GONE

            crmCorporateTurnover.financialStartDate?.let {
                binding.tvFromDate.text = displayFormatDate(it)
                binding.llFromDate.visibility = VISIBLE
            }

            crmCorporateTurnover.financialEndDate?.let {
                binding.tvToDate.text = displayFormatDate(it)
                binding.llToDate.visibility = VISIBLE
            }

            crmCorporateTurnover.estimatedTax?.let {
                binding.llEstimatedAmount.visibility = VISIBLE
                binding.tvEstimatedAmount.text = formatWithPrecision(it)
            }

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, crmCorporateTurnover)
                    }
                })
                binding.txtDelete.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, crmCorporateTurnover)
                    }
                })
            }
        }

    }
}