package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GetSearchIndividualTaxDetails
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityBusinessTransactionHistoryBinding
import com.sgs.citytax.ui.fragments.BusinessTransactionHistoryFragment
import com.sgs.citytax.util.Constant

class BusinessTransactionHistoryActivity : BaseActivity(), BusinessTransactionHistoryFragment.Listener {

    private lateinit var binding: ActivityBusinessTransactionHistoryBinding
    private lateinit var sycoTaxID: String
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_CORPORATE_BUSINESS_TRANSACTION_HISTORY
    private var getSearchIndividualTaxDetails : GetSearchIndividualTaxDetails?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_transaction_history)
        showToolbarBackButton(R.string.business_transaction_history)
        processIntent()
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun processIntent() {
        intent?.extras?.let {
            sycoTaxID = it.getString(Constant.KEY_CUSTOMER_ID, "")
            mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            getSearchIndividualTaxDetails = it.getParcelable(Constant.KEY_BUSINESS_TRANSACTION_HISTORY)
        }
    }

    private fun attachFragment() {
        val fragment = BusinessTransactionHistoryFragment.newInstance(sycoTaxID,mCode,getSearchIndividualTaxDetails)
        addFragment(fragment, true, R.id.container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

