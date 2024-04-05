package com.sgs.citytax.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ImpondmentReturn
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityTicketPaymentBinding
import com.sgs.citytax.ui.fragments.AnimalReturnPayFragment
import com.sgs.citytax.ui.fragments.TicketPaymentFragment
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper

class TicketPaymentActivity : BaseActivity(), TicketPaymentFragment.Listener, AnimalReturnPayFragment.Listener {
    private var fromScreen: Any? = null
    private lateinit var binding: ActivityTicketPaymentBinding
    private var mImpondmentReturnHistory: ArrayList<ImpondmentReturn> = arrayListOf()
    lateinit var selectedSpinCombiValue: String
    lateinit var selectedSpinCombiCode: String
    var fragment = TicketPaymentFragment()

    private lateinit var helper: LocationHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ticket_payment)
        processIntent()
        showFragment()
        showToolbarBackButton(R.string.ticket_payment)

    }

    private fun processIntent() {
        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU)
            if (it.hasExtra(Constant.KEY_VIOLATION_VALUE))
                mImpondmentReturnHistory = it.getParcelableArrayListExtra(Constant.KEY_VIOLATION_VALUE)
            if (it.hasExtra(Constant.KEY_SELECTED_COMBI_VALUE))
                selectedSpinCombiValue = it.getStringExtra(Constant.KEY_SELECTED_COMBI_VALUE)
            if (it.hasExtra(Constant.KEY_SELECTED_COMBI_CODE))
                selectedSpinCombiCode = it.getStringExtra(Constant.KEY_SELECTED_COMBI_CODE)
        }
    }

    private fun showFragment() {
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constant.KEY_IMPOUNDMENT_RETURN, mImpondmentReturnHistory)
        bundle.putString(Constant.KEY_SELECTED_COMBI_VALUE, selectedSpinCombiValue)
        bundle.putString(Constant.KEY_SELECTED_COMBI_CODE, selectedSpinCombiCode)
        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment, false, R.id.container)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        TODO("Not yet implemented")
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
            is TicketPaymentFragment ->
                (currentFragment as TicketPaymentFragment).onBackPressed()
        }
        super.onBackPressed()
    }
}
