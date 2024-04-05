package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityStockInReportBinding
import com.sgs.citytax.ui.fragments.StockInReportFragment

class StockInReportActivity : BaseActivity(), StockInReportFragment.Listener {

    private var binding: ActivityStockInReportBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stock_in_report)
        showToolbarBackButton(R.string.title_stock_in_report)
        setUpMasterFragment()
    }

    private fun setUpMasterFragment() {
        addFragmentWithOutAnimation(StockInReportFragment(), false, R.id.container)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}