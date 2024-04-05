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
import com.sgs.citytax.databinding.ImpoundmentListBinding
import com.sgs.citytax.model.TicketHistory
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.list_item_cheque_details.view.*
import java.util.*

class ImpoundmentListAdapter(val clickListener: IClickListener, val from: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.impoundment_list, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mList = differ.currentList[position]
        (holder as ViewHolder).bind(mList, clickListener, from)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    class ViewHolder(val mBinding: ImpoundmentListBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(mImpondmentReturn: TicketHistory, clickListener: IClickListener, from: String) {
            if (from == "1") {
                mImpondmentReturn.let {

                }
            } else if (from == "2") {


                mBinding.llReturn.visibility = View.VISIBLE
                //mBinding.llViolation.visibility = View.GONE
                mBinding.impoundChequeDetailsWrapper.visibility = View.GONE
                mBinding.llBtnPay.visibility = View.VISIBLE
                mBinding.llQuantity.visibility = View.GONE

                if (mImpondmentReturn.invoiceTransactionTypeCode!! == (Constant.InvoiceTransactionTypeCode.IMPOUNDMENT.name)) {
                    mBinding.noHead.text = getString(R.string.impound_number_imp)
                    mBinding.dateHead.text = getString(R.string.impound_date)
                    mBinding.typeHead.text = getString(R.string.impoundment_type)
                    mBinding.classHead.text = getString(R.string.impound_class)
                    mBinding.detailsHead.text = getString(R.string.impound_details)
                    mBinding.subTypeHead.text = getString(R.string.impoundment_sub_type)
                }

                mBinding.tvViolationNo.text = mImpondmentReturn.invoiceTransactionVoucherNo.toString()
                mBinding.tvViolationDate.text = formatDisplayDateTimeInMillisecond(mImpondmentReturn.invoiceTransactionVoucherDate)
                mBinding.tvViolationType.text = mImpondmentReturn.impoundmentType
                mBinding.tvViolationSubtype.text = mImpondmentReturn.impoundmentSubType

                mBinding.llTicketNumber.visibility =
                    mImpondmentReturn.noticeReferenceNo?.let {
                        mBinding.tvTicketNumber.text = it.toString()
                        View.VISIBLE
                    } ?: View.GONE

                mBinding.tvViolationClass.text = mImpondmentReturn.violationClass
                mBinding.tvViolationDetails.text = mImpondmentReturn.violationDetails

                mBinding.tvVehicleNumber.text = mImpondmentReturn.vehicleNo
                mBinding.tvVehicleOwner.text = mImpondmentReturn.vehicleOwner

                mBinding.tvDriver.text = mImpondmentReturn.driver

                mBinding.tvpendingAmt.text = formatWithPrecision(mImpondmentReturn.transactionDue)
                mBinding.tvAmtPaid.text = formatWithPrecision(mImpondmentReturn.netReceivable?.minus(mImpondmentReturn.transactionDue!!))
                if (mImpondmentReturn.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code)
                {
                    mImpondmentReturn.quantity.let {
                        mBinding.llQuantity.visibility = View.VISIBLE
                        mBinding.tvQuantity.text = getQuantity(mImpondmentReturn.quantity.toString())
                    }
                }
                mBinding.llVehicleDetail.isVisible = mImpondmentReturn.violatorTypeCode?.toUpperCase(Locale.getDefault()) != Constant.ViolationTypeCode.ANIMAL.code


                //Show - Cheque details if status code is new -already paid - 28/03/22
                if (mImpondmentReturn.chequeStatusCode == Constant.CheckStatus.NEW.value) {
                    mBinding.impoundChequeDetailsWrapper.visibility = View.VISIBLE
                    mBinding.llBtnPay.visibility = View.GONE
                    mBinding.impoundChequeDetailsWrapper.apply {
                        mImpondmentReturn.chequeBankName?.let {
                            tvChequeBankNameLabel.text = getString(R.string.cheque_bank_name).dropLast(1)
                            tvChequeBankName.text = it
                        }
                        mImpondmentReturn.chequeNumber?.let { tvChequeNo.text = it }
                        mImpondmentReturn.chequeDate?.let {
                            tvChequeDate.text = displayFormatDate(it) }
                        mImpondmentReturn.chequeStatus?.let { tvChequeStatus.text = it}
                        mImpondmentReturn.amountToSettleByCheque?.let {
                            tvChequeAmount.text = formatWithPrecision(mImpondmentReturn.amountToSettleByCheque)
                        }
                    }
                }

            }

            mBinding.btnPay.setOnClickListener {
                clickListener.onClick(it, adapterPosition, mImpondmentReturn)
            }

        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<TicketHistory>() {
        override fun areItemsTheSame(oldItem: TicketHistory, newItem: TicketHistory): Boolean {
            return oldItem.invoiceTransactionVoucherNo == newItem.invoiceTransactionVoucherNo
//            return false
        }

        override fun areContentsTheSame(oldItem: TicketHistory, newItem: TicketHistory): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    /*fun add(list: TicketHistory?) {
        mImpondmentReturnList.add(list!!)
        notifyItemInserted(mImpondmentReturnList.size - 1)
    }

    fun addAll(mImpondmentReturnList: ArrayList<TicketHistory>?) {
        if (mImpondmentReturnList != null) {
            for (mList in mImpondmentReturnList) {
                add(mList)
            }
        }
    }

    fun remove(mList: TicketHistory?) {
        val position: Int = mImpondmentReturnList.indexOf(mList)
        if (position > -1) {
            mImpondmentReturnList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<TicketHistory> {
        return mImpondmentReturnList
    }

    fun clear() {
        mImpondmentReturnList = arrayListOf()
        notifyDataSetChanged()
    }*/

    interface Listener {
        fun onItemClick(list: TicketHistory, position: Int)
    }
}