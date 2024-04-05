package com.sgs.citytax.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemAdvertisementImagesBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.util.IClickListener

class AdvertisementImageAdapter(val comDocumentReferences: ArrayList<COMDocumentReference>, val iClickListener: IClickListener?) : RecyclerView.Adapter<AdvertisementImageAdapter.AdvertisementImageViewHolder>() {

    private var isDeleteEnabled: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertisementImageViewHolder {
        return AdvertisementImageViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_advertisement_images, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: AdvertisementImageViewHolder, position: Int) {
        holder.bind(comDocumentReferences[position],comDocumentReferences, isDeleteEnabled, iClickListener)
    }

    override fun getItemCount(): Int {
        return comDocumentReferences.size
    }

    class AdvertisementImageViewHolder(val mBinding: ItemAdvertisementImagesBinding, val context: Context) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(comDocumentReference: COMDocumentReference,documentList:ArrayList<COMDocumentReference>, isDeleteEnabled: Boolean, iClickListener: IClickListener?) {
            mBinding.itemImageDocumentPreview.tag = documentList
            if (!comDocumentReference.data.isNullOrEmpty()) {
                val imageBytes: String = comDocumentReference.data!!
                val imageByteArray: ByteArray = Base64.decode(imageBytes, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                Glide.with(context).load(bitmap).into(mBinding.itemImageDocumentPreview)
            } else if (!comDocumentReference.awsfile.isNullOrEmpty())
                Glide.with(context).load(comDocumentReference.awsfile).into(mBinding.itemImageDocumentPreview)

            if (isDeleteEnabled) {
                mBinding.btnDelete.visibility = VISIBLE
                mBinding.btnDelete.setOnClickListener {
                    iClickListener?.onClick(it, adapterPosition, comDocumentReference)
                }
            }

           /* mBinding.itemImageDocumentPreview.setOnClickListener {
                iClickListener?.onClick(it, adapterPosition, comDocumentReference)
            }
*/
        }
    }

    fun enableDelete(flag: Boolean) {
        this.isDeleteEnabled = flag
    }

    fun get(): ArrayList<COMDocumentReference> {
        return comDocumentReferences
    }

}