package com.sgs.citytax.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.response.AgreementResultsList
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.RowAgreementListBinding
import com.sgs.citytax.ui.custom.swipeUtils.ViewBinderHelper
import com.sgs.citytax.util.*
import com.sgs.citytax.util.displayFormatDate

class AgreementListAdapter  (
    iClickListener: IClickListener,
    private val fromScreen: Constant.QuickMenu,
    private val screenMode: Constant.ScreenMode?,private val lockSwipe: Boolean) : RecyclerView.Adapter<AgreementListAdapter.ViewHolder>() {

    private var mArrayList: ArrayList<AgreementResultsList> = arrayListOf()
    private val mBinderHelper = ViewBinderHelper()
    private var mIClickListener: IClickListener? = iClickListener

    init {
        mBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.row_agreement_list, parent, false), parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (lockSwipe)
        {
            mBinderHelper.lockSwipe(position.toString())
        }
        mBinderHelper.bind(holder.binding.swipeLayout, position.toString())
        mBinderHelper.closeAll()
        holder.bind(mArrayList[position], mIClickListener)
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    fun update(list: List<AgreementResultsList>) {
        for (item: AgreementResultsList in list)
            mArrayList.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        mArrayList = arrayListOf()
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: RowAgreementListBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(agreementResultsListRef: AgreementResultsList, iClickListener: IClickListener?) {
            agreementResultsListRef?.referenceNo?.let {
                binding.llRefNum.visibility= View.VISIBLE
                binding.tvReffNumber.text=agreementResultsListRef.referenceNo
            }
            if(agreementResultsListRef.legalAgreementNo!=null) {
                binding.llAgreementRef.visibility=View.VISIBLE
                binding.tvAgreementReffNumber.text = agreementResultsListRef.legalAgreementNo
            }
            if(agreementResultsListRef.dueNoticeType!=null) {
                binding.llDueNoticeType.visibility=View.VISIBLE
                binding.tvDueNoticeType.text = agreementResultsListRef.dueNoticeType
            }
            if(agreementResultsListRef.noticeReferenceNo!=null) {
                binding.llDueNoticeRefNo.visibility=View.VISIBLE
                binding.tvDueNoticeNo.text = agreementResultsListRef.noticeReferenceNo
            }
            agreementResultsListRef?.dueAgreementDate?.let {
                binding.llAgreementDate.visibility= View.VISIBLE
                binding.tvAgreementDate.text = serverFormatDatewithTime(agreementResultsListRef.dueAgreementDate)
            }
            agreementResultsListRef?.validUptoDate?.let {
                binding.llValidupto.visibility= View.VISIBLE
                binding.tvValidUpTo.text = displayFormatDate(agreementResultsListRef.validUptoDate)
            }
            agreementResultsListRef?.status?.let {
                binding.llStatus.visibility= View.VISIBLE
                binding.tvStatus.text = agreementResultsListRef.status
            }

            if (agreementResultsListRef.awsPath != null && agreementResultsListRef.awsPath!!.isNotEmpty())
                Glide.with(context).load(agreementResultsListRef.awsPath).placeholder(R.drawable.ic_place_holder).override(72, 72).into(binding.imgAgreement)


            if (iClickListener != null) {
                binding.txtEdit.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, agreementResultsListRef)
                    }
                })
                binding.imgAgreement.setOnClickListener (object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        iClickListener.onClick(v!!, adapterPosition, agreementResultsListRef)
                    }
                })

            }
        }
    }

}
