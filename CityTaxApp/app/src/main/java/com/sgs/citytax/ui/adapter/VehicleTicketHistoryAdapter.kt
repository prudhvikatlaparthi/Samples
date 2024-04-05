package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.databinding.ItemVehicleTicketHistoryBinding
import com.sgs.citytax.model.VehicleTicketHistoryDetails
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.formatDisplayDateTimeInMillisecond
import com.sgs.citytax.util.formatWithPrecision

class VehicleTicketHistoryAdapter(val ticketHistoryDetails: ArrayList<VehicleTicketHistoryDetails>) : RecyclerView.Adapter<VehicleTicketHistoryAdapter.TicketHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketHistoryViewHolder {
        return TicketHistoryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_vehicle_ticket_history, parent, false))
    }

    override fun onBindViewHolder(holder: TicketHistoryViewHolder, position: Int) {
        holder.bind(ticketHistoryDetails[position])
    }

    override fun getItemCount(): Int {
        return ticketHistoryDetails.size
    }


    class TicketHistoryViewHolder(var mBinding: ItemVehicleTicketHistoryBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(ticketHistory: VehicleTicketHistoryDetails) {
            mBinding.llTicketId.visibility = View.GONE
            if (ticketHistory.transactionTypeCode == Constant.InvoiceTransactionTypeCode.IMPOUNDMENT.code) {
                mBinding.txtTypeLabel.text = mBinding.txtTypeLabel.context.getString(R.string.impoundment_type)
                mBinding.txtDateLabel.text = mBinding.txtDateLabel.context.getString(R.string.impound_date)
            } else if (ticketHistory.transactionTypeCode == Constant.InvoiceTransactionTypeCode.VIOLATION_TICKETS.code) {
                mBinding.llTicketId.visibility = View.VISIBLE
                mBinding.txtTypeLabel.text = mBinding.txtTypeLabel.context.getString(R.string.violation_type)
                mBinding.txtDateLabel.text = mBinding.txtDateLabel.context.getString(R.string.violation_date)
                ticketHistory.voucherNo?.let {
                    mBinding.txtTicketId.text = it.toString()
                }
            }
            ticketHistory.violationType?.let {
                mBinding.txtViolationType.text = it
            }
            ticketHistory.transactionDate?.let {
                mBinding.txtViolationDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            ticketHistory.vehicleNo?.let {
                mBinding.txtVehicleNo.text = it
            }
            ticketHistory.vehicleOwner?.let {
                mBinding.txtVehicleOwner.text = it
            }
            ticketHistory.driverName?.let {
                mBinding.txtDriverName.text = it
            }
            ticketHistory.drivingLicenseNumber?.let {
                mBinding.txtDrivingLicenseNumber.text = it
            }
            ticketHistory.fineAmount?.let {
                mBinding.txtFineAmount.text = formatWithPrecision(it)
            }
        }
    }
}