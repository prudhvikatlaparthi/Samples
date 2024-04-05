package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityVehicleOwnershipBinding
import com.sgs.citytax.model.VehicleMaster
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant

class OnBoardingVehicleOwnershipActivity : BaseActivity(),
        VehicleOnBoardEntryFragment.Listener, VehicleOnBoardingMasterFragment.Listener,
        BusinessOwnerEntryFragment.Listener, BusinessOwnerSearchFragment.Listener,
        FragmentCommunicator, DocumentsMasterFragment.Listener,
        DocumentEntryFragment.Listener, NotesMasterFragment.Listener,
        NotesEntryFragment.Listener, PhoneMasterFragment.Listener,
        PhoneEntryFragment.Listener, EmailMasterFragment.Listener,
        CardEntryFragment.Listener, CardMasterFragment.Listener,
        EmailEntryFragment.Listener, AddressMasterFragment.Listener,
        AddressEntryFragment.Listener {

    lateinit var binding: ActivityVehicleOwnershipBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var hideEditButtton: Boolean = false
    private var sycoTaxId = ""
    var vehicleMaster: VehicleMaster? = null

    private val vehicleEntryFragment = VehicleOnBoardEntryFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_ownership)
        processIntent()
        setTitle()
        showVehicleEntryScreen()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun showVehicleEntryScreen() {
        //region SetArguments
        val bundle = Bundle()
        bundle.putString(Constant.KEY_SYCO_TAX_ID, sycoTaxId)
        bundle.putBoolean(Constant.KEY_EDIT, hideEditButtton)
        if (vehicleMaster != null) {
            bundle.putParcelable(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS, vehicleMaster)
        }
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        vehicleEntryFragment.arguments = bundle
        //endregion

        addFragment(vehicleEntryFragment, false, R.id.container)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    private fun setTitle() {
        when (fromScreen) {
            Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP -> showToolbarBackButton(R.string.title_register_vehicle)
            Constant.QuickMenu.QUICK_MENU_PARKING_VEHICLE_OWNERSHIP -> showToolbarBackButton(R.string.title_register_vehicle)
            Constant.QuickMenu.QUICK_MENU_VIOLATION_REGISTER_VEHICLE -> showToolbarBackButton(R.string.title_register_vehicle)
            Constant.QuickMenu.QUICK_MENU_VEHICLE_OWNERSHIP_TICKET_ISSUE -> showToolbarBackButton(R.string.title_register_vehicle)
            else -> {
            }
        }
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_QUICK_MENU)) {
                fromScreen = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu?
                sycoTaxId = intent.getStringExtra(Constant.KEY_SYCO_TAX_ID) ?: ""
            }

            if (intent.hasExtra(Constant.KEY_EDIT))
                hideEditButtton = intent.getBooleanExtra(Constant.KEY_EDIT, false)

            if (intent.hasExtra(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS)) {
                vehicleMaster = intent.getParcelableExtra(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS) as VehicleMaster
            }

            if (hideEditButtton) {
                mScreenMode = Constant.ScreenMode.EDIT
            } else {
                mScreenMode = Constant.ScreenMode.ADD
            }
        }
    }


    override fun onBackPressed() {
        when (currentFragment) {
            is VehicleOnBoardEntryFragment -> {
                setTitle()
                if (screenMode == Constant.ScreenMode.ADD || screenMode == Constant.ScreenMode.EDIT) {
                    showConfirmationDialog()
                    return
                }
            }
            is DocumentsMasterFragment -> {
                (currentFragment as DocumentsMasterFragment).onBackPressed()
            }
            is NotesMasterFragment -> {
                (currentFragment as NotesMasterFragment).onBackPressed()
            }
            is VehicleCitizenOnBoardFragment -> {
                (currentFragment as VehicleCitizenOnBoardFragment).onBackPressed()
            }
            is VehicleOnBoardingMasterFragment -> {
                setTitle()
                (currentFragment as VehicleOnBoardingMasterFragment).onBackPressed()
            }
            is AddressMasterFragment -> {
                (currentFragment as AddressMasterFragment).onBackPressed()
            }
            is PhoneMasterFragment -> {
                (currentFragment as PhoneMasterFragment).onBackPressed()
            }
            is EmailMasterFragment -> {
                (currentFragment as EmailMasterFragment).onBackPressed()
            }
            is CardMasterFragment -> {
                (currentFragment as CardMasterFragment).onBackPressed()
            }
            is BusinessOwnerEntryFragment -> {
                showToolbarBackButton(R.string.citizen)
            }
        }
        super.onBackPressed()
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

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.container)
    }
}