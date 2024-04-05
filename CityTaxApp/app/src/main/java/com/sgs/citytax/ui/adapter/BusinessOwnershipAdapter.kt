package com.sgs.citytax.ui.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemBusinessOwnershipBinding
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.ui.fragments.BusinessOwnerMasterFragment
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener

class BusinessOwnershipAdapter(iClickListener: IClickListener, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<BusinessOwnershipAdapter.ViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener
    private var mBusinessOwnerships: ArrayList<BusinessOwnership> = arrayListOf()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_business_ownership, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (edit == Constant.ScreenMode.VIEW) {
            holder.binding.txtDelete.visibility = View.GONE
        }
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mBusinessOwnerships[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mBusinessOwnerships.size
    }

    fun reset(list: ArrayList<BusinessOwnership>) {
        mBusinessOwnerships = list
        notifyDataSetChanged()
    }

    fun update(list: List<BusinessOwnership>) {
        for (item: BusinessOwnership in list)
            mBusinessOwnerships.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mBusinessOwnerships = arrayListOf()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemBusinessOwnershipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ownership: BusinessOwnership, iClickListener: IClickListener?) {
            var name = ""
            if (ownership.firstName != null && !TextUtils.isEmpty(ownership.firstName))
                name = ownership.firstName!! + " "
            if (ownership.lastName != null && !TextUtils.isEmpty(ownership.lastName))
                name += ownership.lastName
            binding.tvOwner.text = name
            binding.tvBusinessOwnerID.text = ownership.businessOwnerID

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, ownership)
                    }
                })
                binding.txtDelete.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, ownership)
                    }
                })
            }
        }
    }

}