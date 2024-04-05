package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.text.TextUtils
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.BusinessTaxDueYearSummary
import com.sgs.citytax.api.payload.GetQrNoteAndLogoPayload
import com.sgs.citytax.api.payload.StoreCustomerB2B
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentBusinessSummaryApprovalBinding
import com.sgs.citytax.databinding.FragmentRejectionRemarksBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.BusinessSummaryPreviewActivity
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.OtpValidationActivity
import com.sgs.citytax.ui.adapter.BusinessSummaryApprovalAdapter
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.REQUEST_CODE_BUSINESS_MASTER
import java.math.BigDecimal

class BusinessSummaryApprovalFragment : BaseFragment(), View.OnClickListener, IClickListener {

    private lateinit var mBinding: FragmentBusinessSummaryApprovalBinding
    private var mListener: Listener? = null
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER

    private var mStoreCustomerB2B: StoreCustomerB2B? = StoreCustomerB2B()
    private var businessOwnerShips: List<BusinessOwnership>? = arrayListOf()
    private var ropList: List<ROPListItem> = arrayListOf()
    private var businessEstimatedTax: BigDecimal = BigDecimal.ZERO
    private var isFirst = false
    var selectedPosition: Int = 0
    var businessTaxDueYearSummaryList: HashMap<Int, List<Any>>? = null
    var propertyTaxDetails: ArrayList<PropertyTax4Business> = arrayListOf()
    var landTaxDetails: ArrayList<PropertyTax4Business> = arrayListOf()
    var groupList: ArrayList<String>? = null
    var orgData: List<OrgData>? = null
    private val printHelper = PrintHelper()
    private var dataResponse: DataResponse? = DataResponse()
    private var businessOwners = ""
    var businessDueSummary: BusinessDueSummary? = null
    private var businessMode: Constant.BusinessMode = Constant.BusinessMode.None


    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (arguments?.getSerializable(Constant.KEY_QUICK_MENU) != null)
                mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            mStoreCustomerB2B = arguments?.getParcelable(Constant.KEY_STORE_CUSTOMER_B2B)
            businessMode = arguments?.getSerializable(Constant.KEY_BUSINESS_MODE) as? Constant.BusinessMode ?: Constant.BusinessMode.None
            isFirst = arguments?.getBoolean(Constant.KEY_IS_FIRST) ?: false
            selectedPosition = arguments?.getInt(Constant.KEY_POSITION) ?: 0
        }
        //endregion
        setViews()
        bindData()
        setListeners()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_business_summary_approval, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    private fun setViews() {
//        setHasOptionsMenu(true)
        if (businessMode == Constant.BusinessMode.BusinessActivate) {
            mBinding.progressView.show()
            mBinding.rootConstraintLayout.hide()
        } else {
            mBinding.progressView.hide()
            mBinding.rootConstraintLayout.show()
        }
        mBinding.listView.setAdapter(BusinessSummaryApprovalAdapter(this))
        if (mCode == Constant.QuickMenu.QUICK_MENU_VERIFICATION_BUSINESS) {
            if (mStoreCustomerB2B?.organization?.statusCode == Constant.OrganizationStatus.INACTIVE.value || mStoreCustomerB2B?.organization?.statusCode == Constant.OrganizationStatus.REJECTED.value) {
                mBinding.llSaveEdit.visibility = GONE
                mBinding.llVerifyReject.visibility = VISIBLE
            } else {
                mBinding.llSaveEdit.visibility = GONE
                mBinding.llVerifyReject.visibility = GONE
            }
        }

        if (mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_SUMMARY) {
            mBinding.llApproveVerify.visibility = VISIBLE
            mBinding.llSaveEdit.visibility = VISIBLE
            mBinding.btnEdit.visibility = GONE
            mBinding.btnProceed.visibility = GONE
            mBinding.btnClose.visibility = GONE
            mBinding.btnPrint.visibility = VISIBLE
        }
    }

    private fun bindData() {
        ObjectHolder.clearTax()
        mListener?.showProgressDialog()
        mBinding.txtTaxPayerName.text = mStoreCustomerB2B?.organization?.organization
        mBinding.txtSycoTaxID.text = mStoreCustomerB2B?.organization?.sycotaxID
        mBinding.txtStatus.text = mStoreCustomerB2B?.organization?.status

        if (isFirst) {
            if (MyApplication.getPrefHelper().IsApprover == "Y") {
                mBinding.btnProceed.text = getString(R.string.activate)
            } else {
                mBinding.btnProceed.text = getString(R.string.proceed)
            }
        } else {
            if (MyApplication.getPrefHelper().IsApprover == "Y") {
                mBinding.btnProceed.text = getString(R.string.approve)
            } else {
                mBinding.btnProceed.text = getString(R.string.verify)
            }
        }

        val tableNames: ArrayList<String> = arrayListOf(
                "CRM_AccountPhones",
                "CRM_AccountEmails",
                "VU_CRM_AccountAddresses",
                "VU_CRM_TaxPayerAccountContacts",
                "VU_CRM_CustomerProductInterests",
                "CRM_CorporateTurnover",
//                "VU_CRM_PropertyOnwerships",
                "VU_ADM_VehicleOwnership",
                "VU_CRM_PublicDomainOccupancy",
                "VU_CRM_RightOfPlaces",
                "VU_CRM_PropertyRents",
                "VU_CRM_Advertisements",
                "VU_CRM_Shows",
                "VU_CRM_Hotels"
        )

        APICall.getCorporateOfficeChildTabList(tableNames, mStoreCustomerB2B?.organization?.accountID!!,
                mStoreCustomerB2B?.organization?.organizationID!!, object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                setAdapter(response)
                dataResponse = response
                mListener?.dismissDialog()
                getBusinessDocuments()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
                getBusinessDocuments()
            }
        })

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

    private fun getBusinessDocuments() {
        mListener?.showProgressDialog()
        APICall.getDocumentDetails("${mStoreCustomerB2B?.organization?.organizationID!!}", "CRM_Organizations",
                object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        mListener?.dismissDialog()
                        val comDocumentReferences = response as ArrayList<COMDocumentReference>
                        val list = arrayListOf<StoreCustomerB2B>()
                        val storeCustomerB2B = StoreCustomerB2B()
                        storeCustomerB2B.attachment = comDocumentReferences
                        list.add(storeCustomerB2B)
                        (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_business_documents), list, mBinding.listView)
                        if (businessMode == Constant.BusinessMode.BusinessActivate) {
//                            mListener?.finish()
                            navigateToPreviewScreen(true)
                        }
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                    }
                })
    }

    private fun setAdapter(response: DataResponse) {
        if (response.typeOfTaxes.isNotEmpty()) {
            val typeOfTaxes = ArrayList<VUCRMCustomerProductInterestLines>()

            var vehicleOwnershipEstimateTax = BigDecimal.ZERO
            var cmeLine: VUCRMCustomerProductInterestLines? = null
            for (header in response.typeOfTaxes) {
                header.estimatedTax?.vucrmCustomerProductInterestLines?.let {
                    for (line in header.estimatedTax?.vucrmCustomerProductInterestLines!!) {
                        line.productCode = header.productCode
                        line.status = header.sts
                        line.active = header.active
                        line.product = header.product
                        line.taxRuleBookCode = header.taxRuleBookCode
                        if ("ADM_VehicleOwnership".toLowerCase() != line.entityName?.toLowerCase() ) {
                            if (line.Applied != "N") {
                                typeOfTaxes.add(line)

                                line.taxRuleBookCode?.toUpperCase()?.let {
                                    if (it == Constant.TaxRuleBook.CP.Code || it == Constant.TaxRuleBook.CME.Code)
                                        cmeLine = line
                                }
                            }
                        } else
                            line.taxAmount?.let {
                                if((line.taxRuleBookCode?.toUpperCase()==Constant.TaxRuleBook.CP.Code) &&line.Applied=="Y") {
                                    vehicleOwnershipEstimateTax = vehicleOwnershipEstimateTax.plus(it)
                                }
                            }
                    }
                }
            }

            cmeLine?.let {
                val index = typeOfTaxes.indexOf(it)
                if((it.taxRuleBookCode?.toUpperCase()==Constant.TaxRuleBook.CP.Code) && it.Applied=="Y") {
                    it.taxAmount?.let { amount ->
                        it.taxAmount = amount.plus(vehicleOwnershipEstimateTax)
                    }
                }else{
                    it.taxAmount = vehicleOwnershipEstimateTax
                }

                typeOfTaxes.set(index, it)
            }

            for (lineEstimatedTax in typeOfTaxes) {
                businessEstimatedTax = businessEstimatedTax.add(lineEstimatedTax.taxAmount)
            }
            ObjectHolder.taxes.addAll(typeOfTaxes)
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_taxes), typeOfTaxes, mBinding.listView)
        }

        if (response.accountPhones.isNotEmpty())
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_phones), response.accountPhones, mBinding.listView)

        if (response.emailAccounts.isNotEmpty())
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_emails), response.emailAccounts, mBinding.listView)

        if (response.accountAddresses.isNotEmpty())
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_address), response.accountAddresses, mBinding.listView)

        if (response.corporateTurnOver.isNotEmpty())
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.corporate_turnover), response.corporateTurnOver, mBinding.listView)

//        if (response.propertyOwnership.isNotEmpty())
//            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.property_ownership), response.propertyOwnership, mBinding.listView)

        if (response.vehicleOwnerships.isNotEmpty())
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.vehicle_ownership), response.vehicleOwnerships, mBinding.listView)

        if (response.podList.isNotEmpty())
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.public_domain_occupancy), response.podList, mBinding.listView)

        if (response.ropList.isNotEmpty()) {
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.rights_of_places_in_markets), response.ropList, mBinding.listView)
            ropList = response.ropList
        }

        if (response.propertyRents.isNotEmpty())
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_rental_details), response.propertyRents, mBinding.listView)

        if (response.vuCrmAdvertisements.isNotEmpty()) {
            mListener?.showProgressDialog()
            for (vuCrmAdvertisement in response.vuCrmAdvertisements) {
                APICall.getDocumentDetails(vuCrmAdvertisement.advertisementId.toString(), "CRM_Advertisements", object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        vuCrmAdvertisement.documentList = response as ArrayList<COMDocumentReference>
                        mListener?.dismissDialog()
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                    }
                })
            }
            //ObjectHolder.taxes.addAll(response.vuCrmAdvertisements)
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_advertisements), response.vuCrmAdvertisements, mBinding.listView)
        }

        if (response.shows.isNotEmpty()) {
            for (show in response.shows) {
                mListener?.showProgressDialog()
                APICall.getDocumentDetails(show.showID.toString(), "CRM_Shows", object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        show.documents = response as ArrayList<COMDocumentReference>
                        mListener?.dismissDialog()
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                    }
                })
            }
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_shows), response.shows, mBinding.listView)
        }

        if (response.hotels.isNotEmpty()) {
            mListener?.showProgressDialog()
            for (hotel in response.hotels) {
                APICall.getDocumentDetails(hotel.hotelId.toString(), "CRM_Hotels", object : ConnectionCallBack<List<COMDocumentReference>> {
                    override fun onSuccess(response: List<COMDocumentReference>) {
                        hotel.documents = response as ArrayList<COMDocumentReference>
                        mListener?.dismissDialog()
                    }

                    override fun onFailure(message: String) {
                        mListener?.dismissDialog()
                    }
                })
            }
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_hotels), response.hotels, mBinding.listView)
        }

        if (response.businessOwnerships.isNotEmpty()) {
            mListener?.showProgressDialog()
            response.businessOwnerships.forEach {
                APICall.getDocumentDetails("${it.contactID}", "CRM_Contacts",
                        object : ConnectionCallBack<List<COMDocumentReference>> {
                            override fun onSuccess(response: List<COMDocumentReference>) {
                                mListener?.dismissDialog()
                                it.documents = response as ArrayList<COMDocumentReference>
                            }

                            override fun onFailure(message: String) {
                                mListener?.dismissDialog()
                            }
                        })
            }
            (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_business_owner), response.businessOwnerships, mBinding.listView)
            businessOwnerShips = response.businessOwnerships
        }

        ObjectHolder.registerBusiness.vuCrmAccounts?.accountId?.let {
            mListener?.showProgressDialog()
            APICall.getBusinessDueSummary(it, object : ConnectionCallBack<BusinessDueSummaryResults> {
                override fun onSuccess(response: BusinessDueSummaryResults) {
                    businessDueSummary = response.businessDueSummary[0]
                    businessDueSummary?.let {
                        //Todo this line commented for not to add business summary duplicatie,
                       // TODO it will add in *BusinessSummaryPreviewActivity* getBusinessDueSummaryDetails function
//                        ObjectHolder.taxes.add(it)
                    }
                    mListener?.dismissDialog()
                    (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_outstandings), response.businessDueSummary, mBinding.listView)
                    getBusinessTaxDueYearSummary()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                    getBusinessTaxDueYearSummary()
                }

            })
        }

        /*if (mCode == Constant.QuickMenu.QUICK_MENU_VERIFICATION_BUSINESS) {
            mListener?.showAlertDialog(R.string.business_already_verified, R.string.ok, View.OnClickListener {
                mListener?.finish()
            }, 0, null, 0, null, null)
        }*/
    }

    private fun getBusinessTaxDueYearSummary() {
        mListener?.showProgressDialog()
        APICall.getbusinessTaxDueYearSummary(mStoreCustomerB2B?.organization?.accountID!!, object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {
                mListener?.dismissDialog()

                if (response.businessTaxDueYearSummary.isNotEmpty()) {
                    var descBusinessTaxDueYearSummary: List<BusinessTaxDueYearSummary> = arrayListOf()
                    descBusinessTaxDueYearSummary = response.businessTaxDueYearSummary.sortedByDescending { it.year!! }
                    businessTaxDueYearSummaryList = descBusinessTaxDueYearSummary.groupBy { it.year!! } as HashMap<Int, List<Any>>
                }
                /***
                 * year sorting descending order
                 */
                var businessSortedWithYear: HashMap<Int, List<Any>>? = HashMap()
                businessSortedWithYear?.putAll(businessTaxDueYearSummaryList!!.toSortedMap(compareByDescending{it}))
                println("keys of sorted >>>>>>>>>>.$businessTaxDueYearSummaryList")
                for (group in businessTaxDueYearSummaryList!!) {
                    businessTaxDueYearSummaryList!![group.key]?.let { (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.tax_year) + " " + group.key.toString(), it, mBinding.listView) }
                }
                getWeaponSummary()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                getWeaponSummary()
            }
        })
        mListener?.dismissDialog()
    }

    private fun getWeaponSummary() {
        mListener?.showProgressDialog()
        APICall.getWeaponSummary(mStoreCustomerB2B?.organization?.accountID!!, "VU_CRM_Weapons", true, object : ConnectionCallBack<WeaponTaxSummaryResponse> {
            override fun onSuccess(response: WeaponTaxSummaryResponse) {
                mListener?.dismissDialog()
                (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_weapon_tax), response.weaponIndividualTaxDtls, mBinding.listView)
                getCartSummary()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                getCartSummary()
            }
        })

    }

    private fun getCartSummary() {
        mListener?.showProgressDialog()
        APICall.getCartSummary(mStoreCustomerB2B?.organization?.accountID!!, "VU_CRM_Carts", true, object : ConnectionCallBack<CartTaxSummaryResponse> {
            override fun onSuccess(response: CartTaxSummaryResponse) {
                mListener?.dismissDialog()
                (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_cart_tax), response.cartIndividualTaxDtls, mBinding.listView)
                getGammingSummary()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                getGammingSummary()
            }
        })

    }

    private fun getGammingSummary() {
        mListener?.showProgressDialog()
        APICall.getGammingSummary(mStoreCustomerB2B?.organization?.accountID!!, "VU_CRM_GamingMachines", true, object : ConnectionCallBack<GammingTaxSummaryResponse> {
            override fun onSuccess(response: GammingTaxSummaryResponse) {
                mListener?.dismissDialog()
                (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_gaming_machine), response.gammingIndividualTaxDtls, mBinding.listView)
                getPropertySummary()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                getPropertySummary()
            }
        })

    }

    private fun getPropertySummary() {
        mListener?.showProgressDialog()



        APICall.getPropertyTax4Business(mStoreCustomerB2B?.organization?.accountID!!, 0, 10, true, true, object : ConnectionCallBack<PropertyLandTaxDetailsList> {
            override fun onSuccess(taxDetails: PropertyLandTaxDetailsList) {
                mListener?.dismissDialog()

                for (detail in taxDetails.results!!.propertyTaxDetails) {
                    if (detail.taxRuleBookCode == "RES_PROP" || detail.taxRuleBookCode == "COM_PROP")
                        propertyTaxDetails.add(detail)
                    if (detail.taxRuleBookCode == "LAND_PROP")
                        landTaxDetails.add(detail)
                }

                (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_property_txt), propertyTaxDetails, mBinding.listView)

                (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_land_txt), landTaxDetails, mBinding.listView)

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()

            }
        })

    }


    private fun setListeners() {
        mBinding.btnEdit.setOnClickListener(object: OnSingleClickListener()
        {
            override fun onSingleClick(v: View?) {

                Handler().postDelayed({
                    mListener?.popBackStack()
                }, 300)
            }

        })
        mBinding.btnProceed.setOnClickListener(object: OnSingleClickListener()
        {
            override fun onSingleClick(v: View?) {
                if (isFirst) {
//                    mStoreCustomerB2B?.organization?.status = getString(R.string.active)
                    if (MyApplication.getPrefHelper().IsApprover == "Y") {
                        mStoreCustomerB2B?.organization?.statusCode =
                            Constant.OrganizationStatus.ACTIVE.value
                        mStoreCustomerB2B?.organization?.status = getString(R.string.active)
                    }
                    val fragment = BusinessSummaryApprovalFragment()

                    //region SetArguments
                    val bundle = Bundle()
                    bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
                    bundle.putParcelable(Constant.KEY_STORE_CUSTOMER_B2B, mStoreCustomerB2B)
                    bundle.putBoolean(Constant.KEY_IS_FIRST, false)
                    fragment.arguments = bundle
                    //endregion

                    mListener?.showToolbarBackButton(R.string.title_register_business)
                    if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
                        mListener?.showToolbarBackButton(R.string.title_update_business)
                    mListener?.addFragment(fragment, true)
                } else {
                    saveBusinessSummaryDetails(v)
                }
            }

        })
        mBinding.btnVerify.setOnClickListener(object: OnSingleClickListener()
        {
            override fun onSingleClick(v: View?) {

//                mStoreCustomerB2B?.organization?.status = getString(R.string.active)
//                mStoreCustomerB2B?.organization?.statusCode = Constant.OrganizationStatus.ACTIVE.value
                saveBusinessSummaryDetails(v)
            }

        })
        mBinding.btnReject.setOnClickListener(object: OnSingleClickListener()
        {
            override fun onSingleClick(v: View?) {

                val layoutInflater = LayoutInflater.from(context)
                val binding = DataBindingUtil.inflate<FragmentRejectionRemarksBinding>(layoutInflater, R.layout.fragment_rejection_remarks, null, false)
                mListener?.showAlertDialog(resources.getString(R.string.remarks), DialogInterface.OnClickListener { dialog, _ ->
                    if (binding.edtRemarks.text.toString().isNotEmpty()) {
                        mStoreCustomerB2B?.organization?.statusCode = Constant.OrganizationStatus.REJECTED.value
                        mStoreCustomerB2B?.organization?.status = getString(R.string.rejected)
                        mStoreCustomerB2B?.organization?.remarks = binding.edtRemarks.text.toString()
                        dialog.dismiss()
                        saveBusinessSummaryDetails(v)
                    } else {
                        mListener?.showSnackbarMsg(getString(R.string.msg_enter_remarks))
                    }
                }, null, binding.root)
            }

        })
        //mBinding.btnPrint.setOnClickListener(this)
        mBinding.btnPrint.setOnClickListener(object: OnSingleClickListener(){
            override fun onSingleClick(v: View?) {
                if (MyApplication.sunmiPrinterService != null) {
//                    val count =
//                        (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).groupCount
//                    for (index in 0..count - 1) {
//                        mBinding.listView.expandGroup(index)
//                    }
//                    val view = mBinding.listView
//                    val printBody = view.drawToBitmap()
//                    val resizedPrintBody = resize(printBody)
//                    printHelper.printBitmap(resizedPrintBody)
                    val taxes = arrayListOf<VUCRMCustomerProductInterestLines>()
                    for (item in ObjectHolder.taxes) {
                        if (item is VUCRMCustomerProductInterestLines) {
                            taxes.add(item)
                        }
                    }
                    if (dataResponse?.businessOwnerships != null)
                    {
                        dataResponse?.businessOwnerships?.let {
                            for (owner in it) {
                                if (!owner.firstName.isNullOrEmpty()) {
                                    businessOwners = if (businessOwners.isEmpty())
                                        "${owner.firstName}"
                                    else "$businessOwners, ${owner.firstName}"
                                }
                            }
                        }
                    }

                    var businessOwnerID: String? = ""
                    if(dataResponse?.businessOwnerships?.size!! > 0)
                        businessOwnerID = dataResponse?.businessOwnerships?.get(0)?.businessOwnerID

                    printHelper.printBusinessSummary(context,mStoreCustomerB2B?.organization,businessOwners, businessOwnerID,
                        businessEstimatedTax,taxes,businessDueSummary,MyApplication.getPrefHelper().language,orgData)
                }
                else
                    mListener?.showAlertDialog(getString(R.string.msg_print_not_support))
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnEdit -> {
//                Handler().postDelayed({
//                    mListener?.popBackStack()
//                }, 300)
            }
            R.id.btnProceed -> {
//                if (isFirst) {
////                    mStoreCustomerB2B?.organization?.status = getString(R.string.active)
//                    mStoreCustomerB2B?.organization?.statusCode = Constant.OrganizationStatus.ACTIVE.value
//                    val fragment = BusinessSummaryApprovalFragment()
//
//                    //region SetArguments
//                    val bundle = Bundle()
//                    bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
//                    bundle.putParcelable(Constant.KEY_STORE_CUSTOMER_B2B, mStoreCustomerB2B)
//                    bundle.putBoolean(Constant.KEY_IS_FIRST, false)
//                    fragment.arguments = bundle
//                    //endregion
//
//                    mListener?.showToolbarBackButton(R.string.title_register_business)
//                    if (mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
//                        mListener?.showToolbarBackButton(R.string.title_update_business)
//                    mListener?.addFragment(fragment, true)
//                } else {
//                    saveBusinessSummaryDetails(v)
//                }
            }

            R.id.btnVerify -> {
//                mStoreCustomerB2B?.organization?.status = getString(R.string.active)
//                mStoreCustomerB2B?.organization?.statusCode = Constant.OrganizationStatus.ACTIVE.value
//                saveBusinessSummaryDetails(v)
            }

            R.id.btnReject -> {
//                val layoutInflater = LayoutInflater.from(context)
//                val binding = DataBindingUtil.inflate<FragmentRejectionRemarksBinding>(layoutInflater, R.layout.fragment_rejection_remarks, null, false)
//                mListener?.showAlertDialog(resources.getString(R.string.remarks), DialogInterface.OnClickListener { dialog, _ ->
//                    if (binding.edtRemarks.text.toString().isNotEmpty()) {
//                        mStoreCustomerB2B?.organization?.statusCode = Constant.OrganizationStatus.REJECTED.value
//                        mStoreCustomerB2B?.organization?.status = getString(R.string.rejected)
//                        mStoreCustomerB2B?.organization?.remarks = binding.edtRemarks.text.toString()
//                        dialog.dismiss()
//                        saveBusinessSummaryDetails(v)
//                    } else {
//                        mListener?.showSnackbarMsg(getString(R.string.msg_enter_remarks))
//                    }
//                }, null, binding.root)
            }
           /* R.id.btnPrint ->
            {
                if (MyApplication.sunmiPrinterService != null) {
//                    val count =
//                        (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).groupCount
//                    for (index in 0..count - 1) {
//                        mBinding.listView.expandGroup(index)
//                    }
//                    val view = mBinding.listView
//                    val printBody = view.drawToBitmap()
//                    val resizedPrintBody = resize(printBody)
//                    printHelper.printBitmap(resizedPrintBody)
                    val taxes = arrayListOf<VUCRMCustomerProductInterestLines>()
                    for (item in ObjectHolder.taxes) {
                        if (item is VUCRMCustomerProductInterestLines) {
                            taxes.add(item)
                        }
                    }
                    if (dataResponse?.businessOwnerships != null)
                    {
                        dataResponse?.businessOwnerships?.let {
                            for (owner in it) {
                                if (!owner.firstName.isNullOrEmpty()) {
                                    businessOwners = if (businessOwners.isEmpty())
                                        "${owner.firstName}"
                                    else "$businessOwners, ${owner.firstName}"
                                }
                            }
                        }
                    }

                    var businessOwnerID: String? = ""
                    if(dataResponse?.businessOwnerships?.size!! > 0)
                        businessOwnerID = dataResponse?.businessOwnerships?.get(0)?.businessOwnerID

                    printHelper.printBusinessSummary(context,mStoreCustomerB2B?.organization,businessOwners, businessOwnerID,
                        businessEstimatedTax,taxes,businessDueSummary,MyApplication.getPrefHelper().language)
                }
                else
                    mListener?.showAlertDialog(getString(R.string.msg_print_not_support))
            }*/

        }
    }

    private fun saveBusinessSummaryDetails(view: View?) {
        mListener?.showProgressDialog()
        APICall.storeCustomerB2B(mStoreCustomerB2B, object : ConnectionCallBack<TaxPayerResponse> {
            override fun onSuccess(response: TaxPayerResponse) {
                mListener?.dismissDialog()
                if (mCode == Constant.QuickMenu.QUICK_MENU_VERIFICATION_BUSINESS) {
                    if (view?.id == R.id.btnVerify) {
                        /*Handler().postDelayed(
                                {
                                    val intent = Intent()
                                    intent.putExtra(Constant.KEY_STATUS, mStoreCustomerB2B?.organization?.status)
                                    intent.putExtra(Constant.KEY_POSITION, selectedPosition)
                                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                                    mListener?.popBackStack()
                                }, 500
                        )*/
//                        Toast.makeText(context, context?.resources?.getString(R.string.verified_sucessfully), Toast.LENGTH_SHORT).show()
                        if (TextUtils.isEmpty(mStoreCustomerB2B?.organization?.email) && TextUtils.isEmpty(mStoreCustomerB2B?.organization?.phone)) {
                            Toast.makeText(
                                context,
                                getString(R.string.please_update_email_phone),
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                        if (TextUtils.isEmpty(mStoreCustomerB2B?.organization?.email) && (!TextUtils.isEmpty(mStoreCustomerB2B?.organization?.phone) && mStoreCustomerB2B?.organization?.phoneVerified == "Y")) {
                            Toast.makeText(context, getString(R.string.please_update_email), Toast.LENGTH_SHORT)
                                .show()
                            return
                        }
                        if (TextUtils.isEmpty(mStoreCustomerB2B?.organization?.phone) && (!TextUtils.isEmpty(mStoreCustomerB2B?.organization?.email) && mStoreCustomerB2B?.organization?.emailVerified == "Y")) {
                            Toast.makeText(context, getString(R.string.please_update_phone), Toast.LENGTH_SHORT)
                                .show()
                            return
                        }
                        mStoreCustomerB2B?.organization?.email?.let {
                            if (!TextUtils.isEmpty(it) && "Y" != mStoreCustomerB2B?.organization?.emailVerified) {
                                navigateToOTPValidation()
                                return
                            }
                        }
                        mStoreCustomerB2B?.organization?.phone?.let {
                            if (!TextUtils.isEmpty(it) && "Y" != mStoreCustomerB2B?.organization?.phoneVerified) {
                                navigateToOTPValidation()
                                return
                            }
                        }
                    } else if (view?.id == R.id.btnReject) {
                        Handler().postDelayed(
                            {
                                val intent = Intent()
                                intent.putExtra(Constant.KEY_STATUS, getString(R.string.rejected))
                                intent.putExtra(Constant.KEY_POSITION, selectedPosition)
                                targetFragment?.onActivityResult(
                                    targetRequestCode,
                                    Activity.RESULT_OK,
                                    intent
                                )
                                mListener?.popBackStack()
                            }, 500
                        )
                        Toast.makeText(context, context?.resources?.getString(R.string.rejected_sucessfully), Toast.LENGTH_SHORT).show()
                    }

                } else {
                    navigateToNextScreen()
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                if(!message.isNullOrEmpty())
                    mListener?.showAlertDialog(message)
            }
        })
    }

    private fun navigateToNextScreen() {
        if (mStoreCustomerB2B?.organization?.email != null && !TextUtils.isEmpty(mStoreCustomerB2B?.organization?.email) && mStoreCustomerB2B?.organization?.emailVerified == "N") {
            mListener?.finish()
            navigateToOTPValidation()
            return
        }
        if (mStoreCustomerB2B?.organization?.phone != null && !TextUtils.isEmpty(mStoreCustomerB2B?.organization?.phone) && mStoreCustomerB2B?.organization?.phoneVerified == "N") {
            mListener?.finish()
            navigateToOTPValidation()
            return
        }
        mListener?.finish()
        navigateToPreviewScreen(false)
    }

    private fun navigateToPreviewScreen(forResult: Boolean) {
        val intent = Intent(context, BusinessSummaryPreviewActivity::class.java)
        intent.putExtra(Constant.KEY_ORGANISATION, mStoreCustomerB2B?.organization)
        businessOwnerShips?.let {
            if (it.isNotEmpty())
                intent.putParcelableArrayListExtra(
                    Constant.KEY_BUSINESS_OWNER,
                    it as java.util.ArrayList<out Parcelable>
                )
        }
        intent.putExtra(Constant.KEY_BUSINESS_MODE,businessMode)
        intent.putExtra(Constant.KEY_OTP_VALIDATION, true)
        intent.putExtra(Constant.KEY_ESTIMATED_TAX, businessEstimatedTax)
        if (forResult) {
            startActivityForResult(intent, Constant.REQUEST_CODE_BUSINESS_MASTER)
        } else {
            startActivity(intent)
        }
    }

    private fun navigateToOTPValidation() {
        val intent = Intent(context, OtpValidationActivity::class.java)
        intent.putExtra(Constant.KEY_STORE_CUSTOMER_B2B, mStoreCustomerB2B)
        intent.putExtra(Constant.KEY_ORGANISATION, mStoreCustomerB2B?.organization)
        businessOwnerShips?.let {
            if (it.isNotEmpty())
                intent.putParcelableArrayListExtra(Constant.KEY_BUSINESS_OWNER, it as java.util.ArrayList<out Parcelable>)
        }
        mCode.let {
            intent.putExtra(Constant.KEY_QUICK_MENU, it)
        }
        intent.putExtra(Constant.KEY_ESTIMATED_TAX, businessEstimatedTax)
        startActivityForResult(intent,REQUEST_CODE_BUSINESS_MASTER)
    }

    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String)
        fun showAlertDialog(msg: String, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener?, view: View)
        fun showProgressDialog()
        fun showSnackbarMsg(msg: String)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        fun finish()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener?, view: View?)
        fun setResult(resultCode: Int, intent: Intent)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                R.id.itemImageDocumentPreview -> {
                    val documentReferences: ArrayList<COMDocumentReference> = view.tag as ArrayList<COMDocumentReference>
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    documentReferences.remove(comDocumentReference)
                    documentReferences.add(0, comDocumentReference)
                    intent.putExtra(Constant.KEY_DOCUMENT, documentReferences)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_BUSINESS_MASTER){
            data?.let {
                if (it.getBooleanExtra(Constant.KEY_REFRESH, false)) {
                    val intent = Intent()
                    intent.putExtra(Constant.KEY_REFRESH, true)
                    requireActivity().setResult(BaseActivity.RESULT_OK, intent)
                    mListener?.finish()
                }
                if (it.hasExtra(Constant.KEY_REFRESH_VERIFICATION)) {
                    if (it.getBooleanExtra(Constant.KEY_REFRESH_VERIFICATION, false)) {
                        val intent = Intent()
                        intent.putExtra(
                            Constant.KEY_STATUS,
                            mStoreCustomerB2B?.organization?.status
                        )
                        intent.putExtra(Constant.KEY_POSITION, selectedPosition)
                        targetFragment?.onActivityResult(
                            targetRequestCode,
                            Activity.RESULT_OK,
                            intent
                        )
                    }
                    mListener?.popBackStack()
                }
            }
        }
    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, null)
        mListener?.popBackStack()
    }
}