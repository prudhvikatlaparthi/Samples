package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.Business
import com.sgs.citytax.databinding.ItemBusinessListBinding
import com.sgs.citytax.util.IClickListener

class BusinessAdapter(iClickListener: IClickListener, private val isImpContra: Boolean = false) : RecyclerView.Adapter<BusinessAdapter.ViewHolder>() {

    private var mIClickListener: IClickListener? = iClickListener
    private var mBusiness: ArrayList<Business> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_business_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mBusiness[position], mIClickListener,isImpContra)
    }

    override fun getItemCount(): Int {
        return mBusiness.size
    }

    fun reset(list: ArrayList<Business>) {
        mBusiness = list
        notifyDataSetChanged()
    }

    fun addAll(list: List<Business>) {
        mBusiness.clear()
        mBusiness.addAll(list)
        notifyDataSetChanged()
    }

    fun update(list: List<Business>) {
        for (item: Business in list)
            mBusiness.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mBusiness = arrayListOf()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemBusinessListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(business: Business, iClickListener: IClickListener?,isImpContra: Boolean) {
            if (isImpContra){
                binding.llMobileNo.isVisible = true
                binding.llIDSycoTax.isVisible = false
            }
            binding.tvMblNo.text = business.number
            binding.tvSycoTaxID.text = business.sycotaxID
            binding.tvBusinessName.text = business.businessName
            binding.tvBusinessOwner.text = business.owners

            if (iClickListener != null) {
                binding.llContainer.setOnClickListener { view: View? -> iClickListener.onClick(view!!, adapterPosition, business) }
            }
        }
    }

}