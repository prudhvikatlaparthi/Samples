package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.AdvanceSearchFilter
import com.sgs.citytax.api.payload.FilterColumn
import com.sgs.citytax.api.payload.TableDetails
import com.sgs.citytax.api.response.GenericServiceResponse
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentPropertyVeriicationReceiptBinding
import com.sgs.citytax.model.PendingRequestList
import com.sgs.citytax.model.VUCOMPropertyVerificationRequests
import com.sgs.citytax.util.*
import java.util.*

class PropertyVerificationReceiptFragment : BaseFragment() {
    private lateinit var mBinding: FragmentPropertyVeriicationReceiptBinding
    private var mListener: Listener? = null
    private var pendingList: PendingRequestList? = null
    private var printHelper = PrintHelper()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_veriication_receipt, container, false)
        //mListener?.hideToolbar()
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        arguments?.let {
            if (it.containsKey(Constant.KEY_PENDING_PROPERTY_LIST))
                pendingList = it.getParcelable(Constant.KEY_PENDING_PROPERTY_LIST)
        }
        getReceiptData()
        setListeners()
    }

    private fun getReceiptData() {
        mListener?.showProgressDialog()
        val searchFilter = AdvanceSearchFilter()
        val filterList: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "PropertyVerificationRequestID"
        filterColumn.columnValue = "${pendingList?.propertyVerificationRequestId}"
        filterColumn.srchType = "equal"

        filterList.add(filterColumn)
        searchFilter.filterColumns = filterList

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_COM_PropertyVerificationRequests"
        tableDetails.primaryKeyColumnName = "PropertyVerificationRequestID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "AND"

        searchFilter.tableDetails = tableDetails

        APICall.getTableOrViewData(searchFilter, object : ConnectionCallBack<GenericServiceResponse> {
            override fun onSuccess(response: GenericServiceResponse) {
                if (response.result?.propertyVerifications?.isNotEmpty() == true) {
                    bindReceiptData(response.result!!.propertyVerifications!![0])
                }
                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun bindReceiptData(receiptDetails: VUCOMPropertyVerificationRequests?) {
        if (receiptDetails != null) {
            mBinding.txtDateOfPrint.text = formatDisplayDateTimeInMillisecond(Date())

            if (pendingList?.taxRuleBookCode == Constant.TaxRuleBook.LAND_PROP.Code) {
                mBinding.tvHeader.text = getString(R.string.receipt_land_verification_approval)
                mBinding.tvPropertyType.text = getString(R.string.receipt_land_type)
                mBinding.tvPropertyPermission.text = getString(R.string.land_permission_verification_type)
                mBinding.tvPropertyVerificationDate.text = getString(R.string.land_verification_request_date)
                mBinding.tvPropertyVerificationID.text = getString(R.string.land_verification_request_id)
                mBinding.tvPropertyname.text = getString(R.string.receipt_land_name)
                mBinding.tvLandOwner.text = getString(R.string.receipt_land_owner)
                mBinding.tvLandSycotaxID.text = getString(R.string.receipt_land_id_sycotax)
                mBinding.tvDetailHeader.text = getString(R.string.land_verification_details)
                mBinding.tvVerificationApprovalDate.text = getString(R.string.land_verification_approval_date)
                mBinding.tvPropertyPermissionValidity.text = getString(R.string.land_permission_validity_period)

            }

            receiptDetails.propertyType?.let {
                mBinding.txtPropertyType.text = it
            }
            receiptDetails.planningPermissionRequestId?.let {
                mBinding.txtPermissionRequestID.text = it.toString()
            }
            receiptDetails.verificationType?.let {
                mBinding.txtPermissionType.text = it
            }

            receiptDetails.propertyVerificationRequestId?.let {
                mBinding.imgQRCode.setImageBitmap(bindQRCode(Constant.ReceiptType.PROPERTY_VERIFICATION, it.toString()))
                CommonLogicUtils.checkNUpdateQRCodeNotes(mBinding.qrCodeWrapper)
            }

            receiptDetails.verificationRequestDate?.let {
                mBinding.txtVerificationRequestDate.text = formatDisplayDateTimeInMillisecond(it)
            }

            receiptDetails.propertyVerificationRequestId?.let {
                mBinding.txtVerificationRequestID.text = it.toString()
            }
            receiptDetails.propertyName?.let {
                mBinding.txtPropertyName.text = it
            }
            receiptDetails.owner?.let {
                mBinding.txtPropertyOwner.text = it
            }
            receiptDetails.propertySycoTaxID?.let {
                mBinding.txtSycoTaxID.text = it
            }

            if (receiptDetails.citizenSycotaxID != null) {
                mBinding.tvSycotaxIDOf.text = getString(R.string.citizen_syco_tax_id)
                mBinding.tvSYcotaxID.text = receiptDetails.citizenSycotaxID
            }

            if (receiptDetails.citizenCardNo != null) {
                mBinding.llIDCard.visibility = View.VISIBLE
                mBinding.tvIDCardNo.text = receiptDetails.citizenCardNo
            }

            if (receiptDetails.sycoTaxID != null) {
                mBinding.tvSycotaxIDOf.text = getString(R.string.receipt_id_sycotax)
                mBinding.tvSYcotaxID.text = receiptDetails.sycoTaxID
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

            if (receiptDetails.documentVerificationStatus == "Approuvée" && receiptDetails.physicalVerificationStatus == "Approuvée") {
                mBinding.txtVerificationApproalDate.text = formatDisplayDateTimeInMillisecond(checkDatesLatest(receiptDetails.documentVerificationDate, receiptDetails.physicalVerificationDate))
            } else if (receiptDetails.documentVerificationStatus == "Approuvée") {
                mBinding.txtVerificationApproalDate.text = formatDisplayDateTimeInMillisecond(receiptDetails.documentVerificationDate)
            } else if (receiptDetails.physicalVerificationStatus == "Approuvée") {
                mBinding.txtVerificationApproalDate.text = formatDisplayDateTimeInMillisecond(receiptDetails.physicalVerificationDate)
            }

//            receiptDetails.approvedDate?.let {
//                mBinding.txtVerificationApproalDate.text = formatDisplayDateTimeInMillisecond(it)
//            }
            receiptDetails.approvedPeriod?.let {
                mBinding.txtApprovalPeriod.text = it
            }
            receiptDetails.validUptoDate?.let {
                mBinding.txtValidityPeriod.text = formatDisplayDateTimeInMillisecond(it)
            }
            receiptDetails.approvedByUserId?.let {
                mBinding.txtApprovedBy.text = it
            }
            mBinding.txtApprovedBy.text = MyApplication.getPrefHelper().loggedInUserID
        }

    }

    private fun setListeners() {
        mBinding.btnPrint.setOnClickListener {
            if (MyApplication.sunmiPrinterService != null) {
                val view = loadBitmapFromView(mBinding.llVerificationPreview)
                val resizedView = resize(view)
                printHelper.printBitmap(resizedView)
                mListener?.popBackStack()
            } else
                mListener?.showAlertDialog(getString(R.string.msg_print_not_support)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }

        }
    }


    interface Listener {
        fun showProgressDialog()
        fun popBackStack()
        fun dismissDialog()
        fun hideToolbar()
        fun showAlertDialog(message: String)
        fun showAlertDialog(msg: String, okListener: DialogInterface.OnClickListener)
    }
}