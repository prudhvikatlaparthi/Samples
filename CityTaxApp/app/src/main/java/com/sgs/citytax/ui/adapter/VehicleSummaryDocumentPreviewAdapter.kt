package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.VehSummaryDocumentPreviewBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.util.IClickListener

class VehicleSummaryDocumentPreviewAdapter(var comDocumentReferences: ArrayList<COMDocumentReference>, iClickListener: IClickListener) : RecyclerView.Adapter<VehicleSummaryDocumentPreviewAdapter.DocumentPreviewHolder>() {

    private var mIClickListener: IClickListener? = iClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentPreviewHolder {
        return DocumentPreviewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context)
                , R.layout.veh_summary_document_preview, parent, false)
                , parent.context)
    }

    override fun onBindViewHolder(holder: DocumentPreviewHolder, position: Int) {
        holder.bind(comDocumentReferences.get(position), comDocumentReferences, mIClickListener)
    }

    override fun getItemCount(): Int {
        return comDocumentReferences.size
    }


    class DocumentPreviewHolder(var binding: VehSummaryDocumentPreviewBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comDocumentReference: COMDocumentReference, comDocumentReferences: ArrayList<COMDocumentReference>, iClickListener: IClickListener?) {


            binding.tvDocumentName.text = comDocumentReference.documentTypeName
            binding.itemImageDocumentPreview.setTag(comDocumentReferences)
            if (comDocumentReference.awsfile != null && comDocumentReference.awsfile!!.isNotEmpty()) {
                Glide.with(context).load(comDocumentReference.awsfile).placeholder(R.drawable.ic_place_holder).override(150, 150).into(binding.itemImageDocumentPreview)
            }
            if (iClickListener != null) {
                binding.itemImageDocumentPreview.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, comDocumentReference)
                }
            }
        }
    }


}
