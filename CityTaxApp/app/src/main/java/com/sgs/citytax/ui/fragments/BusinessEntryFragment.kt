package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentBusinessEntryBinding
import com.sgs.citytax.databinding.FragmentRejectionRemarksBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.*


class BusinessEntryFragment : BaseFragment(), View.OnClickListener {
    private var localScreenMode: Constant.ScreenMode? = null
    private var mListener: Listener? = null
    private lateinit var mBinding: FragmentBusinessEntryBinding
    private var prefHelper: PrefHelper = MyApplication.getPrefHelper()
    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS
    private var sycoTaxID = ""
    private var mHelper: LocationHelper? = null

    private var vuCrmAccounts: VUCRMAccounts? = null
    private var statusCodesList: List<COMStatusCode>? = null
    private var organizationList: MutableList<VUCRMOrganization>? = null
    private var businessTypeList: MutableList<CRMCustomerSegments>? = null
    private var activityDomainList: MutableList<CRMActivityDomain>? = null
    private var activityClassList: MutableList<CRMActivityClass>? = null
    private var hotelDesFinancesList: MutableList<COMHotelDesFinances>? = null

    private var mGeoAddress: GeoAddress? = null
    private var mResponseCountriesList: List<COMCountryMaster> = java.util.ArrayList()
    private var mResponseStatesList: List<COMStateMaster> = java.util.ArrayList()
    private var mResponseCitiesList: List<VUCOMCityMaster> = java.util.ArrayList()
    private var mResponseZonesList: List<COMZoneMaster> = java.util.ArrayList()
    private var mResponseSectorsList: List<COMSectors> = java.util.ArrayList()

    var pageIndex: Int = 1
    val pageSize: Int = 20
    var defaultBusinessIndex = -1

    private var hideEditButtton: Boolean = false

    private var isChangedToEditMode = false

    private var businessMode: Constant.BusinessMode = Constant.BusinessMode.None

    private var isForReject: Boolean = false
    private var rejectRemarks: String? = null
    private var isActionEnabled: Boolean = true //todo to check if view mode when Spinners are re-enabled - 16/3/2022

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null)
                parentFragment as Listener
            else context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_business_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentLocation()
    }

    private fun setEditAction(action: Boolean) {
        isActionEnabled = action
        mBinding.edtOrganization.isEnabled = action
        mBinding.spnTelephoneCode.isEnabled = action
        mBinding.edtPhoneNumber.isEnabled = action
        mBinding.edtEmail.isEnabled = action
        mBinding.edtIFU.isEnabled = action
        mBinding.edtTradeNo.isEnabled = action
        mBinding.edtWebsite.isEnabled = action
        mBinding.edtRemarks.isEnabled = action
        mBinding.spnCountry.isEnabled = action
        mBinding.spnState.isEnabled = action
        mBinding.spnCity.isEnabled = action
        mBinding.spnZone.isEnabled = action
        mBinding.spnSector.isEnabled = action
        mBinding.edtStreet.isEnabled = action
        mBinding.edtPlot.isEnabled = action
        mBinding.edtBlock.isEnabled = action
        mBinding.edtDoorNo.isEnabled = action
        mBinding.edtZipCode.isEnabled = action
        mBinding.edtDescription.isEnabled = action
        mBinding.ivAddLocation.isEnabled = action
        if (MyApplication.getPrefHelper().getAsPerAgentType()) {
            mBinding.spnStatus.isEnabled = false
        } else {
            mBinding.spnStatus.isEnabled = action
        }

        // mBinding.spnBusinessType.isEnabled = action
        mBinding.spnBusinessTypeSpin.isEnabled = action
        mBinding.spnActivityDomain.isEnabled = action
        //mBinding.spnActivityClass.isEnabled = action
        mBinding.edtActivityClassSpin.isEnabled = action
        mBinding.spnParentBusiness.isEnabled = action
        mBinding.spnHotelDesFinance.isEnabled = action
        if (action) {
            mBinding.btnSave.visibility = View.VISIBLE
            mBinding.btnEdit.visibility = View.GONE
        } else {
            mBinding.btnSave.visibility = View.GONE
            mBinding.btnEdit.visibility = View.VISIBLE
        }
        /* if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ASA.name) {
             mBinding.btnSave.visibility = View.GONE
             mBinding.btnEdit.visibility = View.GONE
         }*/
        if (hideEditButtton) {
            mBinding.btnEdit.visibility = View.GONE
        }
    }

    fun setVisibleByAgent() {
        if (mListener?.screenMode == Constant.ScreenMode.VIEW && (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)) {
            if (hideEditButtton) {
                if (MyApplication.getPrefHelper().getAsPerAgentType()) {
                    mBinding.btnEdit.visibility = View.GONE
                    setEditAction(true)
                    setClickEnable()
                    mBinding.btnSave.visibility = View.VISIBLE
                } else {
                    mBinding.btnEdit.visibility = View.GONE
                    mBinding.btnSave.visibility = View.GONE
                }
            } else {
                mBinding.btnEdit.visibility = View.VISIBLE
                setEditAction(false)
                setClickEnable()
                mBinding.btnSave.visibility = View.GONE
            }
        }
    }

    override fun initComponents() {

        //region getArguments
        arguments?.let {
            sycoTaxID = arguments?.getString(Constant.KEY_SYCO_TAX_ID) ?: ""
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            businessMode = arguments?.getSerializable(Constant.KEY_BUSINESS_MODE) as? Constant.BusinessMode ?: Constant.BusinessMode.None
            vuCrmAccounts = ObjectHolder.registerBusiness.vuCrmAccounts
            mGeoAddress = arguments?.getParcelable(Constant.KEY_ADDRESS)
            hideEditButtton = arguments?.getBoolean(Constant.KEY_EDIT) ?: false

        }
        //endregion
        setViews()
        showViewsEnabled()
        bindSpinner()
        setListeners()
        fetchChildEntriesCount()
        // setEvents()
//        setVisibleByAgent()

        //TODO As per new requirement, set to Gone, when coming from BusinessInfo Dialog (Business Record)- 10/3/2022
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD) {
            mBinding.btnEdit.visibility = View.GONE
        }else{
            setVisibleByAgent()
        }
    }

    private fun setEvents() {

        if (defaultBusinessIndex == -1)
            mBinding.spnBusinessTypeSpin.setText("")

        mBinding.spnBusinessTypeSpin.setOnClickListener {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                businessTypeList!!
            )
            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setAdapter(adapter as ArrayAdapter<*>) { dialog, which ->
                dialog.dismiss()
                val any = adapter.getItem(which)
                mBinding.spnBusinessTypeSpin.setText(any?.segment)
            }
            builder.show()
        }

        mBinding.edtActivityClassSpin.setOnClickListener {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                activityClassList!!
            )
            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setAdapter(adapter as ArrayAdapter<*>) { dialog, which ->
                dialog.dismiss()
                val any = adapter.getItem(which)
                if (any?.name != getString(R.string.select))
                    mBinding.edtActivityClassSpin.setText(any?.name)
                else
                    mBinding.edtActivityClassSpin.setText("")
            }
            builder.show()
        }
    }

    private fun showViewsEnabled() {
        when (mListener?.screenMode) {
            Constant.ScreenMode.EDIT -> setEditAction(true)
            Constant.ScreenMode.VIEW -> setEditAction(false)
        }
    }

    override fun onClick(view: View?) {
        try {
            view?.let {
                when (view.id) {

                    R.id.ivAddLocation -> {
                        var mLatitude = 0.0
                        var mLongitude = 0.0
                        if (mBinding.edtLatitude.text.toString().trim().isNotEmpty()) {
                            mLatitude = mBinding.edtLatitude.text.toString().trim().toDouble()
                        }

                        if (mBinding.edtLongitude.text.toString().trim().isNotEmpty()) {
                            mLongitude = mBinding.edtLongitude.text.toString().trim().toDouble()
                        }
                        val dialog: LocateDialogFragment =
                            LocateDialogFragment.newInstance(mLatitude, mLongitude)
                        dialog.show(childFragmentManager, LocateDialogFragment::class.java.simpleName)
                    }

                    R.id.llWeaponTax -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val weaponTaxFragment = WeaponTaxMasterFragment()

                                //region SetArguments
                                val bundle = Bundle()

                                getPayload().geoAddress?.let {
                                    ObjectHolder.registerBusiness.geoAddress = it
                                }

                                if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS)
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_WEAPON_TAX
                                    )
                                else
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_WEAPON_TAX
                                    )

                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                weaponTaxFragment.arguments = bundle
                                //endregion

                                weaponTaxFragment.setTargetFragment(
                                    this,
                                    Constant.REQUEST_CODE_WEAPON_LIST
                                )
                                mListener?.showToolbarBackButton(R.string.title_weapon_tax)
                                mListener?.addFragment(weaponTaxFragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }

                    }
                    R.id.llPropertyTax -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val propertyTaxFragment = PropertyTaxMasterFragment()

                                //region SetArguments
                                val bundle = Bundle()

                                getPayload().geoAddress?.let {
                                    ObjectHolder.registerBusiness.geoAddress = it
                                }

                                if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS)
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_PROPERTY
                                    )
                                else
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_PROPERTY
                                    )

                                //todo Hide views for geo spacial- Busianess Record - 15/3/2022
                                if(fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD){
                                    bundle.putBoolean(
                                        Constant.KEY_GEO_SPATIAL_VIEW,
                                        true
                                    )
                                }

                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                propertyTaxFragment.arguments = bundle
                                //endregion

                                propertyTaxFragment.setTargetFragment(
                                    this,
                                    Constant.REQUEST_CODE_PROPERTY_LIST
                                )
                                mListener?.showToolbarBackButton(R.string.title_property_txt)
                                mListener?.addFragment(propertyTaxFragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }

                    }
                    R.id.llLandTax -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val propertyTaxFragment = PropertyTaxMasterFragment()

                                //region SetArguments
                                val bundle = Bundle()

                                getPayload().geoAddress?.let {
                                    ObjectHolder.registerBusiness.geoAddress = it
                                }

                                if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS)
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND
                                    )
                                else
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND
                                    )

                                //todo Hide views for geo spacial- Busianess Record - 15/3/2022
                                if(fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD){
                                    bundle.putBoolean(
                                        Constant.KEY_GEO_SPATIAL_VIEW,
                                        true
                                    )
                                }

                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                propertyTaxFragment.arguments = bundle
                                //endregion

                                propertyTaxFragment.setTargetFragment(
                                    this,
                                    Constant.REQUEST_CODE_PROPERTY_LIST
                                )
                                mListener?.showToolbarBackButton(R.string.title_land_txt)
                                mListener?.addFragment(propertyTaxFragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }

                    }
                    R.id.llCartTax -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val fragment = CartTaxMasterFragment()

                                getPayload().geoAddress?.let {
                                    ObjectHolder.registerBusiness.geoAddress = it
                                }
                                //region SetArguments
                                val bundle = Bundle()

                                if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS)
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_CART_TAX
                                    )
                                else
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_CART_TAX
                                    )

                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                fragment.arguments = bundle
                                //endregion

                                fragment.setTargetFragment(this, Constant.REQUEST_CODE_WEAPON_LIST)
                                mListener?.showToolbarBackButton(R.string.title_cart_tax)
                                mListener?.addFragment(fragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }

                    }

                    R.id.llGameMachineTax -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val fragment = GamingMachineTaxMasterFragment()

                                getPayload().geoAddress?.let {
                                    ObjectHolder.registerBusiness.geoAddress = it
                                }

                                //region SetArguments
                                val bundle = Bundle()

                                if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS)
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_GAMING_MACHINE_TAX
                                    )
                                else
                                    bundle.putSerializable(
                                        Constant.KEY_QUICK_MENU,
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_GAMING_MACHINE_TAX
                                    )

                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                fragment.arguments = bundle
                                //endregion

                                fragment.setTargetFragment(this, Constant.REQUEST_CODE_WEAPON_LIST)
                                mListener?.showToolbarBackButton(R.string.title_gaming_machine)
                                mListener?.addFragment(fragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }

                    }
                    R.id.llTaxes -> {
                        localScreenMode = mListener?.screenMode
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {

                                //region Set Arguments
                                val taxMasterFragment = TaxMasterFragment()
                                getPayload().geoAddress?.let {
                                    ObjectHolder.registerBusiness.geoAddress = it
                                }
                                val bundle = Bundle()
                                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                taxMasterFragment.arguments = bundle
                                //endregion

                                taxMasterFragment.setTargetFragment(
                                    this,
                                    Constant.REQUEST_CODE_TYPE_OF_TAX_LIST
                                )
                                mListener?.showToolbarBackButton(R.string.title_taxes)
                                mListener?.addFragment(taxMasterFragment, true)
                            }
                            validateView() -> {
                                saveBusiness(getPayload(), view)
                                //checkPhoneNumberExist(view)
                            }
                            else -> {

                            }

                            /*vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val fragment = TaxEntryFragment.newInstance(fromScreen)
                                mListener?.showToolbarBackButton(R.string.title_taxes)
                                mListener?.addFragment(fragment, true)
                            }
                            validateView() -> {
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }*/
                        }
                    }

                    R.id.llBusinessOwner -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                //region Set Arguments
                                val businessOwnerMasterFragment = BusinessOwnerMasterFragment()
                                val bundle = Bundle()
                                bundle.putSerializable(
                                    Constant.KEY_QUICK_MENU,
                                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
                                        Constant.QuickMenu.QUICK_MENU_REGISTER_OWNER
                                    } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD) {
                                        if (MyApplication.getPrefHelper()
                                                .getAsPerAgentType() && hideEditButtton && mListener?.screenMode == Constant.ScreenMode.VIEW
                                        ) {
                                            //todo Hide views for geo spacial- Busianess Record - 15/3/2022
                                            isChangedToEditMode = false
                                            mListener?.screenMode = Constant.ScreenMode.VIEW
                                            bundle.putBoolean(Constant.KEY_GEO_SPATIAL_VIEW, true)
                                        }else{
                                            //todo Hide views for geo spacial- Busianess Record - 15/3/2022
                                            isChangedToEditMode = false
                                            mListener?.screenMode = Constant.ScreenMode.VIEW
                                            bundle.putBoolean(Constant.KEY_GEO_SPATIAL_VIEW, true)
                                        }
                                        Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD
                                    } else {
                                        if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS && MyApplication.getPrefHelper()
                                                .getAsPerAgentType() && hideEditButtton && mListener?.screenMode == Constant.ScreenMode.VIEW
                                        ) {
                                            isChangedToEditMode = true
                                            mListener?.screenMode = Constant.ScreenMode.EDIT
                                        }
                                        Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS
                                    }
                                )
                                bundle.putString(Constant.KEY_SHOW, Constant.KEY_BUSINESS)
                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                val geoAddress: GeoAddress
                                if (!TextUtils.isEmpty(mBinding.edtLatitude.text) && !TextUtils.isEmpty(
                                        mBinding.edtLongitude.text
                                    )
                                ) {
                                    geoAddress = prepareData(
                                        mBinding.edtLatitude.text.toString().trim().toDouble(),
                                        mBinding.edtLongitude.text.toString().trim().toDouble()
                                    )
                                } else {
                                    geoAddress = prepareData(0.0, 0.0)
                                }
                                bundle.putParcelable(Constant.KEY_ADDRESS, geoAddress)
                                businessOwnerMasterFragment.arguments = bundle
                                //endregion

                                businessOwnerMasterFragment.setTargetFragment(
                                    this,
                                    Constant.REQUEST_CODE_BUSINESS_OWNERSHIP
                                )
                                mListener?.showToolbarBackButton(R.string.title_business_owner)
                                mListener?.addFragment(businessOwnerMasterFragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }

                    }
                    R.id.llDocuments -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val fragment = DocumentsMasterFragment()

                                //region SetArguments
                                val bundle = Bundle()
                                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
                                        ?: 0
                                )
                                fragment.arguments = bundle
                                //endregion

                                fragment.setTargetFragment(this, Constant.REQUEST_CODE_DOCUMENTS_LIST)
                                mListener?.showToolbarBackButton(R.string.documents)
                                mListener?.addFragment(fragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }
                    }

                    R.id.llNotes -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val fragment = NotesMasterFragment()

                                //region SetArguments
                                val bundle = Bundle()
                                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId
                                        ?: 0
                                )
                                fragment.arguments = bundle
                                //endregion

                                fragment.setTargetFragment(this, Constant.REQUEST_CODE_NOTES_LIST)

                                mListener?.showToolbarBackButton(R.string.notes)
                                mListener?.addFragment(fragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }
                    }

                    R.id.llOutstandings -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val fragment = OutstandingsMasterFragment()
                                val bundle = Bundle()
                                bundle.putInt(
                                    Constant.KEY_CUSTOMER_ID,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                fragment.arguments = bundle
                                fragment.setTargetFragment(
                                    this,
                                    Constant.REQUEST_CODE_OUT_STANDING_ENTRY
                                )

                                mListener?.showToolbarBackButton(R.string.title_initial_outstandings)
                                mListener?.addFragment(fragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }
                    }

                    R.id.btnEdit -> {
                        mListener?.screenMode = Constant.ScreenMode.EDIT
                        showViewsEnabled()
                        setClickEnable()
                        fromScreen = Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS
                        mListener?.showToolbarBackButton(R.string.title_update_business)
                    }

                    R.id.spnParentBusiness -> {
                        SearchableDialog.showSpinnerSelectionDialog(
                            activity,
                            organizationList,
                            object : SearchableDialog.SpinnerDialogInterface {
                                override fun onItemSelected(selObj: Any?) {
                                    val obj = (selObj as VUCRMOrganization)
                                    mBinding.spnParentBusiness.text = obj.organization
                                }
                            })
                    }

                    R.id.llListDueNotice -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val fragment = ListofDueNoticeMasterFragment()
                                getPayload().geoAddress?.let {
                                    ObjectHolder.registerBusiness.geoAddress = it
                                }
                                //region SetArguments
                                val bundle = Bundle()
                                bundle.putSerializable(
                                    Constant.KEY_QUICK_MENU,
                                    Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS
                                )
                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                fragment.arguments = bundle
                                //endregion


                                bundle.putInt(Constant.KEY_PRIMARY_KEY, vuCrmAccounts?.accountId!!)

                                fragment.arguments = bundle
                                fragment.setTargetFragment(
                                    this,
                                    Constant.REQUEST_CODE_EXAMPLE
                                )
                                mListener?.showToolbarBackButton(R.string.title_list_of_due_notice)
                                mListener?.addFragment(fragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }

                    }

                    R.id.llAgreementList -> {
                        when {
                            vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
                                    vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 -> {
                                val fragment = AgreementListMasterFragment()
                                getPayload().geoAddress?.let {
                                    ObjectHolder.registerBusiness.geoAddress = it
                                }
                                //region SetArguments
                                val bundle = Bundle()
                                bundle.putSerializable(
                                    Constant.KEY_QUICK_MENU,
                                    Constant.QuickMenu.QUICK_MENU_AGREEMENT
                                )

                                //todo Hide views for geo spacial- Busianess Record - 15/3/2022
                                if(fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD){
                                    bundle.putBoolean(
                                        Constant.KEY_GEO_SPATIAL_VIEW,
                                        true
                                    )
                                }
                                bundle.putInt(
                                    Constant.KEY_PRIMARY_KEY,
                                    ObjectHolder.registerBusiness.vuCrmAccounts?.accountId
                                        ?: 0
                                )
                                fragment.arguments = bundle
                                //endregion
                                bundle.putInt(Constant.KEY_PRIMARY_KEY, vuCrmAccounts?.accountId!!)

                                fragment.arguments = bundle
                                fragment.setTargetFragment(
                                    this,
                                    Constant.REQUEST_CODE_EXAMPLE
                                )
                                mListener?.showToolbarBackButton(R.string.title_agreement_list)
                                mListener?.addFragment(fragment, true)
                            }
                            validateView() -> {
                                //checkPhoneNumberExist(view)
                                saveBusiness(getPayload(), view)
                            }
                            else -> {

                            }
                        }

                    }

                    else -> {

                    }
                }
            }
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
    }

    override fun onDetach() {
        mListener = null
        mHelper?.disconnect()
        super.onDetach()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mHelper?.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        localScreenMode?.let {
            mListener?.screenMode = it
        }
        if (MyApplication.getPrefHelper()
                .getAsPerAgentType() && (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD || fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS) && hideEditButtton && isChangedToEditMode
        ) {
            isChangedToEditMode = false
            mListener?.screenMode = Constant.ScreenMode.VIEW
        }
        mHelper?.onActivityResult(requestCode, resultCode)
        getBusinessDueSummaryDetails()
        fetchChildEntriesCount()
    }

    private fun setViews() {
        if (businessMode == Constant.BusinessMode.BusinessActivate) {
            mBinding.llVerifyReject.show()
            mBinding.llEditSave.hide()
        } else {
            mBinding.llVerifyReject.hide()
            mBinding.llEditSave.show()
        }
        when (fromScreen) {
            Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> {
                mBinding.tilEstimatedTaxAmount.visibility = View.GONE
                mBinding.llStatus.visibility = View.GONE
            }

            else -> {
                //Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS
            }
        }
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        APICall.getCorporateOfficeLOVValues(
            "CRM_Accounts",
            object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    try {
                        mResponseCountriesList = response.countryMaster
                        mResponseStatesList = response.stateMaster
                        mResponseCitiesList = response.cityMaster
                        mResponseZonesList = response.zoneMaster
                        mResponseSectorsList = response.sectors

                        filterCountries()

                        statusCodesList = response.statusCodes
                        organizationList = response.organizations
                        businessTypeList = response.businessTypes
                        activityDomainList = response.activityDomain
                        hotelDesFinancesList = response.hotelDesFinances

                        if (statusCodesList.isNullOrEmpty())
                            mBinding.spnStatus.adapter = null
                        else {
                            val statusAdapter = ArrayAdapter<COMStatusCode>(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                statusCodesList!!
                            )
                            mBinding.spnStatus.adapter = statusAdapter
                            //region select In active status by default
                            var pos = 0
                            for ((index, obj) in statusCodesList!!.withIndex()) {
                                if (Constant.OrganizationStatus.INACTIVE.value == obj.statusCode) {
                                    pos = index
                                    break
                                }
                            }
                            mBinding.spnStatus.setSelection(pos, true)
                            //endregion
                        }

                        if (hotelDesFinancesList.isNullOrEmpty())
                            mBinding.spnHotelDesFinance.adapter = null
                        else {
                            hotelDesFinancesList?.add(
                                0,
                                COMHotelDesFinances(0,null,getString(R.string.select))
                            )
                            val hotelDesFinanceAdapter = ArrayAdapter<COMHotelDesFinances>(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                hotelDesFinancesList!!
                            )
                            mBinding.spnHotelDesFinance.adapter = hotelDesFinanceAdapter
                            //region select In active status by default
                            var pos = 0
                            for ((index, obj) in hotelDesFinancesList!!.withIndex()) {
                                if (vuCrmAccounts?.hotelDesFinanceID == obj.hotelDesFinanceID) {
                                    pos = index
                                    break
                                }
                            }
                            mBinding.spnHotelDesFinance.setSelection(pos, true)
                            //endregion
                        }

                        if (mResponseCountriesList.isNotEmpty()) {
                            val countryCode: String? = "BFA"
                            val countries: MutableList<COMCountryMaster> = arrayListOf()
                            var index = -1
                            val telephonicCodes: ArrayList<Int> = arrayListOf()
                            for (country in mResponseCountriesList) {
                                country.telephoneCode?.let {
                                    if (it > 0) {
                                        countries.add(country)
                                        telephonicCodes.add(it)
                                        if (index <= -1 && countryCode == country.countryCode)
                                            index = countries.indexOf(country)
                                    }
                                }
                            }
                            if (index <= -1) index = 0
                            if (telephonicCodes.size > 0) {
                                val telephonicCodeArrayAdapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    telephonicCodes
                                )
                                mBinding.spnTelephoneCode.adapter = telephonicCodeArrayAdapter
                                if (vuCrmAccounts?.telephoneCode != null) {
                                    mBinding.spnTelephoneCode.setSelection(
                                        telephonicCodes.indexOf(
                                            vuCrmAccounts?.telephoneCode as Int
                                        )
                                    )
                                } else {
                                    mBinding.spnTelephoneCode.setSelection(index)
                                }
                            } else mBinding.spnTelephoneCode.adapter = null
                        }

                        activityDomainList = response.activityDomain
                        //activityClassList = response.activityClass

                        if (activityDomainList.isNullOrEmpty())
                            mBinding.spnActivityDomain.adapter = null
                        else {
                            activityDomainList?.add(
                                0,
                                CRMActivityDomain(getString(R.string.select), -1)
                            )
                            val domainAdapter = ArrayAdapter<CRMActivityDomain>(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                activityDomainList!!
                            )
                            mBinding.spnActivityDomain.adapter = domainAdapter
                        }


                        /**
                         *adding this to pre-populate the activity class with --select-- val
                         */
                        clearActivityClassAdapter()

                        organizationList?.add(0, VUCRMOrganization(getString(R.string.select), -1))

                        /*if (organizationList.isNullOrEmpty())
                                mBinding.spnParentBusiness.adapter = null
                            else {
                                organizationList?.add(0, VUCRMOrganization("-Select-", -1))
                                val organizationAdapter = ArrayAdapter<VUCRMOrganization>(requireContext(), android.R.layout.simple_list_item_1, organizationList!!)
                                mBinding.spnParentBusiness.adapter = organizationAdapter
                            }*/

                        if (businessTypeList.isNullOrEmpty()) {
                            mBinding.spnBusinessTypeSpin.setText("")
                        } else {
                            businessTypeList!!.forEachIndexed { index, crmCustomerSegments ->
                                if (crmCustomerSegments.defntn.equals("Y", ignoreCase = true)) {
                                    defaultBusinessIndex = index
                                    mBinding.spnBusinessTypeSpin.setText(crmCustomerSegments?.segment)
                                }
                            }
                            setEvents()
                            //val businessListAdapter = ArrayAdapter<CRMCustomerSegments>(requireContext(), android.R.layout.simple_list_item_1, businessTypeList!!)
                            /*if(defaultIndex == -1) {
                                businessListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                mBinding.spnBusinessType.adapter = NothingSelectedSpinnerAdapter(
                                        businessListAdapter,
                                        R.layout.contact_spinner_row_nothing_selected, //-1 for empty drop spinner
                                        requireContext())
                            }else{
                                mBinding.spnBusinessType.adapter = businessListAdapter
                            }*/

                        }
                    } catch (e: Exception) {
                        LogHelper.writeLog(exception = e)
                    }

                    bindData()

                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    //  mBinding.spnBusinessType.adapter = null
                    try {
                        mBinding.spnParentBusiness.text = MyApplication.getContext().getString(R.string.select)
                        mBinding.spnActivityDomain.adapter = null
                        mBinding.spnStatus.adapter = null

                        mBinding.spnCountry.adapter = null
                        mBinding.spnState.adapter = null
                        mBinding.spnCity.adapter = null
                        mBinding.spnZone.adapter = null
                        mBinding.spnSector.adapter = null
                    } catch (e: Exception) {
                        LogHelper.writeLog(exception = e)
                    }

                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
    }

    private fun fetchCRMActivityClassLOV(id: Int, fromBindData: Boolean = false) {
        val searchFilter = AdvanceSearchFilter()
        val filterList: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "ActivityDomainID"
        filterColumn.columnValue = id.toString()
        filterColumn.srchType = "equal"
        filterList.add(filterColumn)
        searchFilter.filterColumns = filterList

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "CRM_ActivityClasses"
        tableDetails.primaryKeyColumnName = "ActivityClassID"
        tableDetails.selectColoumns = "ActivityClassID,ActivityClass,ActivityClassCode,Active"
        tableDetails.TableCondition = ""
        tableDetails.sendCount = false
        tableDetails.initialTableCondition = ""
        searchFilter.tableDetails = tableDetails

        searchFilter.pageIndex = 1
        searchFilter.pageSize = 100

        mListener?.showProgressDialog()
        APICall.getTableOrViewData(
            searchFilter,
            object : ConnectionCallBack<GenericServiceResponse> {
                override fun onSuccess(response: GenericServiceResponse) {
                    mListener?.dismissDialog()
                    if (response.result?.activityClass?.size ?: 0 > 0) {
                        if(isActionEnabled) mBinding.edtActivityClassSpin.isEnabled = true
                        activityClassList = response.result?.activityClass

                        if (fromBindData) {
                            activityClassList?.let {
                                for ((index, obj) in activityClassList?.withIndex()!!) {
                                    if (vuCrmAccounts?.activityClassID == obj.ID) {
                                        mBinding.edtActivityClassSpin.setText(obj.name.toString())
                                        break
                                    }
                                }
                            }
                        }

                    } else {
                        clearActivityClassAdapter()
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    clearActivityClassAdapter()
                    // mListener?.showAlertDialogFailure(message, R.string.no_records_found, DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                }
            })
    }

    private fun clearActivityClassAdapter() {
        //activityClassList!!.clear()
        activityClassList = ArrayList()
        mBinding.edtActivityClassSpin.setText("")
        mBinding.edtActivityClassSpin.isEnabled = false
    }

    private fun bindData() {
        try {
            getBusinessDueSummaryDetails()
            mBinding.tvSycoTaxID.text = sycoTaxID
            vuCrmAccounts?.let { it ->

                vuCrmAccounts?.phone?.let {
                    if (!TextUtils.isEmpty(it)) {
                        mBinding.tilPhoneNumber.helperText =
                            getString(R.string.mobile_is_not_verified)
                        vuCrmAccounts?.phoneVerified?.let { phoneVerified ->
                            mBinding.tilPhoneNumber.helperText =
                                if (phoneVerified == "Y") getString(R.string.mobile_is_verified) else getString(
                                    R.string.mobile_is_not_verified
                                )
                        }
                    }
                }
                vuCrmAccounts?.email?.let {
                    if (!TextUtils.isEmpty(it)) {
                        mBinding.tilEmail.helperText = getString(R.string.email_is_not_verified)
                        vuCrmAccounts?.emailVerified?.let { emailVerified ->
                            mBinding.tilEmail.helperText =
                                if (emailVerified == "Y") getString(R.string.email_is_verified) else getString(
                                    R.string.email_is_not_verified
                                )
                        }
                    }
                }

                mBinding.edtOrganization.setText(vuCrmAccounts?.accountName)
                mBinding.edtPhoneNumber.setText(vuCrmAccounts?.phone)
                mBinding.edtEmail.setText(vuCrmAccounts?.email)
                mBinding.edtWebsite.setText(vuCrmAccounts?.website)
                mBinding.edtIFU.setText(vuCrmAccounts?.ifu)
                mBinding.edtTradeNo.setText(vuCrmAccounts?.tradeNo)
                mBinding.tvSycoTaxID.text = vuCrmAccounts?.sycoTaxID.toString()
                mBinding.edtRemarks.setText(vuCrmAccounts?.remarks)

                it.estimatedTax.let {
                    mBinding.edtEstimatedTaxAmount.setText(formatWithPrecision(it.toString()))
                }

                mBinding.edtLatitude.setText(vuCrmAccounts?.latitude)
                mBinding.edtLongitude.setText(vuCrmAccounts?.longitude)

                statusCodesList?.let {
                    for ((index, obj) in statusCodesList?.withIndex()!!) {
                        if (vuCrmAccounts?.statusCode?.contentEquals(obj.statusCode.toString()) == true) {
                            mBinding.spnStatus.setSelection(index)
                            break
                        }
                    }
                }

                vuCrmAccounts?.parentOrganizationID?.let {
                    organizationList?.let {
                        for ((index, obj) in organizationList?.withIndex()!!) {
                            if (vuCrmAccounts?.parentOrganizationID!! == obj.organizationID) {
                                mBinding.spnParentBusiness.text = (obj.organization)
                                break
                            }
                        }
                    }
                }


                businessTypeList?.let {
                    for ((index, obj) in businessTypeList?.withIndex()!!) {
                        if (vuCrmAccounts?.segmentId == obj.segmentId) {
                            // mBinding.spnBusinessType.setSelection(index)
                            mBinding.spnBusinessTypeSpin.setText(obj.segment.toString())
                            break;
                        }
                    }
                }

                getExistingAddress(vuCrmAccounts?.geoAddressID)

                activityDomainList?.let {
                    for ((index, obj) in activityDomainList?.withIndex()!!) {
                        if (vuCrmAccounts?.activityDomainID == obj.ID) {
                            mBinding.spnActivityDomain.setSelection(index)
                            /***
                             * Calling this to fecth the list of activtyclass
                             */
                            fetchCRMActivityClassLOV(obj!!.ID, true)
                            break
                        }
                    }
                }

                activityClassList?.let {
                    for ((index, obj) in activityClassList?.withIndex()!!) {
                        if (vuCrmAccounts?.activityClassID == obj.ID) {
                            mBinding.edtActivityClassSpin.setText(obj.name.toString())
                            break
                        }
                    }
                }
                /*Todo once business is created, any user can not change business status from app side */
                mBinding.spnStatus.isEnabled = false
            }
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
    }

    private fun getExistingAddress(geoAddressID: Int?) {
        val searchFilter = OwnerSearchFilter()
        searchFilter.pageSize = pageSize
        searchFilter.pageIndex = pageIndex
        searchFilter.query = null

        val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "GeoAddressID"
        filterColumn.columnValue = geoAddressID.toString()
        filterColumn.srchType = "equal"
        listFilterColumn.add(listFilterColumn.size, filterColumn)
        searchFilter.filterColumns = listFilterColumn
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "VU_COM_GeoAddresses"
        tableDetails.primaryKeyColumnName = "GeoAddressID"
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = "OR"

        searchFilter.tableDetails = tableDetails

        APICall.getBusinessAddress(
            searchFilter,
            object : ConnectionCallBack<BusinessAddressResponse> {
                override fun onSuccess(response: BusinessAddressResponse) {
                    response.results.businessOwner.get(0).let {
                        mGeoAddress = it
                        if (context == null){
                            return
                        }
                        if (mGeoAddress != null) {
                            mBinding.edtStreet.setText(mGeoAddress!!.street)
                            mBinding.edtZipCode.setText(mGeoAddress!!.zipCode)
                            mBinding.edtPlot.setText(mGeoAddress!!.plot)
                            mBinding.edtBlock.setText(mGeoAddress!!.block)
                            mBinding.edtDoorNo.setText(mGeoAddress!!.doorNo)
                            mBinding.edtDescription.setText(mGeoAddress!!.description)
                            filterCountries()
                        } /*else
                    {
                        mBinding.spnCountry.adapter = null
                        mBinding.spnState.adapter = null
                        mBinding.spnCity.adapter = null
                        mBinding.spnZone.adapter = null
                        mBinding.spnSector.adapter = null

                        mBinding.edtStreet.setText("")
                        mBinding.edtZipCode.setText("")
                        mBinding.edtPlot.setText("")
                        mBinding.edtBlock.setText("")
                        mBinding.edtDoorNo.setText("")
                        mBinding.edtDescription.setText("")
                    }*/

                    }

                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })

    }

    fun bindLatLongs(latitude: Double?, longitude: Double?) {
        mBinding.edtLatitude.setText(latitude.toString())
        mBinding.edtLongitude.setText(longitude.toString())
    }

    private val emailWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (vuCrmAccounts?.emailVerified != null && vuCrmAccounts?.emailVerified != "Y") {
                mBinding.tilEmail.helperText = getString(R.string.email_is_not_verified)
                return
            }
            vuCrmAccounts?.email?.let {
                if (!TextUtils.isEmpty(it)) {
                    val chars = s?.toString()
                    if (it != chars)
                        mBinding.tilEmail.helperText = getString(R.string.email_is_not_verified)
                    else
                        mBinding.tilEmail.helperText = getString(R.string.email_is_verified)
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private val mobileWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (vuCrmAccounts?.phoneVerified != null && vuCrmAccounts?.phoneVerified != "Y") {
                mBinding.tilPhoneNumber.helperText = getString(R.string.mobile_is_not_verified)
                return
            }
            vuCrmAccounts?.phone?.let {
                if (!TextUtils.isEmpty(it)) {
                    val chars = s?.toString()
                    if (it != chars)
                        mBinding.tilPhoneNumber.helperText =
                            getString(R.string.mobile_is_not_verified)
                    else
                        mBinding.tilPhoneNumber.helperText = getString(R.string.mobile_is_verified)
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun setListeners() {
        mBinding.llWeaponTax.setOnClickListener(this)
        mBinding.llCartTax.setOnClickListener(this)
        mBinding.llGameMachineTax.setOnClickListener(this)
        mBinding.llTaxes.setOnClickListener(this)
        mBinding.llBusinessOwner.setOnClickListener(this)
        mBinding.btnEdit.setOnClickListener(this)
        mBinding.llNotes.setOnClickListener(this)
        mBinding.llDocuments.setOnClickListener(this)
        mBinding.ivAddLocation.setOnClickListener(this)
        mBinding.spnParentBusiness.setOnClickListener(this)
        mBinding.llOutstandings.setOnClickListener(this)
        mBinding.llPropertyTax.setOnClickListener(this)
        mBinding.llLandTax.setOnClickListener(this)
        mBinding.llListDueNotice.setOnClickListener(this)
        mBinding.llAgreementList.setOnClickListener(this)

        mBinding.edtEmail.addTextChangedListener(emailWatcher)
        mBinding.edtPhoneNumber.addTextChangedListener(mobileWatcher)

        mBinding.spnCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var country: COMCountryMaster? = COMCountryMaster()
                if (p0 != null && p0.selectedItem != null)
                    country = p0.selectedItem as COMCountryMaster
                filterStates(country?.countryCode)
            }
        }

        mBinding.spnState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var state: COMStateMaster? = COMStateMaster()
                if (p0 != null && p0.selectedItem != null)
                    state = p0.selectedItem as COMStateMaster
                filterCities(state?.stateID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var city: VUCOMCityMaster? = VUCOMCityMaster()
                if (p0 != null && p0.selectedItem != null)
                    city = p0.selectedItem as VUCOMCityMaster
                filterZones(city?.cityID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterSectors(zone?.zoneID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnActivityDomain.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    var crmActivityDomain: CRMActivityDomain? =
                        CRMActivityDomain(getString(R.string.select), -1)
                    if (p0 != null && p0.selectedItem != null)
                        crmActivityDomain = p0.selectedItem as CRMActivityDomain

                    //Log.e("crmact domian is",">>>>>>>>>>>> ${crmActivityDomain!!.name},  ${crmActivityDomain!!.ID}")
                    fetchCRMActivityClassLOV(crmActivityDomain!!.ID)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        mBinding.tvFixedExpenses.setOnClickListener {
            if (mBinding.llFixedExpenses.visibility == VISIBLE) {
                mBinding.llFixedExpenses.visibility = GONE
                mBinding.tvFixedExpenses.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_down,
                    0
                )
                mBinding.cardView.requestFocus()
                mBinding.view.clearFocus()
            } else {
                mBinding.llFixedExpenses.visibility = VISIBLE
                mBinding.tvFixedExpenses.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_up,
                    0
                )
                mBinding.view.requestFocus()
                mBinding.cardView.clearFocus()
            }
        }

        mBinding.btnReject.setOnClickListener {

            val layoutInflater = LayoutInflater.from(context)
            val binding = DataBindingUtil.inflate<FragmentRejectionRemarksBinding>(layoutInflater, R.layout.fragment_rejection_remarks, null, false)
            mListener?.showAlertDialog(
                R.string.remarks,
                R.string.save,
                View.OnClickListener {
                    val dialog = (it as Button).tag as AlertDialog
                    if (binding.edtRemarks.text.toString().isNotEmpty()) {
                        dialog.dismiss()
                        rejectRemarks = binding.edtRemarks.text.toString()
                        isForReject = true
                        mBinding.btnSave.performClick()
                    } else {
                        dialog.dismiss()
                        mListener?.showSnackbarMsg(getString(R.string.msg_enter_remarks))
                    }
                },
                R.string.cancel,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                0,
                View.OnClickListener
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                },
                binding.root
            )
/*
            mListener?.showAlertDialog(resources.getString(R.string.remarks), DialogInterface.OnClickListener { dialog, _ ->
                if (binding.edtRemarks.text.toString().isNotEmpty()) {
                    */
/*mStoreCustomerB2B?.organization?.statusCode = Constant.OrganizationStatus.REJECTED.value
                    mStoreCustomerB2B?.organization?.status = getString(R.string.rejected)
                    mStoreCustomerB2B?.organization?.remarks = binding.edtRemarks.text.toString()*//*

                    dialog.dismiss()
//                    saveBusinessSummaryDetails(v)
                    rejectRemarks = binding.edtRemarks.text.toString()
                    isForReject = true
                    mBinding.btnSave.performClick()
                } else {
                    mListener?.showSnackbarMsg(getString(R.string.msg_enter_remarks))
                }
            }, null, binding.root)
*/
        }
        mBinding.btnActivate.setOnClickListener {
            isForReject = false
            mBinding.btnSave.performClick()
        }
        mBinding.btnSave.setOnClickListener {
            val payload = getPayload()
            if (businessMode == Constant.BusinessMode.BusinessActivate) {
                payload.organization?.statusCode = if (isForReject) Constant.OrganizationStatus.REJECTED.value else Constant.OrganizationStatus.ACTIVE.value
                payload.organization?.status = if (isForReject) getString(R.string.rejected) else getString(R.string.active)
                if (isForReject){
                    payload.organization?.remarks = rejectRemarks
                }
            }
            when {
                validateView() -> {
                    //checkPhoneNumberExist(view)
                    saveBusiness(payload, mBinding.btnSave)
                }
                else -> {

                }
            }
        }
    }

    private fun filterCountries() {
        val countries: MutableList<COMCountryMaster> = java.util.ArrayList()
        var index = -1
        var countryCode: String? = "BFA"
        if (mGeoAddress != null) countryCode = mGeoAddress!!.countryCode
        for (country in mResponseCountriesList) {
            countries.add(country)
            if (index <= -1 && countryCode != null && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode) index =
                countries.indexOf(country)
        }
        if (index <= -1) index = 0
        if (countries.size > 0) {
            val countryMasterArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                countries
            )
            mBinding.spnCountry.adapter = countryMasterArrayAdapter
            mBinding.spnCountry.setSelection(index)
            filterStates(countries[index].countryCode)
        } else {
            mBinding.spnCountry.adapter = null
            filterStates(countryCode)
        }
    }

    private fun filterStates(countryCode: String?) {
        var states: MutableList<COMStateMaster> = java.util.ArrayList()
        var index = -1
        var stateID = 100497
        if (mGeoAddress != null && mGeoAddress!!.stateID != null) stateID = mGeoAddress!!.stateID!!
        if (TextUtils.isEmpty(countryCode)) states = java.util.ArrayList() else {
            for (state in mResponseStatesList) {
                if (countryCode == state.countryCode) states.add(state)
                if (index <= -1 && stateID != 0 && state.stateID != null && stateID == state.stateID) index =
                    states.indexOf(state)
            }
        }
        if (index <= -1) index = 0
        if (states.size > 0) {
            val stateArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                states
            )
            mBinding.spnState.adapter = stateArrayAdapter
            mBinding.spnState.setSelection(index)
            filterCities(states[index].stateID!!)
        } else {
            mBinding.spnState.adapter = null
            filterCities(stateID)
        }
    }

    private fun filterCities(stateID: Int) {
        var cities: MutableList<VUCOMCityMaster> = java.util.ArrayList()
        var index = -1
        var cityID = 100312093
        if (mGeoAddress != null && mGeoAddress!!.cityID != null) cityID = mGeoAddress!!.cityID!!
        if (stateID <= 0) cities = java.util.ArrayList() else {
            for (city in mResponseCitiesList) {
                if (city.stateID != null && stateID == city.stateID) cities.add(city)
                if (index <= 0 && cityID != 0 && city.cityID != null && cityID == city.cityID) index =
                    cities.indexOf(city)
            }
        }
        if (index <= -1) index = 0
        if (cities.size > 0) {
            val cityArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                cities
            )
            mBinding.spnCity.adapter = cityArrayAdapter
            mBinding.spnCity.setSelection(index)
            filterZones(cities[index].cityID!!)
        } else {
            mBinding.spnCity.adapter = null
            filterZones(cityID)
        }
    }

    private fun filterZones(cityID: Int) {
        var zones: MutableList<COMZoneMaster> = java.util.ArrayList()
        var index = 0
        var zoneName: String? = ""
        if (mGeoAddress != null && mGeoAddress!!.zone != null) zoneName = mGeoAddress!!.zone
        if (cityID <= 0) zones = java.util.ArrayList() else {
            for (zone in mResponseZonesList) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone) index =
                    zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnZone.adapter = zoneArrayAdapter
            mBinding.spnZone.setSelection(index)
            filterSectors(zones[index].zoneID!!)
        } else {
            mBinding.spnZone.adapter = null
            filterSectors(0)
        }
    }

    private fun filterSectors(zoneID: Int) {
        var sectors: MutableList<COMSectors?> = java.util.ArrayList()
        var index = 0
        var sectorID = 0
        if (mGeoAddress != null && mGeoAddress!!.sectorID != null) sectorID =
            mGeoAddress!!.sectorID!!
        if (zoneID <= 0) sectors = java.util.ArrayList() else {
            for (sector in mResponseSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId) index =
                    sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            if (isActionEnabled) mBinding.spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                sectors
            )
            mBinding.spnSector.adapter = sectorArrayAdapter
            mBinding.spnSector.setSelection(index)
        } else {
            mBinding.spnSector.adapter = null
            mBinding.spnSector.isEnabled = false
        }
    }


    private fun checkPhoneNumberExist(view: View) {
        if (vuCrmAccounts != null && vuCrmAccounts?.accountId != null && vuCrmAccounts?.organizationId != null &&
            vuCrmAccounts?.accountId != 0 && vuCrmAccounts?.organizationId != 0 && vuCrmAccounts?.phone == mBinding.edtPhoneNumber.text.toString()
        ) {
            saveBusiness(getPayload(), view)
        } else {
            mListener?.showProgressDialog()

            val number = mBinding.edtPhoneNumber.text.toString().trim()

            val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
            var filterColumn = FilterColumn()
            val getChildTabCount = GetChildTabCount()
            val searchFilter = SearchFilter()

            filterColumn.columnName = "AccountTypeCode"
            filterColumn.columnValue = Constant.AccountTypeCode.CRO.name
            filterColumn.srchType = "equal"

            listFilterColumn.add(listFilterColumn.size, filterColumn)

            filterColumn = FilterColumn()
            filterColumn.columnName = "Number"
            filterColumn.columnValue = number
            filterColumn.srchType = "equal"

            listFilterColumn.add(listFilterColumn.size, filterColumn)

            searchFilter.filterColumns = listFilterColumn

            val tableDetails = TableDetails()
            tableDetails.tableOrViewName = "VU_CRM_AccountPhones"
            tableDetails.primaryKeyColumnName = "AccountPhoneID"
            tableDetails.selectColoumns = ""
            tableDetails.TableCondition = "AND"
            tableDetails.sendCount = true

            searchFilter.tableDetails = tableDetails

            getChildTabCount.advanceSearchFilter = searchFilter

            APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
                override fun onSuccess(response: Int) {
                    mListener?.dismissDialog()
                    if (response == 0)
                        saveBusiness(getPayload(), view)
                    else mListener?.showAlertDialog(getString(R.string.msg_nuber_exists))
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    mListener?.showAlertDialog(message)
                }
            })
        }
    }

    private fun validateView(): Boolean {

        if (mBinding.edtOrganization.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.tax_business_name))
            return false
        }
        if (mBinding.edtStreet.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.street))
            return false
        }

        /* if (mBinding.edtPhoneNumber.text.toString().trim().isEmpty()) {
             mListener?.showSnackbarMsg(getString(R.string.msg_provide_valid_telephone))
             return false
         }*/
        /*if (mBinding.edtEmail.text.toString().trim().isEmpty()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.email))
            return false
        }*/

        val email = mBinding.edtEmail.text.toString().trim()
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide_valid) + " " + getString(R.string.email))
            return false
        }

        if (mBinding.spnCountry.selectedItem == null) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.country))
            return false
        }
        if (mBinding.spnState.selectedItem == null) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.state))
            return false
        }
        if (mBinding.spnCity.selectedItem == null) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.city))
            return false
        }
        if (mBinding.spnZone.selectedItem == null) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.zone))
            return false
        }
        if (mBinding.spnSector.selectedItem == null) {
            mListener?.showSnackbarMsg(getString(R.string.msg_provide) + " " + getString(R.string.sector))
            return false
        }

        return true
    }

    private fun getPayload(): StoreCustomerB2B {
        val storeCustomerB2B = StoreCustomerB2B()
        try {
            val organization = Organization()

            organization.organization = mBinding.edtOrganization.text.toString().trim()

            if (mBinding.edtIFU.text != null && !TextUtils.isEmpty(mBinding.edtIFU.text.toString()))
                organization.ifu = mBinding.edtIFU.text.toString().trim()
            if (mBinding.edtTradeNo.text != null && !TextUtils.isEmpty(mBinding.edtTradeNo.text.toString()))
                organization.tradeNo = mBinding.edtTradeNo.text.toString().trim()
            if (mBinding.edtPhoneNumber.text != null && !TextUtils.isEmpty(mBinding.edtPhoneNumber.text.toString()))
                organization.phone = mBinding.edtPhoneNumber.text.toString().trim()
            if (mBinding.edtEmail.text != null && !TextUtils.isEmpty(mBinding.edtEmail.text.toString()))
                organization.email = mBinding.edtEmail.text.toString().trim()
            if (mBinding.edtWebsite.text != null && !TextUtils.isEmpty(mBinding.edtWebsite.text.toString()))
                organization.webSite = mBinding.edtWebsite.text.toString().trim()
            if (mBinding.tvSycoTaxID.text != null && !TextUtils.isEmpty(mBinding.tvSycoTaxID.text.toString()))
                organization.sycotaxID = mBinding.tvSycoTaxID.text.toString().trim()
            if (mBinding.edtRemarks.text != null && !TextUtils.isEmpty(mBinding.edtRemarks.text.toString()))
                organization.remarks = mBinding.edtRemarks.text.toString().trim()
            if (mBinding.spnTelephoneCode.selectedItem != null)
                organization.telCode = mBinding.spnTelephoneCode.selectedItem.toString()

            if (mBinding.spnHotelDesFinance.selectedItem != null) {
                val hotelDesFianance: COMHotelDesFinances =
                    mBinding.spnHotelDesFinance.selectedItem as COMHotelDesFinances
                    for (obj in hotelDesFinancesList!!.iterator()) {
                        if (hotelDesFianance.hotelDesFinanceID == obj.hotelDesFinanceID) {
                            organization.hotelDesFinanceID = obj.hotelDesFinanceID
                            break
                        }
                    }
            }
            if (mBinding.spnStatus.selectedItem != null) {
                val statusCode: COMStatusCode = mBinding.spnStatus.selectedItem as COMStatusCode
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
                    organization.statusCode = Constant.OrganizationStatus.INACTIVE.value
                    for (obj in statusCodesList!!.iterator()) {
                        if (Constant.OrganizationStatus.INACTIVE.value == obj.statusCode) {
                            organization.status = obj.status
                            break
                        }
                    }
                } else {
                    organization.statusCode = statusCode.statusCode
                    organization.status = statusCode.status
                }
            }

            if (mBinding.spnBusinessTypeSpin.text != null) {
                if (mBinding.spnBusinessTypeSpin.text.toString() != getString(R.string.select)) {
                    organization.segmentId =
                        getBusinessID(mBinding.spnBusinessTypeSpin.text.toString())
                }
            }

            val selectedActivityDomain = mBinding.spnActivityDomain.selectedItem
            if (selectedActivityDomain != null) {
                if ((selectedActivityDomain as CRMActivityDomain).ID != -1) {
                    organization.activityDomainID = selectedActivityDomain.ID
                    organization.activityDomainName = selectedActivityDomain.name ?: ""
                }
            }

            val selectedActivityClass = mBinding.edtActivityClassSpin.text
            if (selectedActivityClass != null) {
                if (!selectedActivityClass.isEmpty() && !selectedActivityClass.equals(getString(R.string.select))) {
                    organization.activityClassID =
                        getActivityClassID(mBinding.edtActivityClassSpin.text.toString())
                    organization.activityClassName = mBinding.edtActivityClassSpin.text.toString()
                }
            }

            /*if (mBinding.spnParentBusiness.selectedItem != null) {
                    val parentOrganization: VUCRMOrganization = mBinding.spnParentBusiness.selectedItem as VUCRMOrganization
                    if (parentOrganization.organizationID != -1)
                        organization.parentOrganizationID = parentOrganization.organizationID
                }*/

            if (mBinding.spnParentBusiness.text != null && mBinding.spnParentBusiness.text.isNotEmpty() && mBinding.spnParentBusiness.text != getString(
                    R.string.select
                )
            ) {
                organizationList?.let {
                    mBinding.spnParentBusiness.text?.let {
                        for (organisationList in organizationList!!) {
                            if (organisationList.organization == mBinding.spnParentBusiness.text) {
                                organization.parentOrganizationID = organisationList.organizationID
                            }
                        }
                    }
                }
            }

            if (mBinding.edtLatitude.text.toString().trim().isNotEmpty()) {
                organization.latitude = mBinding.edtLatitude.text.toString().trim().toDouble()
            }

            if (mBinding.edtLongitude.text.toString().trim().isNotEmpty()) {
                organization.longitude = mBinding.edtLongitude.text.toString().trim().toDouble()
            }

            if (vuCrmAccounts?.accountId != null && vuCrmAccounts?.accountId != 0)
                organization.accountID = vuCrmAccounts?.accountId!!
            if (vuCrmAccounts?.organizationId != null && vuCrmAccounts?.organizationId != 0)
                organization.organizationID = vuCrmAccounts?.organizationId!!
            if (vuCrmAccounts?.geoAddressID != null && vuCrmAccounts?.geoAddressID != 0)
                organization.geoAddressID = vuCrmAccounts?.geoAddressID

            organization.emailVerified = vuCrmAccounts?.emailVerified
            organization.phoneVerified = vuCrmAccounts?.phoneVerified

            vuCrmAccounts?.email?.let {
                if (mBinding.edtEmail.text != null && it != mBinding.edtEmail.text.toString())
                    organization.emailVerified = "N"
            }
            vuCrmAccounts?.phone?.let {
                if (mBinding.edtPhoneNumber.text != null && it != mBinding.edtPhoneNumber.text.toString())
                    organization.phoneVerified = "N"
            }

            organization.createdByAccountId = prefHelper.accountId

            storeCustomerB2B.organization = organization
            if (!TextUtils.isEmpty(mBinding.edtLatitude.text.toString()) && !TextUtils.isEmpty(
                    mBinding.edtLongitude.text.toString()
                )
            )
                storeCustomerB2B.geoAddress = prepareData(
                    mBinding.edtLatitude.text.toString().trim().toDouble(),
                    mBinding.edtLongitude.text.toString().trim().toDouble()
                )
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
        return storeCustomerB2B
    }

    private fun prepareData(latitude: Double, longitude: Double): GeoAddress {
        val geoAddress = GeoAddress()

        geoAddress.geoAddressID = vuCrmAccounts?.geoAddressID
        geoAddress.accountId = vuCrmAccounts?.accountId

        // region Spinner Data
        val countryMaster = mBinding.spnCountry.selectedItem as COMCountryMaster
        if (countryMaster.countryCode != null) {
            geoAddress.countryCode = countryMaster.countryCode
            geoAddress.country = countryMaster.country
        }
        val comStateMaster = mBinding.spnState.selectedItem as COMStateMaster?
        if (comStateMaster?.state != null) {
            geoAddress.state = comStateMaster.state
            geoAddress.stateID = comStateMaster.stateID
        }
        val comCityMaster = mBinding.spnCity.selectedItem as VUCOMCityMaster?
        if (comCityMaster?.city != null) {
            geoAddress.city = comCityMaster.city
            geoAddress.cityID = comCityMaster.cityID
        }
        val comZoneMaster = mBinding.spnZone.selectedItem as COMZoneMaster?
        if (comZoneMaster?.zone != null) geoAddress.zone = comZoneMaster.zone
        val comSectors = mBinding.spnSector.selectedItem as COMSectors?
        if (comSectors?.sectorId != null) {
            geoAddress.sectorID = comSectors.sectorId
            geoAddress.sector = comSectors.sector
        }
        // endregion
        if (mBinding.edtStreet.text != null && !TextUtils.isEmpty(mBinding.edtStreet.text.toString())) geoAddress.street =
            mBinding.edtStreet.text.toString().trim { it <= ' ' }
        if (mBinding.edtZipCode.text != null && !TextUtils.isEmpty(mBinding.edtZipCode.text.toString())) geoAddress.zipCode =
            mBinding.edtZipCode.text.toString().trim { it <= ' ' }
        if (mBinding.edtPlot.text != null && !TextUtils.isEmpty(mBinding.edtPlot.text.toString())) geoAddress.plot =
            mBinding.edtPlot.text.toString().trim { it <= ' ' }
        if (mBinding.edtBlock.text != null && !TextUtils.isEmpty(mBinding.edtBlock.text.toString())) geoAddress.block =
            mBinding.edtBlock.text.toString().trim { it <= ' ' }
        if (mBinding.edtDoorNo.text != null && !TextUtils.isEmpty(
                mBinding.edtDoorNo.text.toString().trim { it <= ' ' })
        ) geoAddress.doorNo = mBinding.edtDoorNo.text.toString().trim { it <= ' ' }
        if (mBinding.edtDescription.text != null && !TextUtils.isEmpty(mBinding.edtDescription.text.toString())) geoAddress.description =
            mBinding.edtDescription.text.toString().trim { it <= ' ' }
        geoAddress.latitude = "$latitude"
        geoAddress.longitude = "$longitude"

        return geoAddress
    }

    private fun getBusinessID(toString: String): Int? {
        businessTypeList?.let{
            for (obj in it) {
                if (obj.segment == toString) {
                    return obj.segmentId
                }
            }
        }
        return null
    }


    private fun getActivityClassID(toString: String): Int? {
        for (obj in activityClassList!!) {
            if (obj.name == toString) {
                return obj.ID
            }
        }
        return null
    }


    private fun saveBusiness(storeCustomerB2B: StoreCustomerB2B, view: View?) {
//        Log.e("geoaddress ", ">>" + prepareData(17.4540616, 78.4320739))
        setHasOptionsMenu(false)
        mListener?.showProgressDialog()
        mBinding.btnSave.isEnabled = false
        /*if (view?.id == R.id.btnSave) {
            val testLog = Gson().toJson(storeCustomerB2B)
            LogHelper.writeLog(exception = null, message = "api/CRM/StoreCustomerB2B Payload->")
            LogHelper.writeLog(exception = null, message = testLog)
        }*/
        APICall.storeCustomerB2B(storeCustomerB2B, object : ConnectionCallBack<TaxPayerResponse> {
            override fun onSuccess(response: TaxPayerResponse) {
                /*if (view?.id == R.id.btnSave) {
                    val testLogResponse = Gson().toJson(response)
                    LogHelper.writeLog(exception = null, message = "api/CRM/StoreCustomerB2B Res->")
                    LogHelper.writeLog(exception = null, message = testLogResponse)
                }*/
                mListener?.dismissDialog()
                try {
                    ObjectHolder.clearAll() //clear object holder

                    //region Create new VuCrmAccount()
                    if (vuCrmAccounts == null)
                        vuCrmAccounts = VUCRMAccounts()
                    vuCrmAccounts?.sycoTaxID = sycoTaxID
                    vuCrmAccounts?.accountId = response.accountID
                    vuCrmAccounts?.organizationId = response.organizationID
                    vuCrmAccounts?.taskCodeList = response.taskCodeList
                    vuCrmAccounts?.phone = mBinding.edtPhoneNumber.text.toString()
                    vuCrmAccounts?.accountName = mBinding.edtOrganization.text.toString()
                    vuCrmAccounts?.email = mBinding.edtEmail.text.toString()
                    if (response.geoAddressID != 0)
                        vuCrmAccounts?.geoAddressID = response.geoAddressID
                    //endregion

                    ObjectHolder.registerBusiness.sycoTaxID = sycoTaxID
                    ObjectHolder.registerBusiness.vuCrmAccounts = vuCrmAccounts
                    this@BusinessEntryFragment.vuCrmAccounts = vuCrmAccounts

                    mBinding.btnSave.isEnabled = true

                    if (view?.id == R.id.btnSave) {
                        if (businessMode == Constant.BusinessMode.BusinessActivate) {
                            navigateToBusinessSummaryApprovalFragment(storeCustomerB2B)
                        } else {
                            if ((fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS || fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD)) {
                                if (storeCustomerB2B.organization?.phoneVerified == "N" || storeCustomerB2B.organization?.emailVerified == "N") {
                                    navigateToBusinessSummaryApprovalFragment(storeCustomerB2B)
                                    return
                                } else if ((vuCrmAccounts?.statusCode == storeCustomerB2B.organization?.statusCode || storeCustomerB2B.organization?.statusCode != Constant.OrganizationStatus.ACTIVE.value)) {
                                    if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS)
                                        mListener?.showSnackbarMsg(getString(R.string.msg_record_save_success))
                                    else if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
                                        mListener?.showSnackbarMsg(getString(R.string.msg_record_update_success))
                                    Handler().postDelayed({
                                        mListener?.popBackStack()
                                        mListener?.finish()
                                    }, 700)
                                    return
                                }
                            }

                            navigateToBusinessSummaryApprovalFragment(getPayload())
                        }
                    } else
                        onClick(view)
                } catch (e: Exception) {
                    LogHelper.writeLog(exception = e)
                }
            }

            override fun onFailure(message: String) {
//                LogHelper.writeLog(exception = null, message = message)
                mListener?.dismissDialog()
                try {
                    mListener?.showAlertDialog(message)
                    mBinding.btnSave.isEnabled = true
                } catch (e: Exception) {
                    LogHelper.writeLog(exception = e)
                }
            }
        })
    }

    private fun navigateToBusinessSummaryApprovalFragment(storeCustomerB2B: StoreCustomerB2B) {
        val fragment = BusinessSummaryApprovalFragment()
        //region SetArguments
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putSerializable(Constant.KEY_BUSINESS_MODE, businessMode)
        bundle.putParcelable(Constant.KEY_STORE_CUSTOMER_B2B, storeCustomerB2B)
        bundle.putBoolean(Constant.KEY_IS_FIRST, true)
        fragment.arguments = bundle
        //endregion

        mListener?.showToolbarBackButton(R.string.title_register_business)
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS)
            mListener?.showToolbarBackButton(R.string.title_update_business)
        mListener?.addFragment(fragment, true)
    }

    private fun getBusinessDueSummaryDetails() {
        mListener?.showProgressDialog()
        if (vuCrmAccounts != null) {
            vuCrmAccounts?.accountId?.let {
                APICall.getBusinessDueSummary(
                    it,
                    object : ConnectionCallBack<BusinessDueSummaryResults> {
                        override fun onSuccess(response: BusinessDueSummaryResults) {
                            mListener?.dismissDialog()
                            val businessDueSummary = response.businessDueSummary[0]
                            mBinding.txtInitialOutstandingCurrentYearDue.text =
                                formatWithPrecision(businessDueSummary.initialOutstandingCurrentYearDue)
                            mBinding.txtCurrentYearDue.text =
                                formatWithPrecision(businessDueSummary.currentYearDue)
                            mBinding.txtCurrentYearPenaltyDue.text =
                                formatWithPrecision(businessDueSummary.currentYearPenaltyDue)
                            mBinding.txtAnteriorYearDue.text =
                                formatWithPrecision(businessDueSummary.anteriorYearDue)
                            mBinding.txtAnteriorYearPenaltyDue.text =
                                formatWithPrecision(businessDueSummary.anteriorYearPenaltyDue)
                            mBinding.txtPreviousYearDue.text =
                                formatWithPrecision(businessDueSummary.previousYearDue)
                            mBinding.txtPreviousYearPenaltyDue.text =
                                formatWithPrecision(businessDueSummary.previousYearPenaltyDue)
                        }

                        override fun onFailure(message: String) {
                            mListener?.dismissDialog()
                            mListener?.showAlertDialog(message)
                        }

                    })
            }
        } else {
            mBinding.txtInitialOutstandingCurrentYearDue.text = "0.00"
            mBinding.txtCurrentYearDue.text = "0.00"
            mBinding.txtCurrentYearPenaltyDue.text = "0.00"
            mBinding.txtAnteriorYearDue.text = "0.00"
            mBinding.txtAnteriorYearPenaltyDue.text = "0.00"
            mBinding.txtPreviousYearDue.text = "0.00"
            mBinding.txtPreviousYearPenaltyDue.text = "0.00"
        }

    }

    private fun getCurrentLocation() {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS) {
            mHelper = LocationHelper(requireContext(), mBinding.btnSave, fragment = this)
            mHelper?.fetchLocation()
            mHelper?.setListener(object : LocationHelper.Location {
                override fun found(latitude: Double, longitude: Double) {
                    bindLatLongs(latitude, longitude)
                    mListener?.dismissDialog()
                }

                override fun start() {
                    mListener?.showProgressDialog(R.string.msg_location_fetching)
                }
            })
        }
    }

    private fun fetchCount(
        filterColumns: List<FilterColumn>,
        tableCondition: String,
        tableOrViewName: String,
        primaryKeyColumnName: String
    ) {
        val getChildTabCount = GetChildTabCount()
        val searchFilter = SearchFilter()

        searchFilter.filterColumns = filterColumns

        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = tableOrViewName
        tableDetails.primaryKeyColumnName = primaryKeyColumnName
        tableDetails.selectColoumns = ""
        tableDetails.TableCondition = tableCondition
        tableDetails.sendCount = true
        searchFilter.tableDetails = tableDetails

        getChildTabCount.advanceSearchFilter = searchFilter

        APICall.getChildTabCount(getChildTabCount, object : ConnectionCallBack<Int> {
            override fun onFailure(message: String) {
                bindCounts(tableOrViewName, 0)
            }

            override fun onSuccess(response: Int) {
                bindCounts(tableOrViewName, response)
            }
        })
    }

    private fun bindCounts(tableOrViewName: String, count: Int) {
        when (tableOrViewName) {
            "VU_CRM_TaxPayerAccountContacts" -> {
                mBinding.txtNumberOfBusinessOwners.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "TableName"
                filterColumn.columnValue = "CRM_Organizations"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue =
                    "${ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_DocumentReferences", "DocumentReferenceID")
                if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfBusinessOwners.text.toString()
                        .toInt() == 0
                ) {
                    mBinding.llBusinessOwner.isEnabled =
                        MyApplication.getPrefHelper().getAsPerAgentType()
                } else {
                    mBinding.llBusinessOwner.isEnabled = true
                }
            }
            "COM_DocumentReferences" -> {
                mBinding.txtNumberOfDocuments.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                var filterColumn = FilterColumn()
                filterColumn.columnName = "TableName"
                filterColumn.columnValue = "CRM_Organizations"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                filterColumn = FilterColumn()
                filterColumn.columnName = "PrimaryKeyValue"
                filterColumn.columnValue =
                    "${ObjectHolder.registerBusiness.vuCrmAccounts?.organizationId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(listFilterColumn, "AND", "COM_Notes", "NoteID")
                if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfDocuments.text.toString()
                        .toInt() == 0
                ) {
                    mBinding.llDocuments.isEnabled = false
                } else {
                    mBinding.llDocuments.isEnabled = true
                }
            }
            "COM_Notes" -> {
                mBinding.txtNumberOfNotes.text = "$count"
                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "CustomerID"
                filterColumn.columnValue =
                    "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(
                    listFilterColumn,
                    "OR",
                    "CRM_CustomerProductInterests",
                    "CustomerProductInterestID"
                )
                if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfNotes.text.toString()
                        .toInt() == 0
                ) {
                    mBinding.llNotes.isEnabled = false
                } else {
                    mBinding.llNotes.isEnabled = true
                }
            }
            "CRM_CustomerProductInterests" -> {
                mBinding.txtNumberOfTaxes.text = "$count"

                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                filterColumn.columnValue =
                    "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(
                    listFilterColumn,
                    "AND",
                    "VU_ACC_InitialOutstandings",
                    "InitialOutstandingID"
                )
                if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfTaxes.text.toString()
                        .toInt() == 0
                ) {
                    mBinding.llTaxes.isEnabled = false
                } else {
                    mBinding.llTaxes.isEnabled = true
                }
            }
            "VU_ACC_InitialOutstandings" -> {
                mBinding.txtNumberOfOutstandings.text = "$count"

                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                filterColumn.columnValue =
                    "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(
                    listFilterColumn,
                    "AND",
                    "ACC_DueNotices",
                    "DueNoticeID"
                )
                if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfOutstandings.text.toString()
                        .toInt() == 0
                ) {
                    mBinding.llOutstandings.isEnabled = false
                } else {
                    mBinding.llOutstandings.isEnabled = true
                }
            }
            "ACC_DueNotices" -> {
                mBinding.txtNumberOfDuenotices.text = "$count"

                val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
                val filterColumn = FilterColumn()
                filterColumn.columnName = "AccountID"
                filterColumn.columnValue =
                    "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId}"
                filterColumn.srchType = "equal"
                listFilterColumn.add(listFilterColumn.size, filterColumn)
                fetchCount(
                    listFilterColumn,
                    "AND",
                    "VU_ACC_DueAgreements",
                    "DueAgreementID"
                )

//                if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfDuenotices.text.toString()
//                        .toInt() == 0
//                ) {
//                    mBinding.llListDueNotice.isEnabled = false
//                } else {
//                    mBinding.llListDueNotice.isEnabled = true
//                }
            }
            "VU_ACC_DueAgreements" -> {
                mBinding.txtNumberOfAgreements.text = "$count"

//                if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfAgreements.text.toString()
//                        .toInt() == 0
//                ) {
//                    mBinding.llAgreementList.isEnabled = false
//                } else {
//                    mBinding.llAgreementList.isEnabled = true
//                }
            }

//            "CRM_CustomerProductInterests" -> {
//                mBinding.txtNumberOfTaxes.text = "$count"
//            }
        }
    }

    private fun setClickEnable() {
        mBinding.llWeaponTax.isEnabled = true
        mBinding.llCartTax.isEnabled = true
        mBinding.llGameMachineTax.isEnabled = true
        mBinding.llTaxes.isEnabled = true
        mBinding.llBusinessOwner.isEnabled = true
        mBinding.llDocuments.isEnabled = true
        mBinding.llNotes.isEnabled = true
        mBinding.llOutstandings.isEnabled = true
    }

    private fun setClickEnableByAgent() {
        mBinding.llWeaponTax.isEnabled = false
        mBinding.llCartTax.isEnabled = false
        mBinding.llGameMachineTax.isEnabled = false
        mBinding.llTaxes.isEnabled = false
        mBinding.llBusinessOwner.isEnabled = true
        mBinding.llDocuments.isEnabled = false
        mBinding.llNotes.isEnabled = false
        mBinding.llOutstandings.isEnabled = false
    }

    private fun fetchChildEntriesCount() {
        ObjectHolder.registerBusiness.vuCrmAccounts?.accountId?.let {
            val listFilterColumn: ArrayList<FilterColumn> = arrayListOf()
            val filterColumn = FilterColumn()
            filterColumn.columnName = "AccountID"
            filterColumn.columnValue = "${ObjectHolder.registerBusiness.vuCrmAccounts?.accountId}"
            filterColumn.srchType = "equal"
            listFilterColumn.add(listFilterColumn.size, filterColumn)
            fetchCount(listFilterColumn, "OR", "VU_CRM_TaxPayerAccountContacts", "AccountContactID")

            GetIndividualTaxCount4Business(it)

        }

    }

    private fun GetPropertyTaxCount4Business(primaryKey: Int) {
        mListener?.showProgressDialog()
        APICall.getPropertyTaxCount4Business(
            primaryKey,
            object : ConnectionCallBack<GetIndividualTaxCount> {
                override fun onSuccess(response: GetIndividualTaxCount) {
                    mListener?.dismissDialog()
                    mBinding.txtNumberOfPropertyTax.text = "0"
                    mBinding.txtNumberOfLandTax.text = "0"

                    response.propertyCount?.let {
                        mBinding.txtNumberOfPropertyTax.text = response.propertyCount.toString()
                    }
                    response.landCount?.let {
                        mBinding.txtNumberOfLandTax.text = response.landCount.toString()
                    }

                    if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfPropertyTax.text.toString()
                            .toInt() == 0
                    ) {
                        mBinding.llPropertyTax.isEnabled = false
                    } else {
                        mBinding.llPropertyTax.isEnabled = true
                    }

                    if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfLandTax.text.toString()
                            .toInt() == 0
                    ) {
                        mBinding.llLandTax.isEnabled = false
                    } else {
                        mBinding.llLandTax.isEnabled = true
                    }
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                }
            })
    }


    private fun GetIndividualTaxCount4Business(primaryKey: Int) {
        mListener?.showProgressDialog()
        APICall.getIndividualTaxCount4Business(
            primaryKey,
            object : ConnectionCallBack<GetIndividualTaxCount> {
                override fun onSuccess(response: GetIndividualTaxCount) {
                    mListener?.dismissDialog()
                    mBinding.txtNumberOfWeaponTax.text = "0"
                    mBinding.txtNumberOfCartTax.text = "0"
                    mBinding.txtNumberOfGameMachineTax.text = "0"

                    response.weaponsCount?.let {
                        mBinding.txtNumberOfWeaponTax.text = response.weaponsCount.toString()
                    }
                    response.cartCount?.let {
                        mBinding.txtNumberOfCartTax.text = response.cartCount.toString()
                    }
                    response.gamingCount?.let {
                        mBinding.txtNumberOfGameMachineTax.text = response.gamingCount.toString()
                    }

                    if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfWeaponTax.text.toString()
                            .toInt() == 0
                    ) {
                        mBinding.llWeaponTax.isEnabled = false
                    } else {
                        mBinding.llWeaponTax.isEnabled = true
                    }

                    if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfCartTax.text.toString()
                            .toInt() == 0
                    ) {
                        mBinding.llCartTax.isEnabled = false
                    } else {
                        mBinding.llCartTax.isEnabled = true
                    }
                    if (mListener?.screenMode == Constant.ScreenMode.VIEW && mBinding.txtNumberOfGameMachineTax.text.toString()
                            .toInt() == 0
                    ) {
                        mBinding.llGameMachineTax.isEnabled = false
                    } else {
                        mBinding.llGameMachineTax.isEnabled = true
                    }

                    GetPropertyTaxCount4Business(primaryKey)
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    GetPropertyTaxCount4Business(primaryKey)
                }
            })
    }

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showAlertDialogFailure(
            message: String,
            noRecordsFound: Int,
            onClickListener: DialogInterface.OnClickListener
        )

        fun showSnackbarMsg(message: String)
        fun showSnackbarMsg(message: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun popBackStack()
        fun showToast(title: Int)
        fun showToolbarBackButton(title: Int)
        fun showProgressDialog(message: Int)
        fun finish()
        var screenMode: Constant.ScreenMode
        fun showAlertDialog(
            message: Int,
            positiveButton: Int,
            positiveListener: View.OnClickListener,
            neutralButton: Int,
            neutralListener: View.OnClickListener,
            negativeButton: Int,
            negativeListener: View.OnClickListener,
            view: View
        )        fun showAlertDialog(msg: String, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener?, view: View)
    }

}