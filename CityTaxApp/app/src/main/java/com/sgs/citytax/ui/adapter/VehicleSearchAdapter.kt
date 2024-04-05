package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemVehicleSearchListBinding
import com.sgs.citytax.model.VehicleDetails
import com.sgs.citytax.util.IClickListener

class VehicleSearchAdapter(val vehicleDetails:ArrayList<VehicleDetails>, val iClickListener: IClickListener) : RecyclerView.Adapter<VehicleSearchAdapter.VehicleSearchViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleSearchViewHolder {
        return VehicleSearchViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_vehicle_search_list,parent,false))
    }

    override fun onBindViewHolder(holder: VehicleSearchViewHolder, position: Int) {
        holder.bind(vehicleDetails[position],iClickListener)
    }

    override fun getItemCount(): Int {
        return vehicleDetails.size
    }

    class VehicleSearchViewHolder(var mBinding:ItemVehicleSearchListBinding): RecyclerView.ViewHolder(mBinding.root){
        fun bind(vehicleDetails: VehicleDetails,iClickListener: IClickListener){
            mBinding.tvName.text = vehicleDetails.vehicleNumber
            vehicleDetails.owner?.let {
                mBinding.tvOwner.text = it
            }

            mBinding.llRootView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    iClickListener.onClick(it, adapterPosition, vehicleDetails)
                }
            }
        }
    }
}