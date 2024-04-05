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
import com.sgs.citytax.databinding.DueAndAgreementRowDocumentBinding
import com.sgs.citytax.databinding.HandoverDueImageBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener

class HandoverDueImagesAdapter(iClickListener: IClickListener, private val fromScreen: Constant.QuickMenu, private val edit: Constant.ScreenMode?) : RecyclerView.Adapter<HandoverDueImagesAdapter.ViewHolder>() {

    private var mArrayList: ArrayList<COMDocumentReference> = arrayListOf()
    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.handover_due_image, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mIClickListener,edit!!,fromScreen)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun update(list: List<COMDocumentReference>) {
        for (item: COMDocumentReference in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: HandoverDueImageBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            comDocumentReference: COMDocumentReference,
            iClickListener: IClickListener?,
            screenMode: Constant.ScreenMode,
            fromScreen: Constant.QuickMenu
        ) {

            if (comDocumentReference.awsfile != null && comDocumentReference.awsfile!!.isNotEmpty())
                Glide.with(context).load(comDocumentReference.awsfile).placeholder(R.drawable.ic_place_holder).override(72, 72).into(binding.imgDocument)
            else if (comDocumentReference.data != null) {
                val imageBytes: String = comDocumentReference.data!!
                val imageByteArray: ByteArray = Base64.decode(imageBytes, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                Glide.with(context).load(bitmap).placeholder(R.drawable.ic_place_holder).override(72, 72).into(binding.imgDocument)
            }

            if (fromScreen == Constant.QuickMenu.QUICK_MENU_AGREEMENT)
            if(screenMode==Constant.ScreenMode.EDIT)
                binding.btnClearImage.visibility= View.VISIBLE
            else
                binding.btnClearImage.visibility= View.GONE

            if (iClickListener != null) {
                binding.imgDocument.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, comDocumentReference)
                }

                binding.btnClearImage.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, comDocumentReference)
                }
            }

        }
    }

}
