package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemDocumentPreviewBinding
import com.sgs.citytax.databinding.ItemPropertyPlanImagePreviewBinding
import com.sgs.citytax.model.COMPropertyPlanImage
import com.sgs.citytax.util.IClickListener

class PropertyPlanImagePreviewAdapter(var propertyPlans: ArrayList<COMPropertyPlanImage>, iClickListener: IClickListener) : RecyclerView.Adapter<PropertyPlanImagePreviewAdapter.ViewHolder>() {

    private var mIClickListener: IClickListener? = iClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context)
                , R.layout.item_property_plan_image_preview, parent, false)
                , parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(propertyPlans[position], propertyPlans, mIClickListener)
    }

    override fun getItemCount(): Int {
        return propertyPlans.size
    }

    class ViewHolder(var binding: ItemPropertyPlanImagePreviewBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(propertyPlan: COMPropertyPlanImage, images: ArrayList<COMPropertyPlanImage>, iClickListener: IClickListener?) {
            binding.itemPropertyPlanImagePreview.setTag(images)
            propertyPlan.awsPath.let {
                Glide.with(context).load(it).placeholder(R.drawable.ic_place_holder).override(150, 150).into(binding.itemPropertyPlanImagePreview)
            }
            if (iClickListener != null) {
                binding.itemPropertyPlanImagePreview.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, propertyPlan)
                }
            }
        }

    }
}