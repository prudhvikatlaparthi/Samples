package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.constraintlayout.solver.GoalRow
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemParkingTicketHistoryBinding
import com.sgs.citytax.model.ParkingTicket
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.formatDate
import com.sgs.citytax.util.formatWithPrecision

class ParkingTicketHistoryAdapter(private val listener: IClickListener, private var mParkingTickets: List<ParkingTicket>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(var binding: ItemParkingTicketHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(parkingTicket: ParkingTicket, listener: IClickListener) {
            parkingTicket.parkingPlace?.let {
                binding.tvParkingPlace.text = it
            }
            parkingTicket.parkingStartDate?.let {
                binding.tvParkingStartDate.text = formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyhhmmssaa)
            }
            parkingTicket.parkingEndDate?.let {
                binding.tvParkingEndDate.text = formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyhhmmssaa)
            }
            parkingTicket.noticeReferenceNo?.let {
                binding.tvNoticeReferenceNo.text = it
            }
            parkingTicket.parkingTicketID?.let {
                binding.tvParkingTicketID.text = it.toString()
            }
            parkingTicket.status?.let {
                binding.tvStatus.text = it
            }
            parkingTicket.currentDue?.let {
                binding.tvCurrentDue.text = formatWithPrecision(it)
            }
            parkingTicket.netReceivable?.let {
                binding.tvNetReceivable.text = formatWithPrecision(it)
            }
            parkingTicket.vehicleNo?.let {
                binding.tvVehicleNumber.text = it
            }
            parkingTicket.accountName?.let {
                binding.tvAccountName.text = it
            }
           binding.btnCancel.visibility =GONE

           /* if (MyApplication.getPrefHelper().isParkingMunicipalAgent()||MyApplication.getPrefHelper().isParkingThirdPartyAgent())
                binding.btnCancel.visibility = GONE*/

            if ((!MyApplication.getPrefHelper().isParkingMunicipalAgent()||!MyApplication.getPrefHelper().isParkingThirdPartyAgent())
                    && parkingTicket.currentDue == parkingTicket.netReceivable && parkingTicket.status != Constant.TaxInvoices.CANCELLED.Status)
                binding.btnCancel.visibility = VISIBLE


            binding.btnCancel.setOnClickListener {
                listener.onClick(it, adapterPosition, parkingTicket)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.item_parking_ticket_history, parent, false))
    }

    override fun getItemCount(): Int {
        return mParkingTickets.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val parkingTicket = mParkingTickets[position]
        (holder as ViewHolder).bind(parkingTicket, listener)
    }


}