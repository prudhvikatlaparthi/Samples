package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityRegisterBusinessBinding
import com.sgs.citytax.model.ObjectHolder
import com.sgs.citytax.model.VUCRMAccounts
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class RegisterBusinessActivity : BaseActivity(), BusinessEntryFragment.Listener,
    BusinessSummaryApprovalFragment.Listener, TaxEntryFragment.Listener,
    BusinessOwnerMasterFragment.Listener, BusinessOwnerEntryFragment.Listener,
    BusinessOwnerSearchFragment.Listener, RentalMasterFragment.Listener,
    RentalEntryFragment.Listener, PhoneMasterFragment.Listener, PhoneEntryFragment.Listener,
    EmailMasterFragment.Listener, EmailEntryFragment.Listener,
    CorporateTurnOverMasterFragment.Listener, CorporateTurnOverFragment.Listener,
    AddressMasterFragment.Listener, AddressEntryFragment.Listener,
    PropertyOwnershipMasterFragment.Listener, PropertyOwnerFragment.Listener,
    VehicleOwnershipMasterFragment.Listener, VehicleOwnershipEntryFragment.Listener,
    ROPPDOMasterFragment.Listener, ROPPDOEntryFragment.Listener, NotesMasterFragment.Listener,
    NotesEntryFragment.Listener, DocumentsMasterFragment.Listener, DocumentEntryFragment.Listener,
    LocateDialogFragment.Listener, TaxMasterFragment.Listener, AdvertisementMasterFragment.Listener,
    AdvertisementEntryFragment.Listener, OutstandingsMasterFragment.Listener,
    OutstandingEntryFragment.Listener, WeaponTaxMasterFragment.Listener,
    CartTaxMasterFragment.Listener, GamingMachineTaxMasterFragment.Listener,
    PropertyTaxMasterFragment.Listener, ShowTaxMasterFragment.Listener,
    ShowTaxEntryFragment.Listener, HotelListFragment.Listener, HotelEntryFragment.Listener,
    LicenseMasterFragment.Listener, LicenseEntryFragment.Listener,
    VehicleOnBoardingMasterFragment.Listener, CardEntryFragment.Listener,
    CardMasterFragment.Listener, ListofDueNoticeMasterFragment.Listener,
    AgreementListMasterFragment.Listener, AgreementEntryFragment.Listener,
    DueDocumentsMasterFragment.Listener,DueNoticeImagesFragment.Listener,
    FragmentCommunicator {
    private lateinit var mBinding: ActivityRegisterBusinessBinding
    private var sycoTaxId = ""
    private var vuCrmAccount: VUCRMAccounts? = null
    private var fromScreen: Constant.QuickMenu? = null
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private val isAddMode = Constant.ScreenMode.ADD == screenMode
    private val isViewMode = Constant.ScreenMode.VIEW == screenMode
    private val isEditMode = Constant.ScreenMode.EDIT == screenMode

    lateinit var fragment: AgreementListMasterFragment
    private val businessEntryFragment = BusinessEntryFragment()
    private var businessMode: Constant.BusinessMode = Constant.BusinessMode.None
    private var hideEditButtton: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register_business)
        processIntent()
        setTitle()
        showBusinessEntryScreen()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmationDialog() {
        showAlertDialog(R.string.do_you_want_to_exit,
            R.string.yes,
            {
                val dialog = (it as Button).tag as AlertDialog
                dialog.dismiss()
                super.onBackPressed()
            },
            R.string.no,
            {
                val dialog = (it as Button).tag as AlertDialog
                dialog.dismiss()
            })
    }

    override fun onBackPressed() {
        when (currentFragment) {
            is BusinessEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is OutstandingEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is BusinessOwnerEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is CorporateTurnOverFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is PropertyOwnerFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is VehicleOwnershipEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is VehicleOnBoardingMasterFragment -> {
                (currentFragment as VehicleOnBoardingMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is ROPPDOEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is RentalEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is AdvertisementEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is ShowTaxEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is HotelEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            is LicenseEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }

            is AddressMasterFragment -> {
                (currentFragment as AddressMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is BusinessOwnerMasterFragment -> {
                (currentFragment as BusinessOwnerMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is VehicleOnBoardEntryFragment -> {
                setTitle()
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                }
            }
            is DocumentsMasterFragment -> {
                (currentFragment as DocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is NotesMasterFragment -> {
                (currentFragment as NotesMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is AgreementListMasterFragment -> {
                (currentFragment as AgreementListMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is CardMasterFragment -> {
                (currentFragment as CardMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is TaxMasterFragment -> {
                (currentFragment as TaxMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is WeaponTaxMasterFragment -> {
                (currentFragment as WeaponTaxMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is CartTaxMasterFragment -> {
                (currentFragment as CartTaxMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is GamingMachineTaxMasterFragment -> {
                (currentFragment as GamingMachineTaxMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is PhoneMasterFragment -> {
                (currentFragment as PhoneMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is EmailMasterFragment -> {
                (currentFragment as EmailMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is CorporateTurnOverMasterFragment -> {
                (currentFragment as CorporateTurnOverMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is PropertyOwnershipMasterFragment -> {
                (currentFragment as PropertyOwnershipMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is VehicleOwnershipMasterFragment -> {
                (currentFragment as VehicleOwnershipMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is ROPPDOMasterFragment -> {
                (currentFragment as ROPPDOMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is RentalMasterFragment -> {
                (currentFragment as RentalMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is AdvertisementMasterFragment -> {
                (currentFragment as AdvertisementMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is OutstandingsMasterFragment -> {
                (currentFragment as OutstandingsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is PropertyTaxMasterFragment -> {
                (currentFragment as PropertyTaxMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is ShowTaxMasterFragment -> {
                (currentFragment as ShowTaxMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is DueDocumentsMasterFragment -> {
                (currentFragment as DueDocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is DueNoticeImagesFragment -> {
                (currentFragment as DueNoticeImagesFragment).onBackPressed()
                super.onBackPressed()
            }
            is HotelListFragment -> {
                (currentFragment as HotelListFragment).onBackPressed()
                super.onBackPressed()
            }
            is LicenseMasterFragment -> {
                (currentFragment as LicenseMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is AgreementEntryFragment -> {
                showAlertMessage(currentFragment as AgreementEntryFragment)
            }

            else ->
                super.onBackPressed()
        }
        // region Title
        when (currentFragment) {
            is BusinessEntryFragment -> {
                setTitle()
            }
            is BusinessOwnerEntryFragment -> {
                showToolbarBackButton(R.string.title_business_owner)
            }
            is BusinessOwnerMasterFragment -> {
                showToolbarBackButton(R.string.title_business_owner)
            }
            is TaxEntryFragment -> {
                showToolbarBackButton(R.string.title_taxes)
            }
            is TaxMasterFragment -> {
                showToolbarBackButton(R.string.title_taxes)
            }
            is WeaponTaxMasterFragment -> {
                showToolbarBackButton(R.string.title_weapon_tax)
            }
            is CartTaxMasterFragment -> {
                showToolbarBackButton(R.string.title_cart_tax)
            }
            is GamingMachineTaxMasterFragment -> {
                showToolbarBackButton(R.string.title_gaming_machine)
            }
            is AdvertisementEntryFragment -> {
                showToolbarBackButton(R.string.title_advertisements)
            }
            is OutstandingsMasterFragment -> {
                showToolbarBackButton(R.string.title_initial_outstandings)
            }
            is PropertyTaxMasterFragment -> {
                showToolbarBackButton(R.string.title_property_txt)
            }
            is ShowTaxEntryFragment -> {
                showToolbarBackButton(R.string.title_shows)
            }
            is HotelEntryFragment -> {
                showToolbarBackButton(R.string.title_hotels)
            }
            is LicenseEntryFragment -> {
                showToolbarBackButton(R.string.title_licenses)
            }
            is AgreementEntryFragment -> {
                showToolbarBackButton(R.string.agreementText)
            }
            is AgreementListMasterFragment -> {
                showToolbarBackButton(R.string.title_agreement_list)
            }
            is DueDocumentsMasterFragment -> {
                showToolbarBackButton(R.string.agreement_documents)
            }
            is ListofDueNoticeMasterFragment -> {
                showToolbarBackButton(R.string.title_list_of_due_notice)
            }
        }
        // endregion
    }

    private fun showAlertMessage(currentFragment: AgreementEntryFragment)
    {

        if (screenMode == Constant.ScreenMode.EDIT)
        {
            if (currentFragment.getImageCount()==0)
            {
                showAlertDialog(getString(R.string.msg_provide) + " " + getString(R.string.documents))
            }
            else
            {
                showConfirmationDialog()

            }
        }
        else
        {
            showConfirmationDialog()
        }

    }

    private fun setTitle() {
        when (fromScreen) {
            Constant.QuickMenu.QUICK_MENU_REGISTER_BUSINESS -> showToolbarBackButton(R.string.title_register_business)
            Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS -> showToolbarBackButton(R.string.title_update_business)
            Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD -> {
                if (mScreenMode == Constant.ScreenMode.EDIT)
                    showToolbarBackButton(R.string.title_update_business)
                else
                    showToolbarBackButton(R.string.title_business_record)

            }
            else -> {
            }
        }
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_SYCO_TAX_ID))
                sycoTaxId = intent.getStringExtra(Constant.KEY_SYCO_TAX_ID) ?: ""

            if (intent.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen =
                    intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.QuickMenu

            if (intent.hasExtra(Constant.KEY_EDIT))
                hideEditButtton = intent.getBooleanExtra(Constant.KEY_EDIT, false)

            if (intent.hasExtra(Constant.KEY_BUSINESS_MODE))
                businessMode =
                    intent.getSerializableExtra(Constant.KEY_BUSINESS_MODE) as? Constant.BusinessMode ?: Constant.BusinessMode.None

            vuCrmAccount = ObjectHolder.registerBusiness.vuCrmAccounts

            if (fromScreen == Constant.QuickMenu.QUICK_MENU_BUSINESS_RECORD) {
                mScreenMode = Constant.ScreenMode.VIEW
            }
//            if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS && hideEditButtton) {
//                mScreenMode = Constant.ScreenMode.VIEW
//            }
            //todo added this condition for setting screen mode as edit for new requirement from onsite
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_UPDATE_BUSINESS) {
                mScreenMode = Constant.ScreenMode.EDIT
            }
        }
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    private fun showBusinessEntryScreen() {
        //region SetArguments
        val bundle = Bundle()
        bundle.putString(Constant.KEY_SYCO_TAX_ID, sycoTaxId)
        bundle.putBoolean(Constant.KEY_EDIT, hideEditButtton)
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putSerializable(Constant.KEY_BUSINESS_MODE, businessMode)
        businessEntryFragment.arguments = bundle
        //endregion

        addFragment(businessEntryFragment, false, R.id.container)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.container)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

    override fun onLatLonFound(inputLat: Double?, inputLon: Double?) {
        businessEntryFragment.let {
            businessEntryFragment.bindLatLongs(inputLat, inputLon)
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_CODE_BUSINESS_MASTER){
            data?.let {
                if (it.getBooleanExtra(Constant.KEY_REFRESH, false)) {
                    val intent = Intent()
                    intent.putExtra(Constant.KEY_REFRESH, true)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }*/

}