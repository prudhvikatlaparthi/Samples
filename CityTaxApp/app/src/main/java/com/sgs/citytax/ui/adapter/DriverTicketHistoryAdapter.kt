package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemDriverTicketHistoryBinding
import com.sgs.citytax.model.TicketHistory
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatDate
import com.sgs.citytax.util.formatWithPrecision

class DriverTicketHistoryAdapter(private val ticketHistory: ArrayList<TicketHistory>) : RecyclerView.Adapter<DriverTicketHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_driver_ticket_history, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ticketHistory[position])
    }

    override fun getItemCount(): Int {
        return ticketHistory.size
    }

    class ViewHolder(var mBinding: ItemDriverTicketHistoryBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(ticketHistory: TicketHistory) {
            mBinding.llTicketId.visibility = View.GONE
            if (ticketHistory.invoiceTransactionTypeCode == Constant.InvoiceTransactionTypeCode.IMPOUNDMENT.code) {
                mBinding.txtTypeLabel.text = mBinding.txtTypeLabel.context.getString(R.string.impoundment_type)
                mBinding.txtDateLabel.text = mBinding.txtDateLabel.context.getString(R.string.impound_date)
            } else if (ticketHistory.invoiceTransactionTypeCode == Constant.InvoiceTransactionTypeCode.VIOLATION_TICKETS.code) {
                mBinding.llTicketId.visibility = View.VISIBLE
                mBinding.txtTypeLabel.text = mBinding.txtTypeLabel.context.getString(R.string.violation_type)
                mBinding.txtDateLabel.text = mBinding.txtDateLabel.context.getString(R.string.violation_date)
                ticketHistory.invoiceTransactionVoucherNo?.let {
                    mBinding.txtTicketId.text = it.toString()
                }
            }
            ticketHistory.invoiceTransactionVoucherDate?.let {
                mBinding.txtViolationDate.text = formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyHHmmss)
            }
            ticketHistory.violationType?.let {
                mBinding.txtViolationType.text = it
            }
            ticketHistory.vehicleNo?.let {
                mBinding.txtVehicleNo.text = it
            }
            ticketHistory.driver?.let {
                mBinding.txtDriverName.text = it
            }
            ticketHistory.vehicleOwner?.let {
                mBinding.txtVehicleOwner.text = it
            }
            ticketHistory.drivingLicenseNo?.let {
                mBinding.txtDrivingLicenseNumber.text = it
            }
            ticketHistory.fineAmount?.let {
                mBinding.txtFineAmount.text = formatWithPrecision(it)
            }

        }
    }
}