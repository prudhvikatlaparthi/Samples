package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemEmailBinding
import com.sgs.citytax.model.CRMAccountEmails
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.getString

class EmailsAdapter(private var crmAccountEmails: List<CRMAccountEmails>, iClickListener: IClickListener, private var screenMode: Constant.ScreenMode?) : RecyclerView.Adapter<EmailsAdapter.AccountEmailsViewHolder>() {

    private val binderHelper = ViewBinderHelper()
    private var iClickListener: IClickListener? = iClickListener

    init {
        binderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountEmailsViewHolder {
        return AccountEmailsViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_email, parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: AccountEmailsViewHolder, position: Int) {
        if (screenMode == Constant.ScreenMode.VIEW) {
            holder.binding.txtDelete.visibility = View.GONE
        }
        binderHelper.bind(holder.binding.swipeLayout, position.toString())
        binderHelper.closeAll()
        holder.bind(crmAccountEmails[position], iClickListener)
    }

    override fun getItemCount(): Int {
        return crmAccountEmails.size
    }


    class AccountEmailsViewHolder(var binding: ItemEmailBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(crmAccountEmail: CRMAccountEmails, iClickListener: IClickListener?) {
            binding.txtEmail.text = crmAccountEmail.email
            binding.txtEmailType.text = crmAccountEmail.EmailType
            binding.txtStatus.text = if (crmAccountEmail.default.equals("Y", true)) getString(R.string.lbl_default) else "-"

            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, crmAccountEmail)
                }

                binding.txtDelete.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, crmAccountEmail)
                }
            }
        }

    }
}