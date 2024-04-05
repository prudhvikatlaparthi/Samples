package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityVehicleOutBinding
import com.sgs.citytax.model.ParkingTicketDetails
import com.sgs.citytax.ui.fragments.LastParkingOverStayEntryFragment
import com.sgs.citytax.ui.fragments.ParkingTicketPreviewFragment
import com.sgs.citytax.util.Constant

class VehicleOutActivity : BaseActivity(),
        ParkingTicketPreviewFragment.Listener,
        LastParkingOverStayEntryFragment.Listener {
    private lateinit var mBinding: ActivityVehicleOutBinding
    private var fromScreen: Constant.QuickMenu? = null
    private var isValidParkingTicket: Boolean = false
    private var vehicleNo: String? = ""
    private var parkingDetails: ParkingTicketDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_out)
        showToolbarBackButton(R.string.title_select_vehicle_Out)
        processIntent()
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


    private fun processIntent() {
        intent.extras?.let {
            fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            isValidParkingTicket = it.getBoolean(Constant.KEY_IS_VALID_PARKING)
            parkingDetails = it.getParcelable(Constant.KEY_PARKING_TICKET_DETAILS)
            vehicleNo = it.getString(Constant.KEY_VEHICLE_NO)
        }
    }

    private fun attachFragment() {
        if (isValidParkingTicket) {
            val fragment = ParkingTicketPreviewFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            bundle.putParcelable(Constant.KEY_PARKING_TICKET_DETAILS, parkingDetails)
            bundle.putInt(Constant.KEY_TAX_INVOICE_ID, parkingDetails?.taxInvoiceId ?: 0)
            fragment.arguments = bundle
            addFragment(fragment, false)
            prefHelper.isFromHistory = false
        } else {
            val fragment = LastParkingOverStayEntryFragment()
            val bundle = Bundle()
            bundle.putString(Constant.KEY_VEHICLE_NO, vehicleNo)
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            fragment.arguments = bundle
            addFragment(fragment, false)
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, false, R.id.container)
    }

}