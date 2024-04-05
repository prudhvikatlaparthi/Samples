package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GetSearchIndividualTaxDetails
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityBusinessTransactionHistoryBinding
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.ui.fragments.BusinessTransactionHistoryFragment
import com.sgs.citytax.ui.fragments.PropertyTransactionHistoryFragment
import com.sgs.citytax.util.Constant

class PropertyTransactionHistoryActivity : BaseActivity(), FragmentCommunicator {

    private lateinit var binding: ActivityBusinessTransactionHistoryBinding
    private lateinit var sycoTaxID: String
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_CORPORATE_BUSINESS_TRANSACTION_HISTORY
    private var vuComProperties : VuComProperties?= null

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
            if(it.containsKey(Constant.KEY_SYCO_TAX_ID)){
                sycoTaxID = it.getString(Constant.KEY_SYCO_TAX_ID, "")
            }
            if(it.containsKey(Constant.KEY_QUICK_MENU)){
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            }
            if(it.containsKey(Constant.KEY_PROPERTY_TRANSACTION_HISTORY)){
                vuComProperties = it.getParcelable(Constant.KEY_PROPERTY_TRANSACTION_HISTORY)
            }
        }
    }

    private fun attachFragment() {
        val fragment = PropertyTransactionHistoryFragment.newInstance(sycoTaxID,mCode,vuComProperties)
        addFragment(fragment, true, R.id.container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {

    }

    override var screenMode: Constant.ScreenMode
        get() = Constant.ScreenMode.VIEW
        set(value) {}
}

