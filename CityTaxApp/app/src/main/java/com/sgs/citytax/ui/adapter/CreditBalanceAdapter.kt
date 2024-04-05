package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.CreditBalance
import com.sgs.citytax.databinding.RowCreditBalanceBinding
import com.sgs.citytax.util.DateTimeTimeZoneMillisecondFormat
import com.sgs.citytax.util.displayDateTimeTimeSecondFormat
import com.sgs.citytax.util.formatWithPrecision
import com.sgs.citytax.util.getDate

class CreditBalanceAdapter(private var creditBalances: List<CreditBalance>) : RecyclerView.Adapter<CreditBalanceAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.row_credit_balance, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, creditBalances[position])
    }

    override fun getItemCount(): Int {
        return creditBalances.size
    }

    class ViewHolder(var binding: RowCreditBalanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, creditBalance: CreditBalance) {
            binding.tvPaymentDate.text = getDate(creditBalance.date, DateTimeTimeZoneMillisecondFormat, displayDateTimeTimeSecondFormat)
            binding.tvPaymentMode.text = creditBalance.paymentMode
            
            if(!creditBalance.chequeStatus.isNullOrEmpty()) {
                binding.tvPaymentStatus.visibility = VISIBLE
                binding.tvPaymentStatus.text = creditBalance.chequeStatus
            }
            else
            {
                binding.tvPaymentStatus.visibility = GONE
                binding.tvPaymentStatus.text = ""
            }


            if (creditBalance.credit != null) {
                binding.tvAmount.text = formatWithPrecision(creditBalance.credit)
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorGreenLight))

                binding.tvTaxElement.text = ""
                binding.tvTaxElement.visibility = View.GONE

                binding.tvTransactionType.text = context.resources.getString(R.string.credit)
                binding.tvTransactionType.setTextColor(ContextCompat.getColor(context, R.color.colorGreenLight))

                binding.tvTaxPayerName.text = ""
            } else {
                binding.tvAmount.text = formatWithPrecision(creditBalance.debit)
                binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorRedDark))

                binding.tvTaxElement.text = creditBalance.taxElement
                binding.tvTaxElement.visibility = View.VISIBLE

                binding.tvTransactionType.text = context.resources.getString(R.string.debit)
                binding.tvTransactionType.setTextColor(ContextCompat.getColor(context, R.color.colorRedDark))

                binding.tvTaxPayerName.text = creditBalance.taxPayerName
            }
        }
    }

}