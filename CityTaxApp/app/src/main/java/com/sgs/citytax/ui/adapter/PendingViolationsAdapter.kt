package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemPendingViolationsBinding
import com.sgs.citytax.model.TicketHistory
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.list_item_cheque_details.view.*
import java.math.BigDecimal

class PendingViolationsAdapter(val clickListener: IClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_pending_violations, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ticketHistory = differ.currentList[position]
        (holder as ViewHolder).bind(ticketHistory, clickListener)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /*private fun add(ticketHistory: TicketHistory) {
        mTicketHistory.add(ticketHistory)
    }

    fun addAll(ticketHistories: List<TicketHistory>) {
        ticketHistories.forEach {
            add(it)
        }
        notifyDataSetChanged()
    }

    fun clear() {
        mTicketHistory.clear()
        notifyDataSetChanged()
    }
    */
    class ViewHolder(var mBinding: ItemPendingViolationsBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(ticketHistory: TicketHistory, clickListener: IClickListener) {
            mBinding.violationChequeDetailsWrapper.visibility = View.GONE
            mBinding.llBtnPay.visibility = View.VISIBLE

            ticketHistory.invoiceTransactionVoucherNo?.let {
                mBinding.txtViolationNo.text = it.toString()
            }

            mBinding.llTicketNumber.visibility =
                ticketHistory.noticeReferenceNo?.let {
                    mBinding.tvTicketNumber.text = it.toString()
                    View.VISIBLE
                } ?: View.GONE

            ticketHistory.violationType?.let {
                mBinding.txtViolationType.text = it
            }
            ticketHistory.violationClass?.let {
                mBinding.txtViolationSubType.text = it
            }
            ticketHistory.invoiceTransactionVoucherDate?.let {
                mBinding.txtViolationDate.text = formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyHHmmss)
            }
            ticketHistory.vehicleNo?.let {
                mBinding.txtVehicleNo.text = it
            }
            ticketHistory.vehicleOwner?.let {
                mBinding.txtVehicleOwner.text = it
            }
            ticketHistory.driver?.let {
                mBinding.txtDriverName.text = it
            }
            var due = BigDecimal.ZERO
            var netReceivable = BigDecimal.ZERO
            ticketHistory.transactionDue?.let {
                due = it
            }
            ticketHistory.netReceivable?.let {
                netReceivable = it
            }
            mBinding.txtPendingAmount.text = formatWithPrecision(due)
            mBinding.txtAmountPaid.text = formatWithPrecision(netReceivable.minus(due))

            //Show - Cheque details if status code is new -already paid - 28/03/22
            if (ticketHistory.chequeStatusCode == Constant.CheckStatus.NEW.value){
                mBinding.violationChequeDetailsWrapper.visibility = View.VISIBLE
                mBinding.llBtnPay.visibility = View.GONE
                mBinding.violationChequeDetailsWrapper.apply {
                    ticketHistory.chequeBankName?.let {
                        tvChequeBankNameLabel.text = getString(R.string.cheque_bank_name).dropLast(1)
                        tvChequeBankName.text = it
                    }
                    ticketHistory.chequeNumber?.let { tvChequeNo.text = it }
                    ticketHistory.chequeDate?.let {
                        tvChequeDate.text = displayFormatDate(it) }
                    ticketHistory.chequeStatus?.let { tvChequeStatus.text = it}
                    ticketHistory.amountToSettleByCheque?.let {
                        tvChequeAmount.text = formatWithPrecision(ticketHistory.amountToSettleByCheque)
                    }
                }
            }
            mBinding.btnPay.setOnClickListener {
                clickListener.onClick(it, adapterPosition, ticketHistory)
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<TicketHistory>() {
        override fun areItemsTheSame(oldItem: TicketHistory, newItem: TicketHistory): Boolean {
            return oldItem.invoiceTransactionVoucherNo == newItem.invoiceTransactionVoucherNo
        }

        override fun areContentsTheSame(oldItem: TicketHistory, newItem: TicketHistory): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

}