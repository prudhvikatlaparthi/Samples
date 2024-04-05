package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityPendingServiceInvoiceBinding
import com.sgs.citytax.ui.fragments.PendingServiceInvoiceListFragment
import com.sgs.citytax.util.Constant

class PendingServiceInvoicesActivity : BaseActivity()
        , PendingServiceInvoiceListFragment.Listener {
    private lateinit var mBinding: ActivityPendingServiceInvoiceBinding
    private var fromScreen: Constant.QuickMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pending_service_invoice)
        showToolbarBackButton(R.string.title_pending_services)
        intent.extras?.let {
            fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        attachFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }

    private fun attachFragment() {
        val fragment = PendingServiceInvoiceListFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
        fragment.arguments = bundle
        addFragment(fragment, false)

    }
}