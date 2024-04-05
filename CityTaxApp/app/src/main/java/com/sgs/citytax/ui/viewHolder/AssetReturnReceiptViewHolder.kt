package com.sgs.citytax.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.response.AssetRentAndReturnReceiptResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ItemAssetReturnReceiptBinding
import com.sgs.citytax.model.AssetRentAndReturnReceiptDetails
import com.sgs.citytax.util.*
import java.util.*

class AssetReturnReceiptViewHolder(val mBinding: ItemAssetReturnReceiptBinding) : RecyclerView.ViewHolder(mBinding.root) {

    fun bind(receiptResponse: AssetRentAndReturnReceiptResponse, iClickListener: IClickListener,isMoving:Boolean) {

        bindReceiptDetails(receiptResponse.receiptDetails[0],isMoving,receiptResponse)

        mBinding.btnPrint.setOnClickListener (object: OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    iClickListener.onClick(v, adapterPosition, receiptResponse)
                }
            })
    }

    private fun bindReceiptDetails(
        receiptDetails: AssetRentAndReturnReceiptDetails?,
        isMoving: Boolean,
        receiptResponse: AssetRentAndReturnReceiptResponse
    ) {
        CommonLogicUtils.checkNUpdateQRCodeNotes(
            qrCodeWrapper = mBinding.qrCodeWrapper,
            orgDataList = receiptResponse.orgData
        )
        if (receiptDetails != null) {
            val addressLabel = mBinding.titleAddressLabel.context.getString(R.string.title_address)
            mBinding.titleAddressLabel.text = String.format("%s%s", addressLabel, getString(R.string.colon))

            val sector = mBinding.txtSectorLabel.context.getString(R.string.sector)
            mBinding.txtSectorLabel.text = String.format("%s%s", sector, getString(R.string.colon))

            val state = mBinding.titleStateLabel.context.getString(R.string.state)
            mBinding.titleStateLabel.text = String.format("%s%s", state, getString(R.string.colon))

            val city = mBinding.titleCityLabel.context.getString(R.string.city)
            mBinding.titleCityLabel.text = String.format("%s%s", city, getString(R.string.colon))

            val email = mBinding.txtEmailLabel.context.getString(R.string.email)
            mBinding.txtEmailLabel.text = String.format("%s%s", getString(R.string.email), getString(R.string.colon))

            val remarks = mBinding.txtRemarksLabel.context.getString(R.string.remarks)
            mBinding.txtRemarksLabel.text = String.format("%s%s", getString(R.string.remarks), getString(R.string.colon))

            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())
            receiptDetails.printCounts?.let {
                if (it > 0) {
                    mBinding.llDuplicatePrints.visibility = View.VISIBLE
                } else {
                    mBinding.llDuplicatePrints.visibility = View.GONE
                }
            }

            receiptDetails.assetRentId?.let {
                mBinding.txtReturnNumber.text = it.toString()
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.ASSET_RETURN,it.toString(),receiptDetails.assetSycoTaxId))
            }

            receiptDetails.receivedDate?.let {
                mBinding.txtAssetReturnDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            receiptDetails.bookingRequestId?.let {
                mBinding.txtBookingrequestNumber.text = it.toString()
            }

            receiptDetails.assetNo?.let {
                mBinding.txtAssetName.text = it
            }

            receiptDetails.assetSycoTaxId?.let {
                mBinding.txtSycoTaxID.text = it
            }
            if (isMoving){
                mBinding.llOdometerEndDateTime.visibility = View.VISIBLE
                mBinding.llOdometerEndReading.visibility = View.VISIBLE

                receiptDetails.odometerEndDate?.let {
                    mBinding.txtOdometerEndDateTime.text = formatDisplayDateTimeInMillisecond(it)
                }
                receiptDetails.odometerEnd?.let {
                    mBinding.txtOdometerendReading.text = it.toString()
                }
            }else{
                mBinding.llOdometerEndDateTime.visibility = View.GONE
                mBinding.llOdometerEndReading.visibility = View.GONE
            }

            receiptDetails.taxationYear?.let {
                mBinding.txtTaxationYear.text = it.toString()
            }
            receiptDetails.sycoTaxID?.let {
                mBinding.txtBusinessSycoTaxID.text = it
            }
            receiptDetails.assignAssetTo?.let {
                mBinding.txtAssetreturnTo.text = it
            }
            receiptDetails.citizenSycoTaxId?.let {
                mBinding.llCitizenSycoTax.visibility = View.VISIBLE
                mBinding.txtCitizenSycoTaxID.text = it
            }
            receiptDetails.citizenCardNumber?.let {
                mBinding.llCardNumber.visibility = View.VISIBLE
                mBinding.txtIDCardNumber.text = it
            }

            receiptDetails.phoneNumber?.let {
                mBinding.txtPhoneNumber.text = it
            }

            receiptDetails.email?.let {
                mBinding.txtEmail.text = it
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
            receiptDetails.fineAmount?.let {
                mBinding.txtFineAmount.text = formatWithPrecision(it)
            }

            receiptDetails.remarks?.let {
                mBinding.txtRemarks.text = it
            }

            receiptDetails.receivedBy?.let {
                mBinding.txtReturnedBy.text = it
            }
            if (MyApplication.getPrefHelper().isFromHistory == false ) {
//                getReceiptPrintFlag(receiptDetails.assetRentId!!, mBinding.btnPrint)
            }
        }
    }
}