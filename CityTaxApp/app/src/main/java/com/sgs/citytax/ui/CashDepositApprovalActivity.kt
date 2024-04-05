package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityCashDepositApprovalBinding
import com.sgs.citytax.ui.fragments.CashDepositRequestFragment

class CashDepositApprovalActivity : BaseActivity(), CashDepositRequestFragment.Listener {

    private lateinit var mBinding: ActivityCashDepositApprovalBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cash_deposit_approval)
        showToolbarBackButton(R.string.cash_deposit_request)
        setUpFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home ->
                finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private fun setUpFragment() {
        addFragmentWithOutAnimation(CashDepositRequestFragment(), true, R.id.container)
    }
}