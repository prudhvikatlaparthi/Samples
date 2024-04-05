package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemPropertyDocumentBinding
import com.sgs.citytax.databinding.ItemPropertyPlanImageBinding
import com.sgs.citytax.model.COMPropertyPlanImage
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class PropertyPlanImageAdapter(var propertyPlanImages: ArrayList<COMPropertyPlanImage>, iClickListener: IClickListener?,
                               val screenMode: Constant.ScreenMode?,
                               val fromScreen: Constant.QuickMenu?) : RecyclerView.Adapter<PropertyPlanImageAdapter.PropertyPlanImageViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyPlanImageViewHolder {
        return PropertyPlanImageViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_property_plan_image, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: PropertyPlanImageViewHolder, position: Int) {
        mBinderHelper.bind(holder.mBinding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(propertyPlanImages[position], mIClickListener, screenMode,fromScreen)
    }

    override fun getItemCount(): Int {
        return propertyPlanImages.size
    }


    class PropertyPlanImageViewHolder(val mBinding: ItemPropertyPlanImageBinding, val context: Context) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(propertyPlanImage: COMPropertyPlanImage, iClickListener: IClickListener?, screenMode: Constant.ScreenMode?, fromScreen: Constant.QuickMenu?) {

            propertyPlanImage.awsPath?.let {
                Glide.with(context).load(it).placeholder(R.drawable.ic_place_holder).override(72, 72).into(mBinding.imgProperty)
            }

            if (screenMode == Constant.ScreenMode.VIEW || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_PROPERTY)
                mBinding.btnClearImage.visibility = View.GONE
            else
                mBinding.btnClearImage.visibility = View.VISIBLE

            if (iClickListener != null) {
                mBinding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, propertyPlanImage)
                }

                mBinding.imgProperty.setOnClickListener {
                    iClickListener.onClick(it,adapterPosition,propertyPlanImage)
                }

                mBinding.btnClearImage.setOnClickListener {
                    iClickListener.onClick(it,adapterPosition,propertyPlanImage)
                }
            }

        }
    }
}