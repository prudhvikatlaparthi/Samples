package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.InitialOutstandingWaiveOffReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemInitialOutstandingPenaltyWaiveOffReceiptBinding
import com.sgs.citytax.model.OutstandingWaiveOffReceiptDetails
import com.sgs.citytax.util.*
import java.util.*

class InitialOutstandingPenaltyWaiveOfReceiptViewHolder(val mBinding: ItemInitialOutstandingPenaltyWaiveOffReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(taxDetailsResponse: InitialOutstandingWaiveOffReceiptResponse, iClickListener: IClickListener,fromScreen: Constant.QuickMenu?) {
        bindReceiptDetails(taxDetailsResponse.receiptDetails[0],fromScreen,taxDetailsResponse)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, taxDetailsResponse)
                }
            })
        }
    }

    fun bindReceiptDetails(
        receiptDetails: OutstandingWaiveOffReceiptDetails?,
        fromScreen: Constant.QuickMenu?,
        taxDetailsResponse: InitialOutstandingWaiveOffReceiptResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = taxDetailsResponse.orgData
        )
        mBinding.txtSectorLabel.text = String.format("%s%s", getString(R.string.sector), getString(R.string.colon))
        mBinding.txtCitizenSycoTaxIDLabel.text = String.format("%s%s", getString(R.string.receipt_property_owner_id_sycotax), getString(R.string.colon))
        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_OUTSTANDING_WAIVE_OFF||fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_OUTSTANDING_WAIVE_OFF){
            mBinding.txtBusinessOwnerLabel.text = mBinding.txtBusinessOwnerLabel.context.getString(R.string.receipt_property_owner)
            mBinding.txtBusinessNameLabel.text = mBinding.txtBusinessNameLabel.context.getString(R.string.receipt_property_name)
            mBinding.txtSycoTaxIDLabel.text = mBinding.txtSycoTaxIDLabel.context.getString(R.string.receipt_property_id_sycotax)
            mBinding.llPropertyAddress.visibility=View.VISIBLE

            if (!receiptDetails?.propertyOwnerIDSycoTax.isNullOrEmpty())
            {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = receiptDetails?.propertyOwnerIDSycoTax
            }
            if (!receiptDetails?.iDCardNumbers.isNullOrEmpty())
            {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = receiptDetails?.iDCardNumbers
            }
        }else{
            mBinding.txtBusinessOwnerLabel.text = mBinding.txtBusinessOwnerLabel.context.getString(R.string.receipt_business_owner)
            mBinding.txtBusinessNameLabel.text = mBinding.txtBusinessNameLabel.context.getString(R.string.receipt_business_name)
            mBinding.txtSycoTaxIDLabel.text = mBinding.txtSycoTaxIDLabel.context.getString(R.string.receipt_id_sycotax)
        }

        if (receiptDetails != null) {

            receiptDetails.printCounts?.let {
                if (it > 1) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
 //                   mBinding.txtPrintCounts.text = it.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
 //                   mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
                }
            }
            receiptDetails.prodTypeCode?.let {
                if (it.equals("I")) {
                    mBinding.llBusinessOwner.visibility = View.VISIBLE
                    receiptDetails.businessName?.let {
                        mBinding.txtBusinessOwner.text = it
                    }
                } else {
                    mBinding.llBusinessName.visibility = View.VISIBLE
                    mBinding.llBusinessOwner.visibility = View.VISIBLE
                    receiptDetails.businessName?.let {
                        mBinding.txtBusinessName.text = it
                    }

                    receiptDetails.businessOwner?.let {
                        if (it.contains(";"))
                            it.replace(";", "\n")
                        mBinding.txtBusinessOwner.text = it
                    }
                }
            }

            receiptDetails.sycoTaxId?.let {
                mBinding.txtSycoTaxID.text = it
            }
            var address: String? = ""

            receiptDetails.city?.let{
                mBinding.txtCity.text = it
                address += it
                address += ","
            }
            receiptDetails.state?.let{
                mBinding.txtState.text = it
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
            address?.let {
                mBinding.txtAddress.text = it
            }
            receiptDetails.outstandingType?.let {
                mBinding.txtOutStandingType.text = it
            }
            receiptDetails.initialOutStandingId?.let {
                mBinding.txtOutstandingNumber.text = it.toString()
            }
            receiptDetails.year?.let {
                mBinding.txtOutstandingYear.text = it.toString()
            }
            receiptDetails.outstandingAmount?.let {
                mBinding.txtOutstandingAmount.text = formatWithPrecision(it)
            }
            receiptDetails.outstandingDueAmount?.let {
                mBinding.txtOutstandingDueAmount.text = formatWithPrecision(it)
            }
            receiptDetails.outstandingWaiveOffId?.let {
                mBinding.txtOutstandingWaiveOffNumber.text = it.toString()
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.INITIAL_OUTSTANDING, it.toString()))
            }
            receiptDetails.outstandingWaiveOffDate?.let {
                mBinding.txtOutstandingWaiveOffDate.text = formatDisplayDateTimeInMillisecond(it)
            }
            receiptDetails.waiveOffAmount?.let {
                mBinding.txtOutstandingWaiveOffAmount.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }

            receiptDetails.dueAfterWaveOff?.let {
                mBinding.txtDueAfterWaveOff.text = formatWithPrecision(it)
            }

            receiptDetails.remarks?.let {
                mBinding.txtRemarks.text = it
            }
            receiptDetails.waivedOffBy?.let {
                mBinding.txtWaivedOffBy.text = it
            }
        }


    }
}