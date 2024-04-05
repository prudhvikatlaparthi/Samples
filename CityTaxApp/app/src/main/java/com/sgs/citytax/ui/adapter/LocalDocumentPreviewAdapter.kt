package com.sgs.citytax.ui.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemDocumentPreviewBinding
import com.sgs.citytax.model.LocalDocument
import com.sgs.citytax.util.IClickListener

class LocalDocumentPreviewAdapter(
    private val localDocuments: List<LocalDocument>,
    iClickListener: IClickListener
) : RecyclerView.Adapter<LocalDocumentPreviewAdapter.DocumentPreviewHolder>() {

    private var mIClickListener: IClickListener? = iClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentPreviewHolder {
        return DocumentPreviewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_document_preview, parent, false
            ), parent.context
        )
    }

    override fun onBindViewHolder(holder: DocumentPreviewHolder, position: Int) {
        holder.bind(localDocuments[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return localDocuments.size
    }


    class DocumentPreviewHolder(var binding: ItemDocumentPreviewBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(localDoc: LocalDocument, iClickListener: IClickListener?) {
            if (!TextUtils.isEmpty(localDoc.localSrc)) {
                Glide.with(context).load(localDoc.localSrc).into(binding.itemImageDocumentPreview)
            }
            if (iClickListener != null) {
                binding.itemImageDocumentPreview.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, localDoc)
                }
            }
        }
    }

}
