package com.sgs.citytax.ui.adapter

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemPropertyDocumentBinding
import com.sgs.citytax.model.COMPropertyImage
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class PropertyImageAdapter(val propertyImages: ArrayList<COMPropertyImage>, val iClickListener: IClickListener?, val screenMode: Constant.ScreenMode?) : RecyclerView.Adapter<PropertyImageAdapter.PropertyImageViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyImageViewHolder {
        return PropertyImageViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_property_document, parent, false)
                , parent.context)
    }

    override fun onBindViewHolder(holder: PropertyImageViewHolder, position: Int) {
        mBinderHelper.bind(holder.mBinding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(propertyImages[position], iClickListener,screenMode)
    }

    override fun getItemCount(): Int {
        return propertyImages.size
    }


    class PropertyImageViewHolder(val mBinding: ItemPropertyDocumentBinding, val context: Context) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(comPropertyImage: COMPropertyImage, iClickListener: IClickListener?, screenMode: Constant.ScreenMode?) {
            comPropertyImage.description?.let {
                mBinding.tvDescription.text = it
            }

            comPropertyImage.default?.let {
                mBinding.tvDefault.text = it
            }

            comPropertyImage.awsPath?.let {
                Glide.with(context).load(it).into(mBinding.imgProperty)
            }

            if (screenMode == Constant.ScreenMode.VIEW){
                mBinding.txtDelete.visibility = View.GONE
            }else
                mBinding.txtDelete.visibility = View.VISIBLE

            if (iClickListener != null) {
                mBinding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, comPropertyImage)
                }
                mBinding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, comPropertyImage)
                }
            }

        }
    }
}