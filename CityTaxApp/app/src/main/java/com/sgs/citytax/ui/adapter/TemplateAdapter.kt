package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemTemplateBinding
import com.sgs.citytax.util.Constant


class TemplateAdapter(val data: ArrayList<String?>, private val receiptType: Constant.ReceiptType) : RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_template, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], receiptType)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addTemplate(string: String) {
        data.add(string)
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: ItemTemplateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: String?, receiptType: Constant.ReceiptType) {
            binding.receiptWebView.settings.setSupportZoom(true)
            binding.receiptWebView.settings.builtInZoomControls = true
            binding.receiptWebView.settings.displayZoomControls = true

            if (content?.isEmpty() == true) {
                binding.receiptWebView.loadUrl(getFilePath(receiptType))
            } else
                binding.receiptWebView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
        }

        private fun getFilePath(receiptType: Constant.ReceiptType): String {
            val baseString = "file:///android_asset/%s/%s"
            val folderName: String?
            var fileName: String? = ""
            folderName = "35Inch"
            when (receiptType) {
                Constant.ReceiptType.TAX_RECEIPT -> {
                    fileName = "3.5TaxReceipt.html"
                }
                Constant.ReceiptType.TAX_NOTICE -> {
                    fileName = "3.5TaxNotice.html"
                }
                Constant.ReceiptType.TAX_NOTICE_HISTORY -> {
                    fileName = "3.5TaxNotice.html"
                }
                else -> {

                }
            }

            return String.format(baseString, folderName, fileName)
        }
    }

}