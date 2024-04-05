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
import com.sgs.citytax.databinding.ItemDocumentPreviewBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.util.IClickListener

class DocumentPreviewAdapter(var comDocumentReferences: ArrayList<COMDocumentReference>, iClickListener: IClickListener) : RecyclerView.Adapter<DocumentPreviewAdapter.DocumentPreviewHolder>() {

    private var mIClickListener: IClickListener? = iClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentPreviewHolder {
        return DocumentPreviewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context)
                , R.layout.item_document_preview, parent, false)
                , parent.context)
    }

    override fun onBindViewHolder(holder: DocumentPreviewHolder, position: Int) {
        holder.bind(comDocumentReferences.get(position), mIClickListener)
    }

    override fun getItemCount(): Int {
        return comDocumentReferences.size
    }


    class DocumentPreviewHolder(var binding: ItemDocumentPreviewBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comDocumentReference: COMDocumentReference, iClickListener: IClickListener?) {
            if (comDocumentReference.awsfile != null && comDocumentReference.awsfile!!.isNotEmpty()) {
                Glide.with(context).load(comDocumentReference.awsfile).into(binding.itemImageDocumentPreview)
            } else if (comDocumentReference.data != null && comDocumentReference.data!!.isNotEmpty()) {
                val imageBytes: String = comDocumentReference.data!!
                val imageByteArray: ByteArray = Base64.decode(imageBytes, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                Glide.with(context).load(bitmap).into(binding.itemImageDocumentPreview)
            } else {
                binding.itemImageDocumentPreview.visibility = View.GONE
            }
            if (iClickListener != null) {
                binding.itemImageDocumentPreview.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, comDocumentReference)
                }
            }
        }
    }

    fun get(): ArrayList<COMDocumentReference>? {
        return comDocumentReferences
    }

}
