package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.model.PaymentBreakup
import com.sgs.citytax.util.formatWithPrecision
import java.util.*

class PaymentBreakUpAdapter : RecyclerView.Adapter<PaymentBreakUpAdapter.ViewHolder>() {

    private var paymentBreakups: List<PaymentBreakup> = ArrayList()
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.row_payment_breakup, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (paymentMode, amount) = paymentBreakups[position]
        holder.tvAmount.text = formatWithPrecision(amount)
        holder.tvPaymentMode.text = context?.getString(R.string.payment_accepted_with_mode, when(paymentMode){
            "CASH" -> context?.getString(R.string.cash)
            "WALLET" -> context?.getString(R.string.wallet)
            "MOBICASH" -> context?.getString(R.string.mobi_cash)
            "CHEQUE" -> context?.getString(R.string.cheque)
            else -> context?.getString(R.string.cash)
        })
    }

    override fun getItemCount(): Int {
        return paymentBreakups.size
    }

    fun updateBreakUps(paymentBreakups: List<PaymentBreakup>?) {
        if (paymentBreakups != null) this.paymentBreakups = paymentBreakups
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPaymentMode: TextView = view.findViewById(R.id.tvPaymentMode)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

}