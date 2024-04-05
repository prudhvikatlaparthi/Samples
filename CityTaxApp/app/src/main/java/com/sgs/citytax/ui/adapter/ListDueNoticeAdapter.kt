package com.sgs.citytax.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ListDueNoticeResult
import com.sgs.citytax.util.*

class ListDueNoticeAdapter(iClickListener: IClickListener) : RecyclerView.Adapter<ListDueNoticeAdapter.ListDueNoticeViewHolder>() {

    private var mIClickListener: IClickListener? = iClickListener
    private var data:ArrayList<ListDueNoticeResult> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListDueNoticeViewHolder {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.list_due_notice_item,parent,false)
       return ListDueNoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListDueNoticeViewHolder, position: Int) {
       holder.bind(data[position],mIClickListener)
    }

    fun update(list:List<ListDueNoticeResult>){
        for (item: ListDueNoticeResult in list)
            data.add(item)
        notifyDataSetChanged()
    }

    fun clear(){
        data= arrayListOf()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    inner class ListDueNoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        //TextView Value's
        private var dueNoticeStatus:TextView
        private var dueNoticeVoucher:TextView
        private var date:TextView
        private var referenceNo:TextView
        private var reportDateTime:TextView
        private var recipientName:TextView
        private var mobileNum:TextView


        //TextView Text's
        private var dueNoticeStatusText:TextView
        private var agreement_ref_no:TextView
        private var agreementRefNo:TextView
        private var dueNoticeType:TextView
        private var dueNoticeVoucherText:TextView
        private var dateText:TextView
        private var referenceNoText:TextView
        private var btnImages:Button
        private var btnSignature : Button
        private var viewReportDateTIme : TextView
        private var viewRecipientname : TextView
        private var viewMobileNum : TextView



        init {

            //TextView Value's
            reportDateTime=itemView.findViewById(R.id.tvReportingDate)
            recipientName = itemView.findViewById(R.id.tvRecipientName)
            mobileNum = itemView.findViewById(R.id.tvMobileNo)
            dueNoticeStatus=itemView.findViewById(R.id.dueNoticeStatusId)
            dueNoticeType=itemView.findViewById(R.id.dueNoticeType)
            dueNoticeVoucher=itemView.findViewById(R.id.dueNoticeVoucherId)
            date=itemView.findViewById(R.id.dueNoticeDateId)
            referenceNo=itemView.findViewById(R.id.dueNoticeReferenceNoId)

            //TextView Text's
            dueNoticeStatusText=itemView.findViewById(R.id.dueNoticeStatusTextDN)
            viewReportDateTIme=itemView.findViewById(R.id.reportingDateTime)
            viewRecipientname = itemView.findViewById(R.id.recipientName)
            viewMobileNum = itemView.findViewById(R.id.mobileNo)
            agreementRefNo=itemView.findViewById(R.id.agreementRefNo)
            agreement_ref_no=itemView.findViewById(R.id.agreement_ref_no)
            dueNoticeVoucherText=itemView.findViewById(R.id.dueNoticevoucherTextDN)
            dateText=itemView.findViewById(R.id.dateTextDN)
            referenceNoText=itemView.findViewById(R.id.referenceNoDN)
            btnImages=itemView.findViewById(R.id.btnImages)
            btnSignature=itemView.findViewById(R.id.btnSignature)
        }

        fun bind(dueNotice: ListDueNoticeResult?, iClickListener: IClickListener?){
            dueNotice?.let { result ->

                if(result.noticeReferenceNo==null || result.noticeReferenceNo.isNullOrEmpty()){
                    referenceNo.visibility= View.GONE
                    referenceNoText.visibility= View.GONE
                }else{
                    referenceNo.visibility= View.VISIBLE
                    referenceNoText.visibility= View.VISIBLE
                    referenceNo.text=result.noticeReferenceNo
                }
                if(result.dueNoticeType!=null){
                    dueNoticeType.text=result.dueNoticeType
                }
                if(result.legalAgreementNo!=null){
                    agreementRefNo.visibility=View.VISIBLE
                    agreement_ref_no.visibility=View.VISIBLE
                    agreement_ref_no.text=result.legalAgreementNo
                }else{
                    agreementRefNo.visibility=View.GONE
                    agreement_ref_no.visibility=View.GONE
                }

                if(result.dueNoticeDate==null || result.dueNoticeDate.isNullOrEmpty()){
                    date.visibility= View.GONE
                    dateText.visibility= View.GONE
                }else{
                    date.visibility= View.VISIBLE
                    dateText.visibility= View.VISIBLE
                    date.text= serverFormatDatewithTime(result.dueNoticeDate)
                }
                if((result.reportingDateTime==null || result.reportingDateTime.isNullOrEmpty()) || result.dueNoticeType?.contentEquals("CONVOCATION")==false ){
                    reportDateTime.visibility= View.GONE
                    viewReportDateTIme.visibility= View.GONE
                }else{
                    reportDateTime.visibility= View.VISIBLE
                    viewReportDateTIme.visibility= View.VISIBLE
                    reportDateTime.text= getDate(result.reportingDateTime.toString(), DateTimeTimeZoneMillisecondFormat, parkingdisplayDateTimeTimeSecondFormat)
                }

                if((result.recipientName==null || result.recipientName.isNullOrEmpty())){
                    recipientName.visibility = View.GONE
                    viewRecipientname.visibility = View.GONE
                }else{
                    recipientName.visibility = View.VISIBLE
                    viewRecipientname.visibility = View.VISIBLE
                    recipientName.text = result.recipientName
                }
                if((result.mobileNum==null || result.mobileNum.isNullOrEmpty())){
                    mobileNum.visibility = View.GONE
                    viewMobileNum.visibility = View.GONE
                }else{
                    mobileNum.visibility = View.VISIBLE
                    viewMobileNum.visibility = View.VISIBLE
                    mobileNum.text = result.mobileNum
                }

                if(result.status==null || result.status.isNullOrEmpty()){
                    dueNoticeStatus.visibility=View.GONE
                    dueNoticeStatusText.visibility= View.GONE
                }else{
                    dueNoticeStatus.visibility=View.VISIBLE
                    dueNoticeStatusText.visibility= View.VISIBLE
                    dueNoticeStatus.text=result.status
                }


                if(result.voucherNo==null || result.voucherNo.isNullOrEmpty()){
                    dueNoticeVoucher.visibility=View.GONE
                    dueNoticeVoucherText.visibility=View.GONE
                }else{
                    dueNoticeVoucher.text=result.voucherNo
                    dueNoticeVoucher.visibility=View.VISIBLE
                    dueNoticeVoucherText.visibility=View.VISIBLE
                }

            }
            if (iClickListener != null) {
                btnSignature.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        if (v != null) {
                            if (dueNotice != null) {
                                mIClickListener?.onClick(v,adapterPosition,dueNotice)
                            }
                        }
                    }
                })
                btnImages.setOnClickListener(object : OnSingleClickListener() {
                    override fun onSingleClick(v: View?) {
                        if (v != null) {
                            if (dueNotice != null) {
                                mIClickListener?.onClick(v,adapterPosition,dueNotice)
                            }
                        }
                    }
                })
            }
        }

    }
}