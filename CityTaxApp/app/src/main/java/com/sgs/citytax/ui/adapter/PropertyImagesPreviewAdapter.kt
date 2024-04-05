package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemDocumentPreviewBinding
import com.sgs.citytax.databinding.ItemPropertyImagePreviewBinding
import com.sgs.citytax.model.COMPropertyImage
import com.sgs.citytax.util.IClickListener

class PropertyImagesPreviewAdapter(var propertyImages: ArrayList<COMPropertyImage>, iClickListener: IClickListener) : RecyclerView.Adapter<PropertyImagesPreviewAdapter.PropertyPreviewViewHolder>() {

    private var mIClickListener: IClickListener? = iClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyPreviewViewHolder {
        return PropertyPreviewViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context)
                , R.layout.item_property_image_preview, parent, false)
                , parent.context)
    }

    override fun onBindViewHolder(holder: PropertyPreviewViewHolder, position: Int) {
        holder.bind(propertyImages[position], propertyImages, mIClickListener)
    }

    override fun getItemCount(): Int {
        return propertyImages.size
    }


    class PropertyPreviewViewHolder(var binding: ItemPropertyImagePreviewBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propertyImage: COMPropertyImage, images: ArrayList<COMPropertyImage>, iClickListener: IClickListener?) {
            binding.itemPropertyImagePreview.setTag(images)
            propertyImage.awsPath.let {
                Glide.with(context).load(it).placeholder(R.drawable.ic_place_holder).override(150, 150).into(binding.itemPropertyImagePreview)
            }
            if (iClickListener != null) {
                binding.itemPropertyImagePreview.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, propertyImage)
                }
            }
        }

    }
}