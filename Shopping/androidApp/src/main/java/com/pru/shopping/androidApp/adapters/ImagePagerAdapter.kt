package com.pru.shopping.androidApp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pru.shopping.androidApp.databinding.LayoutImagePagerViewBinding

class ImagePagerAdapter(var list: List<String>?) :
    RecyclerView.Adapter<ImagePagerAdapter.MyViewHolder>() {
    inner class MyViewHolder(private val itemBinding: LayoutImagePagerViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        init {
            with(itemBinding) {
            }
        }

        fun bindData(todoItem: String) = with(itemBinding) {
            Glide.with(imgView).load(todoItem).into(imgView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val imageViewBinding = LayoutImagePagerViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return MyViewHolder(imageViewBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        list?.get(position)?.let { holder.bindData(it) }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    fun updateData(list: List<String>?) {
        this.list = list
        notifyDataSetChanged()
    }

    private var listener: ((item: String) -> Unit)? = null
    fun setItemClickListener(listener: (item: String) -> Unit) {
        this.listener = listener
    }
}