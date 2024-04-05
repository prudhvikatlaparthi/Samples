package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.payload.AccountPhone
import com.sgs.citytax.databinding.ItemPhoneBinding
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.getString

class PhonesAdapter(private var arrayList: ArrayList<AccountPhone>, iClickListener: IClickListener, private var screenMode: Constant.ScreenMode?) : RecyclerView.Adapter<PhonesAdapter.PhoneViewHolder>() {

    private val binderHelper = ViewBinderHelper()
    private var iClickListener: IClickListener? = iClickListener

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        return PhoneViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_phone,
                        parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {
        if (screenMode == Constant.ScreenMode.VIEW) {
            holder.binding.txtDelete.visibility = View.GONE
        }

        binderHelper.bind(holder.binding.swipeLayout, position.toString())
        binderHelper.closeAll()
        holder.bind(arrayList[position], iClickListener)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class PhoneViewHolder(var binding: ItemPhoneBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(accountPhone: AccountPhone, iClickListener: IClickListener?) {
            binding.txtPhoneNo.text = accountPhone.number
            binding.txtPhoneType.text = accountPhone.phoneType
            binding.txtStatus.text = if (accountPhone.default.equals("Y", true)) getString(R.string.lbl_default) else ""

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, accountPhone)
                }

                binding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, accountPhone)
                }
            }
        }

    }
}