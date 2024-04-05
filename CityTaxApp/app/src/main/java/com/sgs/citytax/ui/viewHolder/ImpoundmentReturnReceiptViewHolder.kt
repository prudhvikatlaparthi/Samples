package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ImpoundmentReturnReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemImpoundmentReturnReceiptBinding
import com.sgs.citytax.model.ImpoundmentLineSummary
import com.sgs.citytax.model.ImpoundmentReturnReceiptTable
import com.sgs.citytax.util.*
import java.util.*


class ImpoundmentReturnReceiptViewHolder(val mBinding: ItemImpoundmentReturnReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(response: ImpoundmentReturnReceiptResponse, iClickListener: IClickListener) {

        if (response.impoundmentLineSummary.size > 0)
            bindReceiptDetails(response.receiptTable[0], response.impoundmentLineSummary.get(0),response)
        else
            bindReceiptDetails(response.receiptTable[0], response = response)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, response)
                }
            })
        }
    }

    private fun bindReceiptDetails(
        receiptDetails: ImpoundmentReturnReceiptTable?,
        impoundmentLineSummary: ImpoundmentLineSummary? = null,
        response: ImpoundmentReturnReceiptResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = response.orgData
        )
        val owner = mBinding.txtVehicleOwnerLabel.context.getString(R.string.vehicle_owner)
        mBinding.txtVehicleOwnerLabel.text = String.format("%s%s", owner, getString(R.string.colon))

        val driver = mBinding.txtDriverLabel.context.getString(R.string.driver_imp)
        mBinding.txtDriverLabel.text = String.format("%s%s", driver, getString(R.string.colon))

        val licenseNo = mBinding.txtDrivingLicenseNumberLabel.context.getString(R.string.driving_license_number)
        mBinding.txtDrivingLicenseNumberLabel.text = String.format("%s%s", licenseNo, getString(R.string.colon))

        val emailLabel = mBinding.txtEmailIDLabel.context.getString(R.string.email_id)
        mBinding.txtEmailIDLabel.text = String.format("%s%s", emailLabel, getString(R.string.colon))

        val remarks = mBinding.txtRemarksLabel.context.getString(R.string.remarks)
        mBinding.txtRemarksLabel.text = String.format("%s%s", remarks, getString(R.string.colon))

        val yard = mBinding.txtYardLable.context.getString(R.string.txt_yard)
        mBinding.txtYardLable.text = String.format("%s%s", yard, getString(R.string.colon))

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        if (receiptDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            receiptDetails.taxInvoiceId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.IMPOUND_RETURN, it.toString(),impoundmentLineSummary?.returnLineID.toString()))
            }

            if (receiptDetails.printCount != null && receiptDetails.printCount!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
 //               mBinding.txtPrintCounts.text = receiptDetails.printCount.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
            } else {
                mBinding.llDuplicatePrints.visibility = View.GONE
 //               mBinding.txtPrintCounts.text = ""//TODO on requirement of Onsite Team, hiding duplicate print counts
            }

            receiptDetails.impoundmentNumber?.let {
                mBinding.txtImpoundNumber.text = it.toString()
            }

            receiptDetails.impoundmentDate?.let {
                mBinding.txtImpoundDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            receiptDetails.vehicleNumber?.let {
                mBinding.txtVehicleNo.text = it
            }

            receiptDetails.extraCharge?.let {
                mBinding.llExtraCharges.visibility = View.VISIBLE
                mBinding.txtExtraCharges.text = formatWithPrecision(it)
            }

            receiptDetails.yard?.let {
                mBinding.llYard.visibility = View.VISIBLE
                mBinding.txtYard.text = "$it"
            }


            receiptDetails.totalAmound?.let {
                mBinding.llTotalAmount.visibility = View.VISIBLE
                mBinding.txtTotalAmount.text = formatWithPrecision(it)
                getAmountInWordsWithCurrency(it, mBinding.txtAmountInWords)
            }

            receiptDetails.towingCharge?.let {
                mBinding.llTowingCharge.visibility = View.VISIBLE
                mBinding.txtTowingCharge.text = formatWithPrecision(it)
            }

            receiptDetails.vehicleSycoTaxId?.let {
                mBinding.txtVehicleSycoTaxID.text = it
            }
            receiptDetails.impoundFromCitizenSycoTaxID?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }

            receiptDetails.impoundFromCitizenCardNo?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }
            receiptDetails.vehicleOwner?.let {
                mBinding.txtVehicleOwner.text = it
            }

            receiptDetails.driver?.let {
                mBinding.txtDriver.text = it
            }
            receiptDetails.drivingLicenseNumber?.let {
                mBinding.txtDrivingLicenseNumber.text = it
            }

            receiptDetails.goodsOwnerSycoTaxID?.let {
                mBinding.txtSycoTaxID.text = it
            }

            receiptDetails.mobile?.let {
                mBinding.txtPhoneNumber.text = it
            }

            receiptDetails.email?.let {
                mBinding.txtEmail.text = it
            }

            receiptDetails.policeStation?.let {
                mBinding.txtPoliceStation.text = it
            }

            receiptDetails.yard?.let {
                mBinding.txtYard.text = it
            }

            receiptDetails.impoundmentType?.let {
                mBinding.txtImpoundType.text = it
            }

            receiptDetails.impoundmentSubType?.let {
                mBinding.txtImpoundmentSubType.text = it
            }


            receiptDetails.impoundedBy?.let {
                mBinding.txtImpoundBy.text = it
            }


            receiptDetails.badgeNumber?.let {
                mBinding.txtBadgeNumber.text = it
            }

            receiptDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = it
            }


            receiptDetails.handoverImageAWSPath?.let {

                mBinding.imgHandover.visibility = View.VISIBLE
                Glide.with(mBinding.imgHandover.context).load(it).into(mBinding.imgHandover)

            }
            receiptDetails.ownerSignatureAWSPath?.let {

                mBinding.ownerSignature.visibility = View.VISIBLE
                Glide.with(mBinding.ownerSignature.context).load(it).into(mBinding.ownerSignature)
            }

            receiptDetails.customerSignatureAWSPath?.let {

                mBinding.customerSignature.visibility = View.VISIBLE
                Glide.with(mBinding.customerSignature.context).load(it).into(mBinding.customerSignature)
            }

            if (receiptDetails.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code) {
                mBinding.llVehicleImpond.visibility = View.GONE
                mBinding.llBusinessSycoTax.visibility = View.GONE
                mBinding.llTowingCharge.visibility = View.VISIBLE
                mBinding.llExtraCharges.visibility = View.VISIBLE
                mBinding.llTotalAmount.visibility = View.VISIBLE
                mBinding.llAmountToText.visibility = View.VISIBLE
                receiptDetails.goodsOwner?.let {
                    mBinding.llAnimalImpond.visibility = View.VISIBLE
                    mBinding.txtAnimalOwner.text = it
                }

                receiptDetails.impoundFromSycotaxID?.let {
                    mBinding.llBusinessSycoTax.visibility = View.VISIBLE
                    mBinding.txtSycoTaxID.text = it
                }

                receiptDetails.impoundFromCitizenSycoTaxID?.let {
                    mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                    mBinding.txtCitizenSycoTaxID.text = it
                }
                receiptDetails.impoundFromCitizenCardNo?.let {
                    mBinding.llCardNumber.visibility = View.VISIBLE
                    mBinding.txtIDCardNumber.text = it
                }

                receiptDetails.impoundmentReason?.let {
                    mBinding.llImpoundReson.visibility = View.VISIBLE
                    mBinding.txtImpoundmentReason.text = it
                }

                receiptDetails.impoundFromAccountName?.let {
                    mBinding.txtImpoundFrom.text = it
                }

                impoundmentLineSummary?.quantity?.let {
                    mBinding.llAnimalReturnQty.visibility = View.VISIBLE
                    mBinding.txtImpoundmentReturnQty.text = getQuantity(it.toString())
                }

                impoundmentLineSummary?.impoundmentCharge?.let {
                    mBinding.llAnimalImpondCharge.visibility = View.VISIBLE
                    mBinding.txtImpoundmentCharge.text = formatWithPrecision(it)
                }
                impoundmentLineSummary?.violationCharge?.let {
                    mBinding.llAnimalViolation.visibility = View.VISIBLE
                    mBinding.txtViolationCharge.text = formatWithPrecision(it)
                }
                impoundmentLineSummary?.fineAmount?.let {
                    mBinding.llAnimalFine.visibility = View.VISIBLE
                    mBinding.txtImpoundmentFine.text = formatWithPrecision(it)
                }
                impoundmentLineSummary?.handoverImageAWSPath?.let {

                    mBinding.imgHandover.visibility = View.VISIBLE
                    Glide.with(mBinding.imgHandover.context).load(it).into(mBinding.imgHandover)

                }
                impoundmentLineSummary?.ownerSignatureAWSPath?.let {

                    mBinding.ownerSignature.visibility = View.VISIBLE
                    Glide.with(mBinding.ownerSignature.context).load(it).into(mBinding.ownerSignature)
                }

                impoundmentLineSummary?.customerSignatureAWSPath?.let {

                    mBinding.customerSignature.visibility = View.VISIBLE
                    Glide.with(mBinding.customerSignature.context).load(it).into(mBinding.customerSignature)
                }

                impoundmentLineSummary?.impoundmentReturnDate?.let {
                    mBinding.txtImpoundReturnDateTime.text = formatDisplayDateTimeInMillisecond(it)
                }

                impoundmentLineSummary?.returnRemarks?.let {
                    mBinding.txtRemarks.text = it
                }

            }
            else
            {
                mBinding.llTowingCharge.visibility = View.GONE
                mBinding.llExtraCharges.visibility = View.GONE
                mBinding.llTotalAmount.visibility  = View.GONE
                mBinding.llAmountToText.visibility  = View.GONE
                receiptDetails.impoundmentFrom?.let {
                    mBinding.txtImpoundFrom.text = it
                }
                receiptDetails.citizenSycoTaxId?.let {
                    mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                    mBinding.txtCitizenSycoTaxID.text = it
                }
                receiptDetails.citizenCardNumber?.let {
                    mBinding.llCardNumber.visibility = View.VISIBLE
                    mBinding.txtIDCardNumber.text = it
                }
                receiptDetails.goodsOwnerSycoTaxID?.let {
                    mBinding.llBusinessSycoTax.visibility = View.VISIBLE
                    mBinding.txtSycoTaxID.text =it
                }
                receiptDetails.handoverImageAWSPath?.let {

                    mBinding.imgHandover.visibility = View.VISIBLE
                    Glide.with(mBinding.imgHandover.context).load(it).into(mBinding.imgHandover)

                }
                receiptDetails.ownerSignatureAWSPath?.let {

                    mBinding.ownerSignature.visibility = View.VISIBLE
                    Glide.with(mBinding.ownerSignature.context).load(it).into(mBinding.ownerSignature)
                }

                receiptDetails.customerSignatureAWSPath?.let {

                    mBinding.customerSignature.visibility = View.VISIBLE
                    Glide.with(mBinding.customerSignature.context).load(it).into(mBinding.customerSignature)
                }
                receiptDetails.impoundmentReturnDate?.let {
                    mBinding.txtImpoundReturnDateTime.text = formatDisplayDateTimeInMillisecond(it)
                }
                receiptDetails.returnRemarks?.let {
                    mBinding.txtRemarks.text = it
                }
            }

            // As per UAT issue I have commented the below code ~ prudhvi 21 mar 2022
//            if (MyApplication.getPrefHelper().isFromHistory == false ) {
//                getReceiptPrintFlag(receiptDetails.taxInvoiceId!!, mBinding.btnPrint)
//            }
        }

    }
}