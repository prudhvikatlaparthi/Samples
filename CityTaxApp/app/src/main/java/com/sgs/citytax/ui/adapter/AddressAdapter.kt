package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemAddressBinding
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener


class AddressAdapter(iClickListener: IClickListener, private var screenMode: Constant.ScreenMode?) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener
    private var mArrayList: ArrayList<GeoAddress> = arrayListOf()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_address, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (screenMode == Constant.ScreenMode.VIEW) {
            holder.binding.txtDelete.visibility = View.GONE
        }
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun update(list: List<GeoAddress>) {
        for (item: GeoAddress in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: GeoAddress, iClickListener: IClickListener?) {
            binding.txtZone.text = address.zone
            binding.txtCountry.text = address.country
            binding.txtState.text = address.state
            binding.txtCity.text = address.city

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, address)
                }
                binding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, address)
                }
            }
        }
    }

}