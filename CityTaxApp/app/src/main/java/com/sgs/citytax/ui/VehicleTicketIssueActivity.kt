package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityVehicleTicketIssueBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.ViolationDetail
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class VehicleTicketIssueActivity : BaseActivity(),
        VehicleTicketEntryFragment.Listener,
        BusinessOwnerSearchFragment.Listener,
        BusinessOwnerMasterFragment.Listener,
        BusinessOwnerEntryFragment.Listener,
        NotesMasterFragment.Listener,
        NotesEntryFragment.Listener,
        PhoneMasterFragment.Listener,
        PhoneEntryFragment.Listener,
        AddressMasterFragment.Listener,
        AddressEntryFragment.Listener,
        CardMasterFragment.Listener,
        CardEntryFragment.Listener,
        EmailMasterFragment.Listener,
        EmailEntryFragment.Listener,
        DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener,
        LocateDialogFragment.Listener,
        VehicleSearchFragment.Listener,
        BusinessSearchFragment.Listener,
        VehicleTicketHistoryFragment.Listener,
        DriverTicketHistoryFragment.Listener,
        LocalDocumentsMasterFragment.Listener,
        LocalDocumentEntryFragment.Listener,
        ViolationTicketEntryFragment.Listener,
        ViolationTypeMasterFragment.Listener,
        ViolationTypeEntryFragment.Listener {
    private lateinit var mBinding: ActivityVehicleTicketIssueBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var mDocument: COMDocumentReference = COMDocumentReference()
    private var mViolationDetail: ViolationDetail? = ViolationDetail()
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD

    private var fragment = VehicleTicketEntryFragment()
    private var violationFragment = ViolationTicketEntryFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_ticket_issue)
        showToolbarBackButton(R.string.title_ticket_issue)
        processIntent()
        setUpFragments()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)


    override fun onBackPressed() {
        when (currentFragment) {
            is LocalDocumentsMasterFragment -> {
                (currentFragment as LocalDocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is DocumentsMasterFragment -> {
                (currentFragment as DocumentsMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is NotesMasterFragment -> {
                (currentFragment as NotesMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is AddressMasterFragment -> {
                (currentFragment as AddressMasterFragment).onBackPressed()
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
            is ViolationTypeMasterFragment -> {
                (currentFragment as ViolationTypeMasterFragment).onBackPressed()
                super.onBackPressed()
            }
            is VehicleTicketEntryFragment -> {
                showConfirmationDialog()
            }
            is ViolationTicketEntryFragment -> {
                val violationEntryFrag = currentFragment as ViolationTicketEntryFragment
                when {
                    violationEntryFrag.isPoprcVehicleSearchUpVisible() -> {
                        violationEntryFrag.onBackPressed()
                    }
                    violationEntryFrag.isPoprcVehCtzOwnUpVisible() -> {
                        violationEntryFrag.onBackPressed()
                    }
                    violationEntryFrag.isPoprcvDriverUpVisible() -> {
                        violationEntryFrag.onBackPressed()
                    }
                    violationEntryFrag.isBusinessContrevenantPopUpVisible() -> {
                        violationEntryFrag.onBackPressed()
                    }
                    violationEntryFrag.isCitizenContrevenantPopUpVisible() -> {
                        violationEntryFrag.onBackPressed()
                    }
                    else -> {
                        showConfirmationDialog()
                    }
                }
            }
            else ->
                super.onBackPressed()

        }

        when (currentFragment) {
            is VehicleTicketEntryFragment -> {
                showToolbarBackButton(R.string.title_ticket_issue)
            }
            is ViolationTicketEntryFragment -> {
                showToolbarBackButton(R.string.title_ticket_issue)
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

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.hasExtra(Constant.KEY_DOCUMENT))
                mDocument = it.getParcelableExtra(Constant.KEY_DOCUMENT) as COMDocumentReference
            if (it.hasExtra(Constant.KEY_VIOLATION_DETAIL))
                mViolationDetail = it.getParcelableExtra(Constant.KEY_VIOLATION_DETAIL)
        }
    }

    private fun setUpFragments() {
        when (fromScreen) {
            Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE -> {
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putParcelable(Constant.KEY_DOCUMENT, mDocument)
                violationFragment.arguments = bundle
                addFragmentWithOutAnimation(violationFragment, false, R.id.container)
            }
            Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE_EDIT -> {
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                bundle.putParcelable(Constant.KEY_VIOLATION_DETAIL, mViolationDetail)
                fragment.arguments = bundle
                addFragmentWithOutAnimation(fragment, false, R.id.container)
            }
        }

    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

    override fun onLatLonFound(latitude: Double?, longitude: Double?) {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_VEHICLE_TICKET_ISSUE)
            violationFragment.bindLatLongs(latitude, longitude)
        else
            fragment.bindLatLongs(latitude, longitude)
    }

}