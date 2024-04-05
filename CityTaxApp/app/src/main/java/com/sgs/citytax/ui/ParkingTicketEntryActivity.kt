package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityParkingTicketEntryBinding
import com.sgs.citytax.model.VehicleDetails
import com.sgs.citytax.ui.fragments.ParkingEntryFragment
import com.sgs.citytax.ui.fragments.ParkingTicketPreviewFragment
import com.sgs.citytax.util.Constant

class ParkingTicketEntryActivity : BaseActivity(),
        FragmentCommunicator,ParkingTicketPreviewFragment.Listener {

    private lateinit var mBinding: ActivityParkingTicketEntryBinding
    private var mScreenMode: Constant.ScreenMode = Constant.ScreenMode.ADD
    private var fromScreen: Constant.QuickMenu? = null
    private var vehicleDetails: VehicleDetails? = null

    private val parkingEntryFragment = ParkingEntryFragment()


    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_parking_ticket_entry)
        showToolbarBackButton(R.string.parking_ticket)
        processIntent()

        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        bundle.putParcelable(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS, vehicleDetails)
        parkingEntryFragment.arguments = bundle
        addFragmentWithOutAnimation(parkingEntryFragment, false, R.id.container)
    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU)) {
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            }
            if (it.hasExtra(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS)) {
                vehicleDetails = it.getParcelableExtra(Constant.KEY_VEHICLE_OWNERSHIP_DETAILS)
            }
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.container)
    }

    override var screenMode: Constant.ScreenMode
        get() = mScreenMode
        set(value) {
            mScreenMode = value
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (currentFragment) {
        is ParkingTicketPreviewFragment -> finish()
            is ParkingEntryFragment -> showConfirmationDialog()
        }
//        super.onBackPressed()
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
}