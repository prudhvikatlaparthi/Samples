package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CommissionHistory
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemPayoutDetailsBinding
import com.sgs.citytax.model.VUAgentCashCollectionSummary
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class RequestPayoutAdapter(var commissionHistories: List<CommissionHistory>, iClickListener: IClickListener) : RecyclerView.Adapter<RequestPayoutAdapter.RequestPayoutViewHolder>() {

    private var iClickListener: IClickListener? = iClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestPayoutViewHolder {
        return RequestPayoutViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_payout_details, parent, false))
    }

    override fun onBindViewHolder(holder: RequestPayoutViewHolder, position: Int) {
        holder.bind(commissionHistories[position], iClickListener)
    }

    override fun getItemCount(): Int {
        return commissionHistories.size
    }

    class RequestPayoutViewHolder(var binding: ItemPayoutDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(commissionHistory: CommissionHistory, iClickListener: IClickListener?) {
            binding.txtCommissionAmount.text = formatWithPrecision(commissionHistory.netPayable)
            binding.txtRequestedBy.text = commissionHistory.accountName
            binding.txtStatus.text = commissionHistory.status
            binding.txtRequestDate.text = formatDisplayDateTimeInMillisecond(commissionHistory.advanceDate)

            if (!commissionHistory.referenceNo.isNullOrEmpty()) {
                binding.txtVoucherNo.text = commissionHistory.referenceNo
            }

            binding.llApprovedBy.visibility = View.GONE
            binding.llApprovedDate.visibility = View.GONE
            binding.txtApprovedBy.text = ""
            binding.txtApprovedDate.text = ""
            binding.txtPaymentMode.text = ""
            binding.rlApproveReject.visibility = View.GONE
            binding.btnPrint.visibility = View.GONE
            binding.llPaymentMode.visibility = View.GONE

            if (MyApplication.getPrefHelper().isSupervisorOrInspector() && commissionHistory.statusCode.equals("ACC_AdvancePaid.Open")) {
                binding.rlApproveReject.visibility = View.VISIBLE
            } else if (commissionHistory.statusCode.equals("ACC_AdvancePaid.Approved")) {
                binding.btnPrint.visibility = View.VISIBLE

                if (MyApplication.getPrefHelper().superiorTo.isNotEmpty()) {
                    binding.llApprovedBy.visibility = View.VISIBLE
                    binding.llApprovedDate.visibility = View.VISIBLE
//                    binding.llPaymentMode.visibility = View.VISIBLE
                    binding.txtPaymentMode.text = commissionHistory.paymentMode
                    binding.txtApprovedBy.text = commissionHistory.approverName
                    binding.txtApprovedDate.text = formatDisplayDateTimeInMillisecond(commissionHistory.approvedDate)
                }

            } else if (commissionHistory.statusCode.equals("ACC_AdvancePaid.Rejected")) {
                binding.llRemarks.visibility = View.VISIBLE
                binding.txtRemarks.text = commissionHistory.remarks
            }

            if (iClickListener != null) {
                binding.btnPayoutApprove.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, commissionHistory)
                }
                binding.btnPayoutReject.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, commissionHistory)
                }

                binding.btnPrint.setOnClickListener {
                    iClickListener.onClick(it, adapterPosition, commissionHistory)
                }
            }
        }
    }
    private val differCallback = object : DiffUtil.ItemCallback<CommissionHistory>() {
        override fun areItemsTheSame(oldItem: CommissionHistory, newItem: CommissionHistory): Boolean {
            return oldItem.accountId == newItem.accountId
        }

        override fun areContentsTheSame(oldItem: CommissionHistory, newItem: CommissionHistory): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}
