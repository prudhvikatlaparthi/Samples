package com.sgs.citytax.ui.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R

class DialogAdapter : RecyclerView.Adapter<DialogAdapter.DialogAdapterViewHolder>() {

    val resultList: MutableList<Any> = mutableListOf()

    class DialogAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.mTextView)

        fun onBind(info: String) {
            textView.text = info
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_dialog_adapter_view, parent, false)
        return DialogAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: DialogAdapterViewHolder, position: Int) {
        holder.onBind(differ.currentList[position].toString())
        holder.itemView.setOnClickListener {
            addItemListener?.let {
                it(differ.currentList[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateList(list: List<Any>) {
        resultList.addAll(list)
        differ.submitList(resultList)
        notifyDataSetChanged()
    }

    private var addItemListener: ((item: Any, position: Int) -> Unit)? = null
    fun addItemClickListener(listener: (item: Any, position: Int) -> Unit) {
        this.addItemListener = listener
    }

    private val differCallback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return false
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}