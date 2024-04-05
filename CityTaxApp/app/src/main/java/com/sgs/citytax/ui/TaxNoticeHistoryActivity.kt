package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.GenerateTaxNoticeResponse
import com.sgs.citytax.api.response.GetSearchIndividualTaxDetails
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityTaxNoticeHistoryBinding
import com.sgs.citytax.model.TaxNoticeHistoryList
import com.sgs.citytax.model.VuComProperties
import com.sgs.citytax.ui.fragments.TaxNoticeHistoryFragment
import com.sgs.citytax.util.Constant

class TaxNoticeHistoryActivity : BaseActivity(), TaxNoticeHistoryFragment.Listener {

    private lateinit var binding: ActivityTaxNoticeHistoryBinding
    private lateinit var sycoTaxID: String
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_CORPORATE_TAX_NOTICE_HISTORY
    private var getSearchIndividualTaxDetails: GetSearchIndividualTaxDetails? = null
    private var mVuComProperties: VuComProperties? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tax_notice_history)
        showToolbarBackButton(R.string.tax_notice_history)
        processIntent()
        setFragments()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.container)
    }

    override fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        replaceFragment(fragment, true, R.id.container)
    }

    override fun onItemClick(taxNoticeHistoryList: TaxNoticeHistoryList, position: Int) {
        val intent = Intent(this, AllTaxNoticesActivity::class.java)
        val generateTaxNoticeResponse = GenerateTaxNoticeResponse()
        generateTaxNoticeResponse.taxNoticeID = taxNoticeHistoryList.taxInvoiceID?.toInt()
        generateTaxNoticeResponse.taxRuleBookCode = taxNoticeHistoryList.taxRuleBookCode
        intent.putExtra(Constant.KEY_RECEIPT_TYPE, Constant.ReceiptType.TAX_NOTICE_HISTORY)
        intent.putParcelableArrayListExtra(Constant.KEY_GENERATE_TAX_NOTICE_RESPONSE, arrayListOf(generateTaxNoticeResponse))
        intent.putExtra(Constant.KEY_QUICK_MENU, mCode)
        startActivity(intent)
        prefHelper.isFromHistory = true
    }

    private fun processIntent() {
        intent?.extras?.let {
            sycoTaxID = it.getString(Constant.KEY_CUSTOMER_ID, "")
            mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            getSearchIndividualTaxDetails = it.getParcelable(Constant.KEY_TAX_NOTICE_HISTORY)
            mVuComProperties = it.getParcelable(Constant.KEY_PROPERTY_TAX_NOTICE_HISTORY)
        }
    }

    private fun setFragments() {
        addFragment(TaxNoticeHistoryFragment.newInstance(sycoTaxID, mCode, getSearchIndividualTaxDetails, mVuComProperties), true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}