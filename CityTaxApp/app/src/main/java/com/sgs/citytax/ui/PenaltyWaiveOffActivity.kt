package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GetSearchIndividualTaxDetails
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityPenaltyWaiveOffBinding
import com.sgs.citytax.model.LawPenalties
import com.sgs.citytax.model.ParkingPenalties
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.ui.fragments.LawPenaltyWaiveOffFragment
import com.sgs.citytax.ui.fragments.ParkingPenaltyWaiveOffFragment
import com.sgs.citytax.ui.fragments.PenaltyWaiveOffFragment
import com.sgs.citytax.util.Constant

class PenaltyWaiveOffActivity : BaseActivity(), PenaltyWaiveOffFragment.Listener, ParkingPenaltyWaiveOffFragment.Listener, LawPenaltyWaiveOffFragment.Listener {

    private lateinit var binding: ActivityPenaltyWaiveOffBinding
    private var accountId = ""
    private var fromScreen: Constant.QuickMenu? = null
    private var getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails? = null
    private var mLawPenalties: ArrayList<LawPenalties> = arrayListOf()
    private var mParkingPenalties: ArrayList<ParkingPenalties> = arrayListOf()
    private var mVuComProperties: VuComProperties? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_penalty_waive_off)
        showToolbarBackButton(R.string.penalty_waive_off)
        processIntent()
        attachFragment()
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_ACCOUNT_ID))
                accountId = intent.getIntExtra(Constant.KEY_ACCOUNT_ID, 0).toString()
            if (intent.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.QuickMenu
            if (intent.hasExtra(Constant.KEY_INDIVIDUAL_TAX_DETAILS))
                getSearchIndividualTaxDetails = intent.getParcelableExtra(Constant.KEY_INDIVIDUAL_TAX_DETAILS)
            if (intent.hasExtra(Constant.KEY_LAW_TAX_DETAILS))
                mLawPenalties = intent.getParcelableArrayListExtra(Constant.KEY_LAW_TAX_DETAILS)
            if (intent.hasExtra(Constant.KEY_PARKING_TAX_DETAILS))
                mParkingPenalties = intent.getParcelableArrayListExtra(Constant.KEY_PARKING_TAX_DETAILS)
            if (intent.hasExtra(Constant.KEY_PROPERTY_TAX_PENALTY_WAIVE_OFF))
                mVuComProperties = it.extras?.getParcelable(Constant.KEY_PROPERTY_TAX_PENALTY_WAIVE_OFF)
        }
    }

    private fun attachFragment() {
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_LAW_PENALTY_WAIVE_OFF) {
            val fragment = LawPenaltyWaiveOffFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_ACCOUNT_ID, accountId)
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            bundle.putParcelableArrayList(Constant.KEY_LAW_TAX_DETAILS, mLawPenalties)
            fragment.arguments = bundle
            addFragment(fragment, false)
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PARKING_PENALTY_WAIVE_OFF) {
            val fragment = ParkingPenaltyWaiveOffFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_ACCOUNT_ID, accountId)
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            bundle.putParcelableArrayList(Constant.KEY_PARKING_TAX_DETAILS, mParkingPenalties)
            fragment.arguments = bundle
            addFragment(fragment, false)
        } else if (fromScreen == Constant.QuickMenu.QUICK_MENU_PROPERTY_TAX_PENALTY_WAIVE_OFF || fromScreen == Constant.QuickMenu.QUICK_MENU_LAND_TAX_PENALTY_WAIVE_OFF) {
            val fragment = PenaltyWaiveOffFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            bundle.putParcelable(Constant.KEY_PROPERTY_TAX_PENALTY_WAIVE_OFF, mVuComProperties)
            fragment.arguments = bundle
            addFragment(fragment, false)
        } else {
            val fragment = PenaltyWaiveOffFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constant.KEY_ACCOUNT_ID, accountId)
            bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
            bundle.putParcelable(Constant.KEY_INDIVIDUAL_TAX_DETAILS, getSearchIndividualTaxDetails)
            fragment.arguments = bundle
            addFragment(fragment, false)
        }

    }


    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.penalityWaiveOffContainer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


}
