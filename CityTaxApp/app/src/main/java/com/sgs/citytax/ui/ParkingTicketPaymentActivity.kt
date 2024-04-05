package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ParkingPaymentTrans
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityParkingTicketPaymentBinding
import com.sgs.citytax.ui.fragments.ParkingTicketPaymentFragment
import com.sgs.citytax.util.Constant

class ParkingTicketPaymentActivity : BaseActivity(), ParkingTicketPaymentFragment.Listener {
    private lateinit var binding: ActivityParkingTicketPaymentBinding
    var fragment = ParkingTicketPaymentFragment()

    private var fromScreen: Any? = null
    private var vehicleNo: String? = ""
    private var parkingID: Int = 0
    private var parkingPaymentTrans: ParkingPaymentTrans? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_parking_ticket_payment)
        processIntent()
        showFragment()
        showToolbarBackButton(R.string.parking_ticket_payment)
    }


    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU)
            if (it.hasExtra(Constant.KEY_VEHICLE_NO))
                vehicleNo = it.getStringExtra(Constant.KEY_VEHICLE_NO)
            if (it.hasExtra(Constant.KEY_PARKING_PLACE_ID))
                parkingID = it.getIntExtra(Constant.KEY_PARKING_PLACE_ID, 0)
        }
    }

    private fun showFragment() {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        bundle.putInt(Constant.KEY_PARKING_PLACE_ID, parkingID)
        bundle.putString(Constant.KEY_VEHICLE_NO, vehicleNo)
        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment, false, R.id.container)
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
            is ParkingTicketPaymentFragment ->
                (currentFragment as ParkingTicketPaymentFragment).onBackPressed()
        }
        super.onBackPressed()
    }


    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        TODO("Not yet implemented")
    }
}
