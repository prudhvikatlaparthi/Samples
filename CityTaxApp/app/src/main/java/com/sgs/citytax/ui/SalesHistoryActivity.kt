package com.sgs.citytax.ui

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivitySalesHistoryBinding
import com.sgs.citytax.ui.fragments.SalesListFragment
import com.sgs.citytax.util.Constant

class SalesHistoryActivity : BaseActivity(),SalesListFragment.Listener {

    private var binding: ActivitySalesHistoryBinding? = null
    private var fromScreen: Constant.QuickMenu? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sales_history)

        intent?.let {
            if (it.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializableExtra(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }

        if (fromScreen == Constant.QuickMenu.QUICK_MENU_SALES_TAX)
            showToolbarBackButton(R.string.title_sales_history)
        else
            showToolbarBackButton(R.string.security_service_history)

        attachFragment()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
    private fun attachFragment() {
        val fragment = SalesListFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        fragment.arguments = bundle
        addFragmentWithOutAnimation(fragment,true,R.id.container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
