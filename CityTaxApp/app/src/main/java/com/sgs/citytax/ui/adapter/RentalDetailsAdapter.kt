package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemRentalDetailsBinding
import com.sgs.citytax.model.CRMPropertyRent
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatWithPrecision

class RentalDetailsAdapter(var crmPropertyrents: ArrayList<CRMPropertyRent>, var iClickListener: IClickListener, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<RentalDetailsAdapter.RentalDetailsVeiwHolder>() {

    val binderHelper = ViewBinderHelper()
    private lateinit var context: Context

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalDetailsVeiwHolder {
        context = parent.context
        return RentalDetailsVeiwHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_rental_details, parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: RentalDetailsVeiwHolder, position: Int) {
        if (edit == Constant.ScreenMode.VIEW) {
            holder.binding.txtDelete.visibility = View.GONE
        }
        binderHelper.bind(holder.binding.swipeLayout, position.toString())
        binderHelper.closeAll()
        holder.bind(crmPropertyrents[position], iClickListener, context)
    }

    override fun getItemCount(): Int {
        return crmPropertyrents.size
    }


    class RentalDetailsVeiwHolder(var binding: ItemRentalDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propertyRent: CRMPropertyRent, iClickListener: IClickListener, context: Context) {
            binding.txtRentType.text = context.resources.getString(R.string.rent_type) + ": ${propertyRent.rentType}"
            if (propertyRent.agreementNo != null && propertyRent.agreementNo?.isNotEmpty()!!)
                binding.txtAgreementNo.text = context.resources.getString(R.string.agreement_no) + ":  ${propertyRent.agreementNo}"
            binding.txtRentAmount.text = context.resources.getString(R.string.rent_amount) + ":  ${propertyRent.rentAmount} "

            propertyRent.estimatedTax?.let {
                binding.llEstimatedAmount.visibility = View.VISIBLE
                binding.tvEstimatedAmount.text = formatWithPrecision(it)
            }

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, propertyRent)
                }

                binding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, propertyRent)
                }
            }

        }
    }


}