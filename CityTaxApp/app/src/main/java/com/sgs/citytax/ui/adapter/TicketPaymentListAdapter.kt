package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.databinding.ItemProgressBinding
import com.sgs.citytax.databinding.TicketPaymentListBinding
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.list_item_cheque_details.view.*

class TicketPaymentListAdapter(val clickListener: IClickListener, val listener: Listener, val from: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mImpondmentReturnList: ArrayList<ImpondmentReturn> = arrayListOf()
    private val mItem = 0
    private val mLoading = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            mItem -> {
                viewHolder = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.ticket_payment_list, parent, false))
            }
            mLoading -> {
                viewHolder = LoadingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_progress, parent, false))
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mList = mImpondmentReturnList[position]
        when (getItemViewType(position)) {
            mItem -> {
                (holder as ViewHolder).bind(mList, clickListener, listener, from, position)
            }
            mLoading -> {
                (holder as LoadingViewHolder).bind(mList)
            }
        }
    }

    override fun getItemCount(): Int {
        return mImpondmentReturnList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mImpondmentReturnList[position].isLoading)
            mLoading
        else mItem
    }


    class ViewHolder(val mBinding: TicketPaymentListBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(mImpondmentReturn: ImpondmentReturn, clickListener: IClickListener, listener: Listener, from: String, position: Int) {
            if (from == "1") {
                mImpondmentReturn?.let {

                }
            } else if (from == "2") {
                mBinding.llReturn.visibility = View.VISIBLE
                mBinding.ticketChequeDetailsWrapper.visibility = View.GONE
                mBinding.llBtnPay.visibility = View.VISIBLE

                //mBinding.llViolation.visibility = View.GONE
                if (mImpondmentReturn.invoiceTransactionTypeCode!! == (Constant.InvoiceTransactionTypeCode.IMPOUNDMENT.name)) {
                    mBinding.noHead.text = getString(R.string.impond_no)
                    mBinding.dateHead.text = getString(R.string.impound_date)
                    mBinding.typeHead.text = getString(R.string.impoundment_type)
                    /*mBinding.classHead.text = getString(R.string.impound_class)
                    mBinding.detailsHead.text = getString(R.string.impound_details)*/
                }

//                mBinding.tvViolationNo.text = mImpondmentReturn.noticeReferenceNo.toString()
//                mBinding.tvViolationDate.text = formatDateTimeInMillisecond(mImpondmentReturn.transactiondate)
//                mBinding.tvViolationType.text = mImpondmentReturn.violationType
//                mBinding.tvViolationClass.text = mImpondmentReturn.violationClass
//                mBinding.tvViolationDetails.text = mImpondmentReturn.violationDetails

                mBinding.tvVehicleNumber.text = mImpondmentReturn.vehicleNo
                mBinding.tvVehicleOwner.text = mImpondmentReturn.vehicleOwner

                mBinding.tvDriver.text = mImpondmentReturn.driver
                mBinding.tvViolator.text = mImpondmentReturn.violator

                mBinding.tvCurrentDue.text = formatWithPrecision(mImpondmentReturn.currentDue)
                mBinding.tvMinPayAmount.text = formatWithPrecision(mImpondmentReturn.minmumPayAmount)
                mBinding.tvAmount.text = formatWithPrecision(mImpondmentReturn.amount)

                mBinding.tvQuantity.text = getQuantity(mImpondmentReturn.quantity.toString())

                //Show - Cheque details if status code is new -already paid - 28/03/22
                if (mImpondmentReturn.chequeStatusCode == Constant.CheckStatus.NEW.value) {
                    mBinding.ticketChequeDetailsWrapper.visibility = View.VISIBLE
                    mBinding.llBtnPay.visibility = View.GONE
                    mBinding.ticketChequeDetailsWrapper.apply {
                        mImpondmentReturn.chequeBankName?.let {
                            tvChequeBankNameLabel.text = getString(R.string.cheque_bank_name).dropLast(1)
                            tvChequeBankName.text = it
                        }
                        mImpondmentReturn.chequeNumber?.let { tvChequeNo.text = it }
                        mImpondmentReturn.chequeDate?.let {
                            tvChequeDate.text = displayFormatDate(it) }
                        mImpondmentReturn.chequeStatus?.let { tvChequeStatus.text = it}
                        mImpondmentReturn.chequeAmount?.let {
                            tvChequeAmount.text = formatWithPrecision(mImpondmentReturn.chequeAmount)
                        }
                    }
                }

                if (mImpondmentReturn.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code)
                {
                    mBinding.llClassHead.visibility = View.GONE
                    mBinding.llVehicleNo.visibility = View.GONE
                    mBinding.llVehicleOwner.visibility = View.GONE
                    mBinding.llDriver.visibility = View.GONE
                    mBinding.llQuantity.visibility = View.VISIBLE

                    mBinding.noHead.text = getString(R.string.impond_no)
                    mBinding.dateHead.text = getString(R.string.impound_date)
                    mBinding.typeHead.text = getString(R.string.impoundment_type)
                    mBinding.detailsHead.text = getString(R.string.violation_details)

                    mBinding.tvViolationNo.text = mImpondmentReturn.noticeReferenceNo.toString()
                    mBinding.tvViolationDate.text = formatDisplayDateTimeInMillisecond(mImpondmentReturn.transactiondate)
                    mBinding.tvViolationType.text = mImpondmentReturn.impoundmentType
                    mBinding.tvViolationDetails.text = mImpondmentReturn.violationDetails
                    mBinding.tvViolator.text = mImpondmentReturn.goodsOwner

                }
                else
                {
                    mBinding.tvViolationNo.text = mImpondmentReturn.noticeReferenceNo.toString()
                    mBinding.tvViolationDate.text = formatDisplayDateTimeInMillisecond(mImpondmentReturn.transactiondate)
                    mBinding.tvViolationType.text = mImpondmentReturn.violationType
                    mBinding.tvViolationClass.text = mImpondmentReturn.violationClass
                    mBinding.tvViolationDetails.text = mImpondmentReturn.violationDetails
                }
            }

            mBinding.btnPay.setOnClickListener {
                clickListener.onClick(it, adapterPosition, mImpondmentReturn)
            }

            mBinding.llRootView.setOnClickListener {
                listener.onItemClick(mImpondmentReturn, position)
            }

        }
    }

    class LoadingViewHolder(var binding: ItemProgressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assetList: ImpondmentReturn) {
            if (assetList.isLoading)
                binding.loadMoreProgress.visibility = View.VISIBLE
            else binding.loadMoreProgress.visibility = View.GONE
        }
    }

    fun add(list: ImpondmentReturn?) {
        mImpondmentReturnList.add(list!!)
        notifyItemInserted(mImpondmentReturnList.size - 1)
    }

    fun addAll(mImpondmentReturnList: List<ImpondmentReturn?>) {
        for (mList in mImpondmentReturnList) {
            add(mList)
        }
    }

    fun remove(mList: ImpondmentReturn?) {
        val position: Int = mImpondmentReturnList.indexOf(mList)
        if (position > -1) {
            mImpondmentReturnList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun get(): ArrayList<ImpondmentReturn> {
        return mImpondmentReturnList
    }

    fun clear() {
        mImpondmentReturnList = arrayListOf()
        notifyDataSetChanged()
    }

    interface Listener {
        fun onItemClick(list: ImpondmentReturn, position: Int)
    }
}