package com.pru.shopping.androidApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pru.shopping.androidApp.databinding.ItemTodoLayoutBinding

class ShopByCategoryAdapter(var list: List<String>?) :
    RecyclerView.Adapter<ShopByCategoryAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val itemBinding: ItemTodoLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        init {
            with(itemBinding) {
                cvItem.setOnClickListener {
                    if(adapterPosition != -1){
                        val data = list?.get(adapterPosition)
                        listener?.let {
                            data?.let { it1 -> it(it1) }
                        }
                    }
                }
            }
        }

        fun bindData(todoItem: String) = with(itemBinding) {
            tvTodoName.text = todoItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemTodoLayoutBinding = ItemTodoLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyViewHolder(itemTodoLayoutBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list?.get(position)?.let { holder.bindData(it) }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    fun updateData(list: List<String>?) {
        this.list = list
        notifyDataSetChanged()
    }

    private var listener: ((item : String) -> Unit)? = null
    fun setItemClickListener(listener: (item : String) -> Unit) {
        this.listener = listener
    }
}