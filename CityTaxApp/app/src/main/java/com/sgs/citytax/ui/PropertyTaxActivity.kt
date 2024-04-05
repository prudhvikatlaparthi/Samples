package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.R
import com.sgs.citytax.api.payload.StorePropertyData
import com.sgs.citytax.api.response.PropertyDetailsBySycoTax
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityPropertyTaxBinding
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper
import com.sgs.citytax.util.LogHelper

class PropertyTaxActivity : BaseActivity(), FragmentCommunicator,
        LocationHelper.Location, PropertyTaxEntryFragment.Listener,
        PropertyImageMasterFragment.Listener,
        PropertyImageEntryFragment.Listener,
        PropertyPlanImageMasterFragment.Listener,
        PropertyPlanImageEntryFragment.Listener,
        LocalDocumentsMasterFragment.Listener,
        LocalDocumentEntryFragment.Listener,
        LocalNotesMasterFragment.Listener,
        LocalNotesEntryFragment.Listener,
        PropertyOwnerMasterFragment.Listener,
        PropertyOwnerEntryFragment.Listener,
        PropertyOwnerOnBoardFragment.Listener,
        BusinessOwnerEntryFragment.Listener,
        BusinessOwnerSearchFragment.Listener,
        DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener,
        NotesMasterFragment.Listener,
        NotesEntryFragment.Listener,
        PhoneMasterFragment.Listener,
        PhoneEntryFragment.Listener,
        CardMasterFragment.Listener,
        CardEntryFragment.Listener,
        EmailMasterFragment.Listener,
        EmailEntryFragment.Listener,
        OutstandingsMasterFragment.Listener,
        OutstandingEntryFragment.Listener,
        PropertyTaxSummaryFragment.Listener {
    private lateinit var binding: ActivityPropertyTaxBinding
    private var mSycoTaxID: String? = ""
    private var editMode: Boolean? = true
    private var mCode: Constant.QuickMenu? = Constant.QuickMenu.QUICK_MENU_NONE
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    var mPropertyDetailsBySycoTax: PropertyDetailsBySycoTax? = null
    var mStorePropertyData: StorePropertyData? = null
    var address: ArrayList<GeoAddress>? = null
    private var mTaxRuleBookCode: String? = ""
    private var mAccountId: Int = 0
    private var setViewForGeoSpatial: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_property_tax)
        showToolbarBackButton(R.string.title_property_txt)
        processIntent()

    }

    private fun attachFragment() {
        val fragment = PropertyTaxEntryFragment.newInstance()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, mCode)
        bundle.putString(Constant.KEY_SYCO_TAX_ID, mSycoTaxID)
        bundle.putParcelable(Constant.KEY_PROPERTY_DETAILS, mStorePropertyData)
        bundle.putParcelableArrayList(Constant.KEY_ADDRESS, address)
        bundle.putString(Constant.KEY_TAX_RULE_BOOK_CODE, mTaxRuleBookCode)
        bundle.putInt(Constant.KEY_ACCOUNT_ID, mAccountId)
        editMode?.let {
            bundle.putBoolean(Constant.KEY_EDIT, it)
        }
        setViewForGeoSpatial?.let { bundle.putBoolean(Constant.KEY_GEO_SPATIAL_VIEW, it) }
        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment, false, R.id.container)
    }

    private fun processIntent() {
        try {
            val mIntent = intent
            mIntent?.let {
                if (it.hasExtra(Constant.KEY_QUICK_MENU)) {
                    mCode = it.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
                }
                if (it.hasExtra(Constant.KEY_SYCO_TAX_ID)) {
                    mSycoTaxID = it.getStringExtra(Constant.KEY_SYCO_TAX_ID)
                }
                if (it.hasExtra(Constant.KEY_PROPERTY_DETAILS)) {
                    mPropertyDetailsBySycoTax = it.getParcelableExtra(Constant.KEY_PROPERTY_DETAILS)
                }
                if (it.hasExtra(Constant.KEY_ADDRESS)) {
                    address = it.getParcelableArrayListExtra<GeoAddress>(Constant.KEY_ADDRESS)
                }
                if (it.hasExtra(Constant.KEY_TAX_RULE_BOOK_CODE)) {
                    mTaxRuleBookCode = it.getStringExtra(Constant.KEY_TAX_RULE_BOOK_CODE)
                }
                if (it.hasExtra(Constant.KEY_ACCOUNT_ID)) {
                    mAccountId = it.getIntExtra(Constant.KEY_ACCOUNT_ID,0)
                }
                if (it.hasExtra(Constant.KEY_EDIT))
                    editMode = it.getBooleanExtra(Constant.KEY_EDIT, false)

                if (editMode == false) {
                    screenMode = Constant.ScreenMode.VIEW
                }
                if (it.hasExtra(Constant.KEY_GEO_SPATIAL_VIEW))
                    setViewForGeoSpatial = it.getBooleanExtra(Constant.KEY_GEO_SPATIAL_VIEW, false)

            }
        } catch (e: Exception) {
            LogHelper.writeLog(exception = e)
        }
        if (mPropertyDetailsBySycoTax != null) {
            if (mPropertyDetailsBySycoTax?.propertyDetails?.propertySycotaxID != null) {
                mSycoTaxID = mPropertyDetailsBySycoTax?.propertyDetails?.propertySycotaxID
            }
            mStorePropertyData = mPropertyDetailsBySycoTax?.propertyDetails
//            address = mPropertyDetailsBySycoTax?.address as ArrayList<GeoAddress>

            if (mStorePropertyData!=null && mStorePropertyData?.isInvoiceGenerated!! || setViewForGeoSpatial == true) {
                mScreenMode = Constant.ScreenMode.VIEW
            }
        }
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, addToBackStack, R.id.container)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onBackPressed() {
        when (currentFragment) {
            is PropertyImageMasterFragment -> {
                (currentFragment as PropertyImageMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is PropertyOwnerMasterFragment -> {
                (currentFragment as PropertyOwnerMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is LocalDocumentsMasterFragment -> {
                (currentFragment as LocalDocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is LocalNotesMasterFragment -> {
                (currentFragment as LocalNotesMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is PropertyPlanImageMasterFragment -> {
                (currentFragment as PropertyPlanImageMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is NotesMasterFragment -> {
                (currentFragment as NotesMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is DocumentsMasterFragment -> {
                (currentFragment as DocumentsMasterFragment).onBackPressed()
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
            is CardMasterFragment -> {
                (currentFragment as CardMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is OutstandingsMasterFragment -> {
                (currentFragment as OutstandingsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is PropertyOwnerEntryFragment -> {
                (currentFragment as PropertyOwnerEntryFragment).onBackPressed()
                super.onBackPressed()
            }
            is PropertyTaxEntryFragment -> {
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                } else super.onBackPressed()
            }
            else -> {
                super.onBackPressed()

            }
        }
        when (currentFragment) {
            is PropertyTaxEntryFragment -> {
                showToolbarBackButton(R.string.title_property_txt)
            }
        }
    }

    private fun showConfirmationDialog() {
        showAlertDialog(R.string.do_you_want_to_exit,
                R.string.yes,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                    finish()
                },
                R.string.no,
                {
                    val dialog = (it as Button).tag as AlertDialog
                    dialog.dismiss()
                })
    }

    override fun start() {
        showProgressDialog()
    }

    override fun found(latitude: Double, longitude: Double) {
        val latLong = LatLng(latitude, longitude)
        addFragmentWithOutAnimation(GeoFenceMapFragment(), true, R.id.map)
        dismissDialog()
    }

}