package com.sgs.citytax.ui.viewHolder

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.PenaltyWaiveOffReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemPenaltyWaiveOffReceiptBinding
import com.sgs.citytax.databinding.ItemWaiveOffDetailsBinding
import com.sgs.citytax.model.PenaltyWaiveOffDetailsTable
import com.sgs.citytax.model.PenaltyWaiveOffReceiptChangeDetails
import com.sgs.citytax.util.*
import java.util.*

class PenaltyWaiveOffReceiptViewHolder(val mBinding: ItemPenaltyWaiveOffReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(penaltyWaiveOffResponse: PenaltyWaiveOffReceiptResponse, iClickListener: IClickListener) {

        bindReceiptDetails(penaltyWaiveOffResponse.penaltyWaiveReceiptChangeDetails[0],penaltyWaiveOffResponse)
        bindWaiveOffDetails(penaltyWaiveOffResponse.penaltyWaiveOffDetailsTable, penaltyWaiveOffResponse.penaltyWaiveReceiptChangeDetails[0])

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, penaltyWaiveOffResponse)
                }
            })
        }
    }

    private fun bindReceiptDetails(
        receiptDetails: PenaltyWaiveOffReceiptChangeDetails?,
        penaltyWaiveOffResponse: PenaltyWaiveOffReceiptResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = penaltyWaiveOffResponse.orgData
        )
        mBinding.txtSycoTaxIDLabel.text = String.format("%s%s", getString(R.string.syco_tax_id), getString(R.string.colon))
        mBinding.txtSectorLabel.text = String.format("%s%s", getString(R.string.sector), getString(R.string.colon))
        mBinding.txtTaxNoticeNoLabel.text = String.format("%s%s", getString(R.string.tax_notice_number), getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        if (receiptDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())
            var address: String? = ""

            receiptDetails.printCounts?.let {
                if (it>1) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                    mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
 //                   mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }
            receiptDetails.businessName?.let {
                mBinding.txtBusinessName.text = it
            }
            receiptDetails.sycoTaxId?.let {
                mBinding.txtSycoTaxID.text = it
            }
            receiptDetails.businessOwners?.let {
                if (it.contains(";"))
                    it.replace(";", "\n")
                mBinding.txtBusinessOwner.text = it
            }

            receiptDetails.state?.let {
                mBinding.txtState.text = it
                address += it
                address += ","
            }
            receiptDetails.city?.let {
                mBinding.txtCity.text = it
                address += it
                address += ","
            }

            receiptDetails.zone?.let {
                mBinding.txtArdt.text = it
                address += it
                address += ","
            }
            receiptDetails.sector?.let {
                mBinding.txtSector.text = it
                address += it
                address += ","
            }
            receiptDetails.plot?.let {
                mBinding.txtSection.text = it
                address += it
                address += ","
            }
            receiptDetails.block?.let {
                mBinding.txtLot.text = it
                address += it
                address += ","
            }
            receiptDetails.doorNo?.let {
                mBinding.txtParcel.text = it
                address += it
                address += ","
            }
            receiptDetails.noticeReferanceNo?.let {
                mBinding.txtTaxNoticeNo.text = it
            }
            receiptDetails.taxInvoiceDate?.let {
                mBinding.txtTaxNoticeDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            receiptDetails.taxAmount?.let {
                mBinding.txtTaxAmount.text = formatWithPrecision(it)
            }
            receiptDetails.invoiceDue?.let {
                mBinding.txtTaxNoticeDueAmount.text = formatWithPrecision(it)
            }
            receiptDetails.waiveOffId?.let {
                mBinding.txtWaiveOffNumber.text = it.toString()
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.PENALTY_WAIVE_OFF, it.toString()))
            }
            receiptDetails.waiveOffDate?.let {
                mBinding.txtWaiveOffDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            receiptDetails.waiveOffAmount?.let {
                mBinding.txtWaiveOffAmount.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }
            receiptDetails?.remarks?.let {
                mBinding.txtWaiveOffRemarks.text = it
            }


            if (receiptDetails?.prodtypcode == "PR")
            {
                mBinding.txtSycoTaxIDLabel.text = String.format("%s", getString(R.string.receipt_property_id_sycotax))
                mBinding.txtBusinessNameLabel.text = String.format("%s%s", getString(R.string.property_name), getString(R.string.colon))
                mBinding.txtCitizenSycoTaxIDLabel.text = String.format("%s%s", getString(R.string.receipt_property_owner_id_sycotax), getString(R.string.colon))
                mBinding.txtBusinessOwnerLabel.text = String.format("%s%s", getString(R.string.property_owner), getString(R.string.colon))

                receiptDetails.businessOwners?.let {
                    if (it.contains(","))
                        it.replace(",", "\n")
                    mBinding.txtBusinessOwner.text = it
                }

                if (!address.isNullOrEmpty()) {
                    mBinding.txtAddress.text = address
                } else
                    mBinding.txtAddress.text = ""

                mBinding.txtAddressLabel.visibility = View.VISIBLE
                mBinding.txtStateLabel.visibility = View.VISIBLE
                mBinding.txtCityLabel.visibility = View.VISIBLE

//                receiptDetails.propertyOwnerIDSycoTax?.let {
//                    {
//                        mBinding.llCitizenSycoTax.visibility = View.VISIBLE
//                        mBinding.txtCitizenSycoTaxID.text = it
//                    }
//                }
//                receiptDetails.iDCardNumbers?.let {
//                    {
//                        mBinding.llCardNumber.visibility = View.VISIBLE
//                        mBinding.txtIDCardNumber.text = it
//                    }
//                }

                if (!receiptDetails.propertyOwnerIDSycoTax.isNullOrEmpty())
                {
                    mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                    mBinding.txtCitizenSycoTaxID.text = receiptDetails.propertyOwnerIDSycoTax
                }
                if (!receiptDetails.iDCardNumbers.isNullOrEmpty())
                {
                    mBinding.llCardNumber.visibility = View.VISIBLE
                    mBinding.txtIDCardNumber.text = receiptDetails.iDCardNumbers
                }
            }




        }
    }

    fun bindWaiveOffDetails(tableDetails: ArrayList<PenaltyWaiveOffDetailsTable>, receiptDetails: PenaltyWaiveOffReceiptChangeDetails?) {
        if (tableDetails.isNotEmpty()) {
            for (tableDetail in tableDetails) {
                val tableBinding: ItemWaiveOffDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(mBinding.root.context),
                        R.layout.item_waive_off_details, mBinding.llWaiveOffDetails, false)

                tableDetail.penaltyId?.let {
                    tableBinding.txtPenaltyNo.text = it.toString()
                }
                tableDetail.penaltyDate?.let {
                    tableBinding.txtPenaltyDate.text = formatDisplayDateTimeInMillisecond(it)
                }
                tableDetail.penaltyName?.let {
                    tableBinding.txtPenaltyName.text = it
                }
                tableDetail.taxDue?.let {
                    tableBinding.txtTaxDue.text = formatWithPrecision(it)
                }
                tableDetail.penaltyPercent?.let {
                    tableBinding.txtPenaltyPercentage.text = it.toString()
                }
                tableDetail.penaltyAmount?.let {
                    tableBinding.txtPenaltyAmount.text = formatWithPrecision(it)
                }
                tableDetail.penaltyDue?.let {
                    tableBinding.txtPenaltyDue.text = formatWithPrecision(it)
                }
                tableDetail.waivedOffAmount?.let {
                    tableBinding.txtWaivedOffAmount.text = formatWithPrecision(it)
                }
                tableDetail.penaltyDueAfterWaivedOff?.let {
                    tableBinding.txtPenaltyDueAfterWaiveOff.text = formatWithPrecision(it)
                }
                receiptDetails?.waiveOffBy?.let {
                    tableBinding.txtWaivedOffBy.text = it
                }


                mBinding.llWaiveOffDetails.addView(tableBinding.root)
            }
        }
    }
}