package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.RowRopDetailBinding
import com.sgs.citytax.model.ROPDetails


class ROPAdapter(val listener: ROPAdapterListener) : RecyclerView.Adapter<ROPAdapter.ViewHolder>() {

    private var ropDetails: ArrayList<ROPDetails> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.row_rop_detail, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ropDetails[position], listener)
    }

    override fun getItemCount(): Int {
        return ropDetails.size
    }

    fun updateROPDetails(details: List<ROPDetails>?) {
        if (details == null) ropDetails.clear() else ropDetails = details as ArrayList<ROPDetails>
        notifyDataSetChanged()
    }

    interface ROPAdapterListener {
        fun collectPayment(ropDetails: ROPDetails)
    }

    class ViewHolder(var binding: RowRopDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ropDetail: ROPDetails, listener: ROPAdapterListener) {
            binding.tvOccupationNature.text = ropDetail.occupancyName
            binding.tvActivityDescription.text = ropDetail.description
            binding.tvLength.text = ropDetail.length.toString()
            binding.tvTotal.text = ropDetail.Amount.toString()
            binding.tvDue.text = ropDetail.due.toString()
            binding.tvHeadsCount.text = ropDetail.invoiceCount.toString()
            binding.btnCollect.setOnClickListener { if (ropDetail.due > 0) listener.collectPayment(ropDetail) }
        }
    }

}