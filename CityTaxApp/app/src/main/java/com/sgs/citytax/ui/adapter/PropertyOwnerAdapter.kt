package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.PropertyOwners
import com.sgs.citytax.api.response.StorePropertyOwnershipWithPropertyOwnerResponse
import com.sgs.citytax.databinding.ItemPropertyOwnerBinding
import com.sgs.citytax.model.COMPropertyOwner
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.displayFormatDate
import com.sgs.citytax.util.serverFormatDate

class PropertyOwnerAdapter(val propertyOwner: ArrayList<StorePropertyOwnershipWithPropertyOwnerResponse>, val iClickListener: IClickListener?) : RecyclerView.Adapter<PropertyOwnerAdapter.PropertyImageViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyImageViewHolder {
        return PropertyImageViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_property_owner, parent, false)
                , parent.context)
    }

    override fun onBindViewHolder(holder: PropertyImageViewHolder, position: Int) {
        mBinderHelper.bind(holder.mBinding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(propertyOwner[position], iClickListener)
    }

    override fun getItemCount(): Int {
        return propertyOwner.size
    }


    class PropertyImageViewHolder(val mBinding: ItemPropertyOwnerBinding, val context: Context) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(comPropertyImage: StorePropertyOwnershipWithPropertyOwnerResponse, iClickListener: IClickListener?) {
            comPropertyImage.fromDate?.let {
                if (it != null)
                    mBinding.txtFromDate.text = displayFormatDate(it)
            }

            comPropertyImage.toDate?.let {
                if (it != null)
                    mBinding.txtToDate.text = displayFormatDate(it)
            }

            mBinding.llRegistraionNo.visibility = View.GONE
            comPropertyImage.registrationNo?.let {
                if (it != null)
                    mBinding.txtRegistrationNo.text = it
            }

            if (iClickListener != null) {
                mBinding.txtPropEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, comPropertyImage)
                }
//                mBinding.txtDelete.setOnClickListener {
//                    iClickListener.onClick(it, adapterPosition, comPropertyImage)
//                }
            }

        }
    }
}