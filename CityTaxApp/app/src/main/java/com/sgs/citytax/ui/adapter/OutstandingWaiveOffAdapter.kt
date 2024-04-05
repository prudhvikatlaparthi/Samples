package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemInitialoutstandingWaiveoffBinding
import com.sgs.citytax.model.GetOutstandingWaiveOff
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatWithPrecision

class OutstandingWaiveOffAdapter(private val outstandingWaiveOff: List<GetOutstandingWaiveOff>, private val listener: IClickListener, private val fromScreen: Constant.QuickMenu?) : RecyclerView.Adapter<OutstandingWaiveOffAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_initialoutstanding_waiveoff,
                parent, false))
    }

    override fun getItemCount(): Int {
        return outstandingWaiveOff.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(outstandingWaiveOff[position], fromScreen, listener)
    }

    class ViewHolder(var binding: ItemInitialoutstandingWaiveoffBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(outstandingWaiveOff: GetOutstandingWaiveOff, fromScreen: Constant.QuickMenu?, listener: IClickListener) {
            binding.txtValueOne.text = outstandingWaiveOff.year.toString()
            binding.txtValueTwo.text = outstandingWaiveOff.product.toString()
            binding.txtValueThree.text = formatWithPrecision(outstandingWaiveOff.netReceivable)
            binding.txtValueFive.text = formatWithPrecision(outstandingWaiveOff.currentDue)
            binding.txtValueFour.text = if (outstandingWaiveOff.paymentSettledAmount == null) "0.0" else formatWithPrecision(outstandingWaiveOff.paymentSettledAmount)
            binding.txtValueSeven.text = if (outstandingWaiveOff.waiveOffSettledAmount == null) "0.0" else formatWithPrecision(outstandingWaiveOff.waiveOffSettledAmount)

            when (outstandingWaiveOff.taxRuleBookCode) {
                Constant.TaxRuleBook.HOTEL.Code -> {
                    binding.txtKeysix.text=binding.txtKeysix.context.getString(R.string.star)
                }
                Constant.TaxRuleBook.SHOW.Code -> {
                    binding.txtKeysix.text=binding.txtKeysix.context.getString(R.string.operator_type)
                }
            }


            if (outstandingWaiveOff.taxSubType.isNullOrEmpty()) {
                binding.llTaxSubType.visibility = View.GONE
            } else {
                binding.llTaxSubType.visibility = View.VISIBLE
                binding.txtValuesix.text = outstandingWaiveOff.taxSubType.toString()
            }
            binding.btnChildWaiveOff.setOnClickListener {
                listener.onClick(it, adapterPosition, outstandingWaiveOff)
            }

            fromScreen?.let {
                if(fromScreen == Constant.QuickMenu.QUICK_MENU_INDIVIDUAL_TAX_OUTSTANDING_WAIVE_OFF)
                    binding.llTaxSubType.visibility = View.GONE
            }

        }
    }

}