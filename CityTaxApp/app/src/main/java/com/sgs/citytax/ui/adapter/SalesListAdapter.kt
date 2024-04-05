package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.SalesListResults
import com.sgs.citytax.databinding.SalesListBinding
import com.sgs.citytax.util.*

class SalesListAdapter(iClickListener: IClickListener, private var fromScreen: Constant.QuickMenu) : RecyclerView.Adapter<SalesListAdapter.ViewHolder>() {
    private var mClickListener: IClickListener? = iClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesListAdapter.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.sales_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mList = differ.currentList[position]
        holder.bind(mList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateAdapter(list: List<SalesListResults>) {
        differ.submitList(list)
    }

    inner class ViewHolder(val mBinding: SalesListBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(salesListItem: SalesListResults) {

            mBinding.tvSalesDate.text = formatDisplayDateTimeInMinutes(salesListItem.taxInvoiceDate)
            mBinding.llSalesReceiptNo.isVisible = salesListItem.paymentReceiptNo?.let {
                mBinding.tvSalesReceiptNo.text = it
                true
            } ?: false

//            mBinding.tvSalesItem.text = salesListItem.item
//            mBinding.tvSalesItemCode.text = salesListItem.itemCode
//            mBinding.tvQuantity.text = salesListItem.quantity.toString()
//            mBinding.tvPrice.text = formatWithPrecision(salesListItem.price)
//            mBinding.tvCitizenName.text = salesListItem.citizen
//            mBinding.tvCitizenSycotaxID.text = salesListItem.citizenSycoTaxID
//            mBinding.tvPaymentMode.text = salesListItem.paymentMode

            salesListItem.chequeNo?.let {
                mBinding.tvPaymentMode.text = mBinding.salesDate.context.getString(R.string.cheque)
                mBinding.llPaymentMode.isVisible = true
            }
            mBinding.tvSalesAmount.text = formatWithPrecision(salesListItem.salesAmount)
            mBinding.tvSalesOrderNo.text = salesListItem.salesOrderNo?.toString()
            mBinding.llStatus.isVisible =  salesListItem.chequeStatus?.let {
                if (!salesListItem.chequeStatus.equals("Rejeté")) {
                    mBinding.tvStatus.text = it
                    true
                } else false
            } ?: false

            mBinding.llPenaltyAmount.isVisible = salesListItem.penaltyAmount?.let {
                if(it.toInt() >0){
                    mBinding.penaltyAmount.text = formatWithPrecision(it)
                    true
                }else{
                    false
                }

            } ?: false


            mBinding.llProsecutionFees.isVisible = salesListItem.prosecutionFees?.let {
                if(it.toInt() >0){
                    mBinding.prosecutionFees.text = formatWithPrecision(it)
                    true
                }else{
                    false
                }
            } ?: false

            mBinding.llTaxName.isVisible =  salesListItem.product?.let {
                mBinding.tvTaxName.text = it
                true
            } ?: false

            mBinding.llTotalDue.isVisible =  salesListItem.netReceivable?.let {
                if(salesListItem.chequeStatus.equals("Rejeté") && fromScreen != Constant.QuickMenu.QUICK_MENU_SECURITY_TAX){
                    mBinding.totalDue.text = formatWithPrecision(it)
                    true
                }else false
            } ?: false

            mBinding.llBankName.isVisible =  salesListItem.bankName?.let {
                if (!salesListItem.chequeStatus.equals("Rejeté")) {
                    mBinding.bankName.text = it
                    true
                }else false
            } ?: false

            mBinding.llChequeNumber.isVisible =  salesListItem.chequeNo?.let {
                if (!salesListItem.chequeStatus.equals("Rejeté")) {
                    mBinding.chequeNumber.text = it
                    true
                }else false
            } ?: false

            mBinding.llChequeDate.isVisible =  salesListItem.chequeDate?.let {
                if (!salesListItem.chequeStatus.equals("Rejeté")) {
                    mBinding.chequeDate.text = displayFormatDate(it)
                    true
                }else false
            } ?: false

            mBinding.llChequeAmount.isVisible =  salesListItem.chequeAmount?.let {
                if (!salesListItem.chequeStatus.equals("Rejeté")) {
                    mBinding.chequeAmount.text = formatWithPrecision(it)
                    true
                }else false
            } ?: false

            salesListItem.paymentModeCode?.let {
                mBinding.tvPaymentMode.text = it
                mBinding.llPaymentMode.isVisible = true
            }

            if (fromScreen == Constant.QuickMenu.QUICK_MENU_SECURITY_TAX){
                mBinding.salesDate.text = mBinding.salesDate.context.getString(R.string.security_service_date)
                mBinding.salesReceiptNo.text = mBinding.salesDate.context.getString(R.string.security_service_receipt_no)
//                mBinding.llTotalDue.isVisible = false
//                mBinding.salesItem.text = mBinding.salesDate.context.getString(R.string.type_of_cover)
//                mBinding.salesItemCode.text = mBinding.salesDate.context.getString(R.string.type_of_cover_code)
                mBinding.salesAmount.text = mBinding.salesAmount.context.getString(R.string.security_service_amount)
            }

            mBinding.llReturn.setOnClickListener(object :OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    mClickListener?.onClick(v!!, adapterPosition, salesListItem)
                }
            })

        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<SalesListResults>() {
        override fun areItemsTheSame(
            oldItem: SalesListResults,
            newItem: SalesListResults
        ): Boolean {
            return oldItem.salesOrderNo == newItem.salesOrderNo
        }

        override fun areContentsTheSame(
            oldItem: SalesListResults,
            newItem: SalesListResults
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)

}