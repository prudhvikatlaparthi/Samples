package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityParkingTrasactionHistoryBinding
import com.sgs.citytax.ui.fragments.ParkingTransactionHistoryFragment
import com.sgs.citytax.util.Constant

class ParkingTransactionHistoryActivity : BaseActivity(), ParkingTransactionHistoryFragment.Listener {

    private lateinit var binding: ActivityParkingTrasactionHistoryBinding

    private var fromScreen: Any? = null
    private var vehicleNo: String? = ""
    private var parkingPlaceID: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_parking_trasaction_history)

        showToolbarBackButton(R.string.parking_transaction_history)
        processIntent()
        attachFragment()
    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU)
            if (it.hasExtra(Constant.KEY_VEHICLE_NO))
                vehicleNo = it.getStringExtra(Constant.KEY_VEHICLE_NO)
            if (it.hasExtra(Constant.KEY_PARKING_PLACE_ID))
                parkingPlaceID = it.getIntExtra(Constant.KEY_PARKING_PLACE_ID, 0)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }


    private fun attachFragment() {
        val fragment = ParkingTransactionHistoryFragment.newInstance()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen as Constant.QuickMenu)
        bundle.putInt(Constant.KEY_PARKING_PLACE_ID, parkingPlaceID)
        bundle.putString(Constant.KEY_VEHICLE_NO, vehicleNo)
        fragment.arguments = bundle
        addFragment(fragment, true, R.id.container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
