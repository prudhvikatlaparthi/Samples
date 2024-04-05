package com.sgs.citytax.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.response.AgreementResultsList
import com.sgs.citytax.databinding.ItemDocumentPreviewBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.util.IClickListener

class AgreementPreviewAdapter(var comDocumentReferences: ArrayList<AgreementResultsList>, iClickListener: IClickListener) : RecyclerView.Adapter<AgreementPreviewAdapter.AgreementPreviewHolder>() {

    private var mIClickListener: IClickListener? = iClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgreementPreviewHolder {
        return AgreementPreviewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context)
            , R.layout.item_document_preview, parent, false)
            , parent.context)
    }

    override fun onBindViewHolder(holder: AgreementPreviewHolder, position: Int) {
        holder.bind(comDocumentReferences.get(position), mIClickListener)
    }

    override fun getItemCount(): Int {
        return comDocumentReferences.size
    }


    class AgreementPreviewHolder(var binding: ItemDocumentPreviewBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comDocumentReference: AgreementResultsList, iClickListener: IClickListener?) {
            if (comDocumentReference.awsPath != null && comDocumentReference.awsPath!!.isNotEmpty()) {
                Glide.with(context).load(comDocumentReference.awsPath).into(binding.itemImageDocumentPreview)
            }  else {
                binding.itemImageDocumentPreview.visibility = View.GONE
            }
            if (iClickListener != null) {
                binding.itemImageDocumentPreview.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, comDocumentReference)
                }
            }
        }
    }

    fun get(): ArrayList<AgreementResultsList>? {
        return comDocumentReferences
    }

}
