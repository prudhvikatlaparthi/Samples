package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GetOutstanding
import com.sgs.citytax.databinding.ItemOutstandingBinding
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.OnSingleClickListener
import com.sgs.citytax.util.formatWithPrecision

class OutstandingAdapter(iClickListener: IClickListener, private val mode: Constant.ScreenMode?) : RecyclerView.Adapter<OutstandingAdapter.ViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener
    private var mArrayList: ArrayList<GetOutstanding> = arrayListOf()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_outstanding, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        if (!edit)
//            mBinderHelper.lockSwipe(position.toString())

        when (mode) {
            Constant.ScreenMode.VIEW -> {
                holder.binding.txtDelete.visibility = View.GONE
            }
        }

        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun update(list: List<GetOutstanding>) {
        for (item: GetOutstanding in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemOutstandingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(outstandig: GetOutstanding, iClickListener: IClickListener?) {
            binding.txtType.text = outstandig.outstandingType.toString()
            binding.txtProduct.text = outstandig.product
            binding.txtNetReceivable.text = formatWithPrecision(outstandig.netReceivable)
            binding.txtYear.text = "${outstandig.year}"

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener(object : OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, outstandig)
                    }
                })
                binding.txtDelete.setOnClickListener (object : OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, outstandig)                    }
                })
            }
        }
    }

}