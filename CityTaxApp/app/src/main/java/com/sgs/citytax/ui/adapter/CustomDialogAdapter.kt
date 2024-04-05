package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.CustomDialogListItem2Binding
import com.sgs.citytax.model.VUCOMLand
import com.sgs.citytax.model.VUCOMProperty

class CustomDialogAdapter(dialog: AlertDialog, listener: Listener) : RecyclerView.Adapter<CustomDialogAdapter.CustomDialogViewHolder>() {
    var list: ArrayList<Any>? = arrayListOf()
    var mBinding: CustomDialogListItem2Binding? = null
    var mDialog: AlertDialog = dialog
    var mListener: Listener? = listener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDialogViewHolder {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.custom_dialog_list_item_2, parent, false)
        return CustomDialogViewHolder(mBinding!!)
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    override fun onBindViewHolder(holder: CustomDialogViewHolder, position: Int) {
        holder.onBind(list?.get(position))
        holder.binding.listText.setOnClickListener {
            mListener?.onAdapterItemClick(mDialog,list?.get(position))
        }
    }

    fun doClearAndUpdateList(mList: ArrayList<Any>?) {
        this.list?.clear()
        mList?.let { this.list!!.addAll(it) }
        notifyDataSetChanged()
    }

    class CustomDialogViewHolder(var binding: CustomDialogListItem2Binding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(any: Any?) {
            binding.listText.text = any.toString()
        }
    }

    interface Listener {
        fun onAdapterItemClick(dialog: AlertDialog, any:Any?)
    }
}