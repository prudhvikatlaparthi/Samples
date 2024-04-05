package com.sgs.citytax.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ImpoundmentRowDocumentBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.IClickListener

class MultipleDocumentsAdapter (private val mArrayList: ArrayList<COMDocumentReference>, iClickListener: IClickListener) : RecyclerView.Adapter<MultipleDocumentsAdapter.ViewHolder>() {

    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.impoundment_row_document, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    class ViewHolder(var binding: ImpoundmentRowDocumentBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            comDocumentReference: COMDocumentReference,
            iClickListener: IClickListener?
        ) {

            if (comDocumentReference.awsfile != null && comDocumentReference.awsfile!!.isNotEmpty())
                Glide.with(context).load(comDocumentReference.awsfile)
                    .placeholder(R.drawable.ic_place_holder).override(72, 72)
                    .into(binding.imgDocument)
            else if (!TextUtils.isEmpty(comDocumentReference.localPath)) {
                val imagePath: String = comDocumentReference.localPath!!
                Glide.with(context).load(imagePath).placeholder(R.drawable.ic_place_holder)
                    .override(72, 72).into(binding.imgDocument)
            } else if (comDocumentReference.data != null) {
                val imageBytes: String = comDocumentReference.data!!
                Glide.with(context).load(imageBytes).placeholder(R.drawable.ic_place_holder)
                    .override(72, 72).into(binding.imgDocument)
            }

//            if (fromScreen == Constant.QuickMenu.QUICK_MENU_AGREEMENT)
//                if(screenMode== Constant.ScreenMode.EDIT)
//                    binding.btnClearImage.visibility= View.VISIBLE
//                else
//                    binding.btnClearImage.visibility= View.GONE

            if (iClickListener != null) {
                binding.imgDocument.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        iClickListener.onClick(it, adapterPosition, comDocumentReference)
                    }
                }

                binding.btnClearImage.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        iClickListener.onClick(it, adapterPosition, comDocumentReference)
                    }
                }
            }

        }
    }

}
