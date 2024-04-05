package com.sgs.citytax.ui.custom.customDropDown

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R

class CustomDropdownAdapter<T>(mCategorySelectedListener: CategorySelectedListener) : RecyclerView.Adapter<CustomDropdownAdapter.CustomDropdownViewHolder>() {
    var list: ArrayList<T> = arrayListOf()
    var categorySelectedListener: CategorySelectedListener = mCategorySelectedListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomDropdownViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_drop_down_item, parent, false)
        return CustomDropdownViewHolder(v)
    }

    override fun onBindViewHolder(holder: CustomDropdownViewHolder, position: Int) {
        holder.dropDownTextView.text = list[position].toString()
        holder.itemView.setOnClickListener {
            categorySelectedListener.onCategorySelected(position, list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: ArrayList<T>) {
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun clearList() {
        this.list.clear()
    }

    class CustomDropdownViewHolder(mItemView: View) : RecyclerView.ViewHolder(mItemView) {
        val dropDownTextView: TextView = mItemView.findViewById(R.id.dropDownTextView)
    }

    interface CategorySelectedListener {
        fun onCategorySelected(position: Int, item: Any?)
    }
}