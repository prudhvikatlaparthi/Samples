package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.RowPropertyOwnershipBinding
import com.sgs.citytax.model.VUCRMPropertyOwnership
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener


class PropertyOwnershipAdapter(private var propertyOwnerships: List<VUCRMPropertyOwnership>, iClickListener: IClickListener, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<PropertyOwnershipAdapter.ViewHolder>() {

    val binderHelper = ViewBinderHelper()
    private var iClickListener: IClickListener? = iClickListener

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.row_property_ownership, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (edit == Constant.ScreenMode.VIEW)
            holder.binding.txtDelete.visibility = View.GONE

        binderHelper.bind(holder.binding.swipeLayout, position.toString())
        binderHelper.closeAll()
        holder.bind(propertyOwnerships[position], iClickListener)
    }

    override fun getItemCount(): Int {
        return propertyOwnerships.size
    }

    class ViewHolder(var binding: RowPropertyOwnershipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propertyOwnership: VUCRMPropertyOwnership, iClickListener: IClickListener?) {
            binding.txtRegistrationNo.text = propertyOwnership.propertyName

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, propertyOwnership)
                }
                binding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, propertyOwnership)
                }
            }
        }
    }

}