package com.sgs.citytax.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetQrNoteAndLogoPayload
import com.sgs.citytax.api.payload.Organization
import com.sgs.citytax.api.response.BusinessDueSummary
import com.sgs.citytax.api.response.BusinessDueSummaryResults
import com.sgs.citytax.api.response.OrgData
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityBusinessSummaryPreviewBinding
import com.sgs.citytax.model.AppReceiptPrint
import com.sgs.citytax.model.BusinessOwnership
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.VUCRMCustomerProductInterestLines
import com.sgs.citytax.ui.adapter.BusinessSummaryTaxAdapter
import com.sgs.citytax.util.*
import java.math.BigDecimal
import java.util.*


class BusinessSummaryPreviewActivity : BaseActivity(), IClickListener {

    private lateinit var mBinding: ActivityBusinessSummaryPreviewBinding
    private var organization: Organization? = null
    private var businessOwnerShip: ArrayList<BusinessOwnership>? = null
    private var isOTPVerified: Boolean = false
    private var businessOwners = ""
    private var ownerIds = ""
    private var cards = ""
    private var sycoTaxes = ""

    private var printHelper = PrintHelper()
    private var estimatedTax: BigDecimal = BigDecimal.ZERO
    var businessDueSummary: BusinessDueSummary? = null
    private var businessMode: Constant.BusinessMode = Constant.BusinessMode.None
    var orgData: List<OrgData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_business_summary_preview)
        processIntent()
        bindData()
        setListeners()
    }

    private fun processIntent() {
        intent.let {
            if (it.hasExtra(Constant.KEY_ORGANISATION))
                organization = it.getParcelableExtra(Constant.KEY_ORGANISATION)
            if (it.hasExtra(Constant.KEY_BUSINESS_OWNER))
                businessOwnerShip = it.getParcelableArrayListExtra(Constant.KEY_BUSINESS_OWNER)
            if (it.hasExtra(Constant.KEY_OTP_VALIDATION))
                isOTPVerified = it.getBooleanExtra(Constant.KEY_OTP_VALIDATION, false)
            if (it.hasExtra(Constant.KEY_ESTIMATED_TAX))
                estimatedTax = it.getSerializableExtra(Constant.KEY_ESTIMATED_TAX) as BigDecimal
            businessMode = it.getSerializableExtra(Constant.KEY_BUSINESS_MODE) as? Constant.BusinessMode ?: Constant.BusinessMode.None
        }
    }

    private fun bindData() {
        mBinding.txtBusinessOwnerLabel.visibility = GONE
        mBinding.txtCitizenSycoTaxIDLabel.visibility = GONE
        mBinding.txtCitizenCardNo.visibility = GONE
        mBinding.txtCitizenSycoTaxID.visibility = GONE
        mBinding.txtCitizenIDCardLabel.visibility = GONE
        mBinding.txtBusinessOwner.visibility = GONE
        mBinding.txtBusinessOwnerIdLabel.visibility = GONE
        mBinding.txtBusinessOwnerId.visibility = GONE
        mBinding.txtPhoneLabel.visibility = GONE
        mBinding.txtPhone.visibility = GONE
        mBinding.txtEmailLabel.visibility = GONE
        mBinding.txtEmail.visibility = GONE

        if (organization != null) {
            mBinding.txtSycoTaxID.text = organization?.sycotaxID
            mBinding.txtBusinessName.text = organization?.organization
        }
        if (organization?.phone != null) {
            mBinding.txtPhone.text = organization?.phone
            mBinding.txtPhoneLabel.visibility = VISIBLE
            mBinding.txtPhone.visibility = VISIBLE
        }
        if (organization?.email != null) {
            mBinding.txtEmail.text = organization?.email
            mBinding.txtEmailLabel.visibility = VISIBLE
            mBinding.txtEmail.visibility = VISIBLE
        }

        if (businessOwnerShip != null) {
            businessOwnerShip?.let {
                for (owner in it) {
                    if (!owner.firstName.isNullOrEmpty()) {
                        businessOwners = if (businessOwners.isEmpty())
                            "${owner.firstName}"
                        else "$businessOwners, ${owner.firstName}"
                    }
                }
                if (businessOwners.isNotEmpty()) {
                    mBinding.txtBusinessOwner.text = businessOwners
                    mBinding.txtBusinessOwner.visibility = VISIBLE
                    mBinding.txtBusinessOwnerLabel.visibility = VISIBLE
                }

                for (owner in it) {
                    if (!owner.businessOwnerID.isNullOrEmpty()) {
                        ownerIds = if (ownerIds.isEmpty())
                            "${owner.businessOwnerID}"
                        else "$ownerIds, ${owner.businessOwnerID}"
                    }
                }
                if (ownerIds.isNotEmpty()) {
                    mBinding.txtBusinessOwnerId.text = ownerIds
                    mBinding.txtBusinessOwnerIdLabel.visibility = VISIBLE
                    mBinding.txtBusinessOwnerId.visibility = VISIBLE
                }

                for (owner in it) {
                    if (!owner.citizenCardNo.isNullOrEmpty()) {
                        cards = if (cards.isEmpty())
                            "${owner.citizenCardNo}"
                        else "$cards, ${owner.citizenCardNo}"
                    }
                }
                if (cards.isNotEmpty()) {
                    mBinding.txtCitizenCardNo.text = cards
                    mBinding.txtCitizenIDCardLabel.visibility = VISIBLE
                    mBinding.txtCitizenCardNo.visibility = VISIBLE
                }

                for (owner in it) {
                    if (!owner.citizenSycoTaxID.isNullOrEmpty()) {
                        sycoTaxes = if (sycoTaxes.isEmpty())
                            "${owner.citizenSycoTaxID}"
                        else "$sycoTaxes, ${owner.citizenSycoTaxID}"
                    }
                }
                if (sycoTaxes.isNotEmpty()) {
                    mBinding.txtCitizenSycoTaxID.text = sycoTaxes
                    mBinding.txtCitizenSycoTaxIDLabel.visibility = VISIBLE
                    mBinding.txtCitizenSycoTaxID.visibility = VISIBLE
                }

            }

        }



        if (estimatedTax > BigDecimal.ZERO) {
            mBinding.txtEstimatedTax.text = formatWithPrecision(estimatedTax)
            mBinding.txtEstimatedTaxLabel.visibility = VISIBLE
            mBinding.txtEstimatedTax.visibility = VISIBLE
        } else {
            mBinding.txtEstimatedTaxLabel.visibility = GONE
            mBinding.txtEstimatedTax.visibility = GONE
        }

//        if (!isOTPVerified){
        if (MyApplication.getPrefHelper().IsApprover != "Y" || businessMode == Constant.BusinessMode.BusinessActivate) {
            mBinding.btnGenerateTaxNotice.text = getString(R.string.close)
            mBinding.btnClose.visibility = GONE
        } else {
            if (isOTPVerified) {
                mBinding.btnGenerateTaxNotice.text = getString(R.string.generate_tax_notice)
                mBinding.btnClose.visibility = VISIBLE
            } else {
                mBinding.btnGenerateTaxNotice.text = getString(R.string.close)
                mBinding.btnClose.visibility = GONE
            }
        }



        val itemDecor = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        mBinding.recyclerView.addItemDecoration(itemDecor)
        getBusinessDueSummaryDetails()

        val payload = GetQrNoteAndLogoPayload()
        APICall.getQrNoteAndLogo(payload, object : ConnectionCallBack<List<OrgData>> {
            override fun onSuccess(response: List<OrgData>)
            {
                orgData  = response
            }

            override fun onFailure(message: String) {

            }
        })
    }

    fun setListeners() {
        mBinding.btnClose.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                onBackPressed()
            }
        })
        mBinding.btnGenerateTaxNotice.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
//                if(isOTPVerified)
                if (mBinding.btnGenerateTaxNotice.text == getString(R.string.generate_tax_notice))
                    navigateToTaxNotice()
                else
                    onBackPressed()
            }
        })
        mBinding.btnPrint.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                insertReceiptPrint(
                    organization?.organizationID,
                    Constant.ReceiptType.BUSINESS_SUMMARY
                )
            }

        })
    }


    private fun navigateToTaxNotice() {
        val intent = Intent(this, TaxDetailsActivity::class.java)
        intent.putExtra(
            Constant.KEY_CUSTOMER_ID,
            ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
        )
        intent.putExtra(Constant.KEY_QUICK_MENU, Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE)
        startActivity(intent)
        finish()
    }

    private fun getBitmap() {
        val url = getURL(Constant.ReceiptType.BUSINESS_SUMMARY)
        val bitmap = createCode(
            url.replace("@documentNo@", organization?.sycotaxID.toString()),
            BarcodeFormat.QR_CODE
        )
        ObjectHolder.taxes.add(bitmap)
        mBinding.recyclerView.adapter = BusinessSummaryTaxAdapter(ObjectHolder.taxes)
    }

    private fun getBusinessDueSummaryDetails() {
        showProgressDialog()
        organization?.accountID?.let { it ->
            APICall.getBusinessDueSummary(
                it,
                object : ConnectionCallBack<BusinessDueSummaryResults> {
                    override fun onSuccess(response: BusinessDueSummaryResults) {
                        dismissDialog()
                        businessDueSummary = response.businessDueSummary[0]
                        businessDueSummary?.let {
                            ObjectHolder.taxes.add(it)
                        }
                        getBitmap()
                    }

                    override fun onFailure(message: String) {
                        dismissDialog()
                        showAlertDialog(message)
                        getBitmap()
                    }

                })
        }
    }

    private fun insertReceiptPrint(Id: Int?, receiptType: Constant.ReceiptType) {
        val appReceiptPrint = AppReceiptPrint()
        appReceiptPrint.printDateTime = getDate(Date(), DateTimeTimeZoneMillisecondFormat)
        appReceiptPrint.receiptCode = "Bussiness_Summary"
        appReceiptPrint.primaryKeyValue = Id

        APICall.insertPrintRequest(appReceiptPrint, object : ConnectionCallBack<Int> {
            override fun onSuccess(response: Int) {
                try {
                    if (receiptType == Constant.ReceiptType.BUSINESS_SUMMARY) {
                        val taxes = arrayListOf<VUCRMCustomerProductInterestLines>()
                        for (item in ObjectHolder.taxes) {
                            if (item is VUCRMCustomerProductInterestLines) {
                                taxes.add(item)
                            }
                        }
                        printHelper.printBusinessSummaryTemplateContent(
                            this@BusinessSummaryPreviewActivity,
                            organization,
                            businessOwners,
                            ownerIds,
                            estimatedTax,
                            taxes,
                            businessDueSummary,
                            prefHelper.language,
                            sycoTaxes,
                            cards,
                            orgData
                        )
                        /* val templateString = TxtTemplateUtils.getSummaryTemplateContent(organization, businessOwnerShip?.firstName, estimatedTax, 32)
                            printHelper.printUSBThermalPrinter(templateString, organization?.sycotaxID, Constant.ReceiptType.BUSINESS_SUMMARY)*/
                    }
                } catch (e: Exception) {
                    showAlertDialog(getString(R.string.msg_print_not_support))
                }
            }

            override fun onFailure(message: String) {
                showAlertDialog(message)
            }
        })
    }

    @Throws(WriterException::class)
    fun createCode(str: String?, type: BarcodeFormat?): Bitmap {
        val mHashtable = Hashtable<EncodeHintType, String?>()
        mHashtable[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val matrix = MultiFormatWriter().encode(str, type, 256, 256, mHashtable)
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (matrix[x, y]) {
                    pixels[y * width + x] = -0x1000000
                } else {
                    pixels[y * width + x] = -0x1
                }
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    override fun onBackPressed() {
        if (businessMode == Constant.BusinessMode.BusinessActivate) {
            val intent = Intent()
            intent.putExtra(Constant.KEY_REFRESH, true)
            setResult(RESULT_OK, intent)
            super.onBackPressed()
        } else {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onClick(view: View, position: Int, obj: Any) {

    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

}