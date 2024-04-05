package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ImpoundmentReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemImpoundmentReceiptBinding
import com.sgs.citytax.model.ImpoundmentReceiptTable
import com.sgs.citytax.util.*
import java.util.*

class ImpoundmentReceiptViewHolder(val mBinding: ItemImpoundmentReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(response: ImpoundmentReceiptResponse, iClickListener: IClickListener) {

        bindReceiptDetails(response.receiptTable[0],response)

        if (iClickListener != null) {
            mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, response)
                }
            })
        }
    }

    private fun bindReceiptDetails(
        receiptDetails: ImpoundmentReceiptTable?,
        response: ImpoundmentReceiptResponse
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

        val addressLabel = mBinding.titleAddressLabel.context.getString(R.string.title_address_new)
        mBinding.titleAddressLabel.text = String.format("%s%s", addressLabel, getString(R.string.colon))

        val sector = mBinding.txtSectorLabel.context.getString(R.string.sector)
        mBinding.txtSectorLabel.text = String.format("%s%s", sector, getString(R.string.colon))

        val state = mBinding.titleStateLabel.context.getString(R.string.state)
        mBinding.titleStateLabel.text = String.format("%s%s", state, getString(R.string.colon))

        val city = mBinding.titleCityLabel.context.getString(R.string.city)
        mBinding.titleCityLabel.text = String.format("%s%s", city, getString(R.string.colon))

        val remarks = mBinding.txtRemarksLabel.context.getString(R.string.remarks)
        mBinding.txtRemarksLabel.text = String.format("%s%s", remarks, getString(R.string.colon))

        val yard = mBinding.yardLabel.context.getString(R.string.txt_yard)
        mBinding.yardLabel.text = String.format("%s%s", yard, getString(R.string.colon))

        val towingCraneType = mBinding.VehicletowingCraneTypesLabel.context.getString(R.string.txt_crane_types)
        mBinding.VehicletowingCraneTypesLabel.text = String.format("%s%s",towingCraneType, getString(R.string.colon))
        mBinding.AnimaltowingCraneTypesLabel.text = String.format("%s%s",towingCraneType, getString(R.string.colon))

        val towingTripCount = mBinding.VehicletowingTripCountLabel.context.getString(R.string.towing_trip_count)
        mBinding.VehicletowingTripCountLabel.text = String.format("%s%s",towingTripCount, getString(R.string.colon))
        mBinding.AnimaltowingTripCountLabel.text = String.format("%s%s",towingTripCount, getString(R.string.colon))

        val towingCharge = mBinding.VehicletowingChargeLabel.context.getString(R.string.towing_charge)
        mBinding.VehicletowingChargeLabel.text = String.format("%s%s",towingCharge, getString(R.string.colon))
        mBinding.AnimaltowingChargeLabel.text = String.format("%s%s",towingCharge, getString(R.string.colon))

        val extraCharge = mBinding.vehicleExtraChargesLabel.context.getString(R.string.extra_charges)
        mBinding.vehicleExtraChargesLabel.text = String.format("%s%s",extraCharge, getString(R.string.colon))
        mBinding.animalExtraChargesLabel.text = String.format("%s%s",extraCharge, getString(R.string.colon))

        val totalAmount = mBinding.vehicleTotalAmountLabel.context.getString(R.string.total_amount)
        mBinding.vehicleTotalAmountLabel.text = String.format("%s%s",totalAmount, getString(R.string.colon))

        mBinding.txtPoweredBy.text = MyApplication.getPrefHelper().copyrightReport

        if (receiptDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            receiptDetails.taxInvoiceId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.TAX_NOTICE, it.toString(), receiptDetails.vehicleSycoTaxId
                        ?: "", receiptDetails.impoundmentNumber))
            }

            if (receiptDetails.printCount != null && receiptDetails.printCount!! > 0) {
                mBinding.llDuplicatePrints.visibility = View.VISIBLE
//                mBinding.txtPrintCounts.text = receiptDetails.printCount.toString()//TODO on requirement of Onsite Team, hiding duplicate print counts
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

            mBinding.llVehicleNo.isVisible = receiptDetails.vehicleNumber?.let {
                mBinding.txtVehicleNo.text = it
                true
            }?:false

          mBinding.llBusinessSycoTaxID.isVisible =  receiptDetails.vehicleSycoTaxId?.let {
                mBinding.txtVehicleSycoTaxID.text = it
              true
            }?:false

            receiptDetails.vehicleOwner?.let {
                mBinding.txtVehicleOwner.text = it
            }

            /* //region Vehicle Owner sycotax and id card number
             receiptDetails.vehicleOwnerCitizenSycotaxID?.let {
                 mBinding.txtVehicleOwnerSycoTaxID.text = it
             }

             receiptDetails.vehicleOwnerCitizenCardNo?.let {
                 mBinding.txtVehicleOwnerIDCardNumber.text = it
             }
             //endregion*/

            mBinding.llDriver.isVisible = receiptDetails.driver?.let {
                mBinding.txtDriver.text = it
                true
            }?:false

            mBinding.llDrivingLicenseNumber.isVisible =  receiptDetails.drivingLicenseNumber?.let {
                mBinding.txtDrivingLicenseNumber.text = it
                true
            }?:false

            receiptDetails.impoundmentFrom?.let {
                mBinding.txtImpoundFrom.text = it
            }
            if (receiptDetails.violatorTypeCode != Constant.ViolationTypeCode.ANIMAL.code) {
                receiptDetails.goodsOwnerSycoTaxID?.let {
                    mBinding.llBusinessSycoTax.visibility = View.VISIBLE
                    mBinding.txtSycoTaxID.text = it
                }
            }
//            receiptDetails.impoundFromCitizenSycotaxID?.let {
//                mBinding.txtCitizenSycoTaxID.text = it
//                mBinding.llCitizenSycoTax.visibility = View.VISIBLE    //citizen id remove for latest receipt changes
//            }

            receiptDetails.impoundFromCitizenCardNo?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
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
                mBinding.llyard.visibility = View.VISIBLE
                mBinding.txtYard.text = it
            }

            receiptDetails.towingCraneType?.let {
                mBinding.llVehicleTowingCraneType.visibility = View.VISIBLE
                mBinding.txtVehicleTowingCraneType.text = it
            }

            receiptDetails.towingTripCount?.let {
                mBinding.llVehicletowingTripCount.visibility = View.VISIBLE
                mBinding.txtVehicleTowingTripCount.text = it.toDouble().toString()
            }

            receiptDetails.towingCharge?.let {
                mBinding.llVehicleTowingCharge.visibility = View.VISIBLE
                mBinding.txtVehicleTowingCharge.text = formatWithPrecision(it)
            }

            receiptDetails.extraCharge?.let {
                mBinding.llVehicleExtraCharges.visibility = View.VISIBLE
                mBinding.txtVehicleExtraCharge.text = formatWithPrecision(it)
            }

            receiptDetails.totalAmound?.let {
                mBinding.llVehicleTotalAmount.visibility = View.VISIBLE
                mBinding.txtVehicleTotalAmount.text = formatWithPrecision(it)
            }

            receiptDetails.totalAmound?.let{
                getAmountInWordsWithCurrency(it,mBinding.txtAmountInWords)
            }

            receiptDetails.impoundmentType?.let {
                mBinding.txtImpoundType.text = it
            }

            receiptDetails.impoundmentSubType?.let {
                mBinding.txtImpoundmentSubType.text = it
            }

            //region Address
            var address: String? = ""

            if (!receiptDetails.state.isNullOrEmpty()) {
                mBinding.txtState.text = receiptDetails.state
                address += receiptDetails.state
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }

            if (!receiptDetails.city.isNullOrEmpty()) {
                mBinding.txtCity.text = receiptDetails.city
                address += receiptDetails.city
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }

            //region Zone
            if (!receiptDetails.zone.isNullOrEmpty()) {
                mBinding.txtArdt.text = receiptDetails.zone
                address += receiptDetails.zone
                address += ","
            } else {
                mBinding.txtArdt.text = ""
                address += ""
            }
            //endregion

            //region Sector
            if (!receiptDetails.sector.isNullOrEmpty()) {
                mBinding.txtSector.text = receiptDetails.sector
                address += receiptDetails.sector
                address += ","
            } else {
                mBinding.txtSector.text = ""
                address += ""
            }
            //endregion

            //region plot
            if (!receiptDetails.plot.isNullOrEmpty()) {
                mBinding.txtSection.text = receiptDetails.plot
                address += receiptDetails.plot
                address += ","
            } else {
                mBinding.txtSection.text = ""
                address += ""
            }
            //endregion

            //region block
            if (!receiptDetails.block.isNullOrEmpty()) {
                address += receiptDetails.block
                mBinding.txtLot.text = receiptDetails.block
                address += ","
            } else {
                mBinding.txtLot.text = ""
                address += ""
            }
            //endregion

            //region door no
            if (!receiptDetails.doorNo.isNullOrEmpty()) {
                mBinding.txtParcel.text = receiptDetails.doorNo
                address += receiptDetails.doorNo
            } else {
                mBinding.txtParcel.text = ""
                address += ""
            }
            //endregion


            address?.let {
                mBinding.txtAddress.text = it
            }

            //endregion

            receiptDetails.lat?.let {
                mBinding.txtLatitude.text = it
            }

            receiptDetails.longitude?.let {
                mBinding.txtLongitude.text = it
            }

            receiptDetails.impoundedBy?.let {
                mBinding.txtImpoundBy.text = it
            }
            var totalCharge = 0.0

            receiptDetails.impoundmentCharge?.let {
                mBinding.txtImpoundCharge.text = formatWithPrecision(it.toString())
                totalCharge = it.toDouble()
            }

            receiptDetails.fineAmount?.let {
                mBinding.txtFineAmount.text = formatWithPrecision(it)
                totalCharge += it
            }

            receiptDetails.remarks?.let {
                mBinding.txtRemarks.text = it
            }

            receiptDetails.badgeNumber?.let {
                mBinding.txtBadgeNumber.text = it
            }

            receiptDetails.awsPath?.let {
                mBinding.imgSignature.visibility = View.VISIBLE
                Glide.with(mBinding.imgSignature.context).load(it).into(mBinding.imgSignature)
            }

            receiptDetails.note?.let {
                mBinding.txtTaxNoticeNote.text = it
            }
            if (receiptDetails.violatorTypeCode == Constant.ViolationTypeCode.ANIMAL.code) {

                mBinding.receiptHeader.text = getString(R.string.receipt_animal_impound)
                mBinding.llVehicle.visibility = View.GONE
                mBinding.llVehicleTotalAmount.visibility = View.GONE
                mBinding.llVehicleTowingCraneType.visibility = View.GONE
                mBinding.llVehicletowingTripCount.visibility = View.GONE
                mBinding.llVehicleTowingCharge.visibility = View.GONE
                mBinding.llVehicleExtraCharges.visibility = View.GONE
                mBinding.llAnimalTowing.visibility = View.VISIBLE
                mBinding.llfooter.visibility = View.GONE

                receiptDetails.towingCraneType?.let {
                    mBinding.llAnimalTowingCraneType.visibility = View.VISIBLE
                    mBinding.txtAnimalTowingCraneType.text = it
                }

                receiptDetails.towingTripCount?.let {
                    mBinding.llAnimaltowingTripCount.visibility = View.VISIBLE
                    mBinding.txtAnimalTowingTripCount.text = it.toDouble().toString()
                }

                receiptDetails.towingCharge?.let {
                    mBinding.llAnimalTowingCharge.visibility = View.VISIBLE
                    mBinding.txtAnimalTowingCharge.text = formatWithPrecision(it)
                    totalCharge += it.toDouble()
                }

                receiptDetails.extraCharge?.let {
                    mBinding.llAnimalExtraCharges.visibility = View.VISIBLE
                    mBinding.txtAnimalExtraCharge.text = formatWithPrecision(it)
                    totalCharge += it.toDouble()
                }

                receiptDetails.impoundFromAccount?.let {
                    mBinding.txtImpoundFrom.text = it
                }
                receiptDetails.impoundFromSycotaxID?.let {
                    mBinding.llBusinessSycoTax.visibility = View.VISIBLE
                    mBinding.txtSycoTaxID.text = it
                }
//                receiptDetails.impoundFromCitizenSycotaxID1?.let {
//                    mBinding.txtCitizenSycoTaxID.text = it
//                    mBinding.llCitizenSycoTax.visibility = View.VISIBLE  // hide citizen id for latest receipt changes
//                }

                receiptDetails.impoundFromCitizenCardNo1?.let {
                    mBinding.llCardNumber.visibility = View.VISIBLE
                    mBinding.txtIDCardNumber.text = it
                }


                receiptDetails.goodsOwner?.let {
                    mBinding.llAnimal.visibility = View.VISIBLE
                    mBinding.txtAnimalOwner.text = it
                }
                receiptDetails.violationType?.let {
                    mBinding.llViolationType.visibility = View.VISIBLE
                    mBinding.txtViolationType.text = it
                }
                receiptDetails.violationClass?.let {
                    mBinding.llViolationClass.visibility = View.VISIBLE
                    mBinding.txtViolationClass.text = it
                }

                val impoundReason = mBinding.impoundReasonLabel.context.getString(R.string.impoundment_reason_imp)
                mBinding.impoundReasonLabel.text = String.format("%s%s",impoundReason, getString(R.string.colon))

                receiptDetails.impoundmentReason?.let {
                    mBinding.llImpoundReson.visibility = View.VISIBLE
                    mBinding.txtImpoundmentReason.text = it
                }
                receiptDetails.impoundQuantity?.let {
                    mBinding.llImpoundAnimalQty.visibility = View.VISIBLE
                    mBinding.txtImpoundAnimalqty.text = getQuantity(it)
                }
                receiptDetails.impoudmentTarif?.let {
                    mBinding.llImpoundAnimalTariff.visibility = View.VISIBLE
                    mBinding.txtAnimalImpondtariff.text = getTariffWithCurrency(it)

                }
                receiptDetails.violationType?.let {
                    mBinding.llViolationType.visibility = View.VISIBLE
                    mBinding.txtViolationType.text = it
                }
                receiptDetails.violationClass?.let {
                    mBinding.llViolationClass.visibility = View.VISIBLE
                    mBinding.txtViolationClass.text = it
                }

                val violationCharge = mBinding.violationChargeLabel.context.getString(R.string.violation_charge)
                mBinding.violationChargeLabel.text = String.format("%s%s",violationCharge, getString(R.string.colon))

                receiptDetails.violationCharge?.let {
                    mBinding.llViolationCharge.visibility = View.VISIBLE
                    mBinding.txtViolationCharge.text = formatWithPrecision(it)
                    totalCharge += it.toDouble()
                }
                totalCharge.let {
                    mBinding.llTotalCharge.visibility = View.VISIBLE
                    mBinding.txtTotalCharge.text = formatWithPrecision(it)
                }
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
                getNoticePrintFlag(receiptDetails.taxInvoiceId ?:0, mBinding.btnPrint)
            }
        }

    }
}