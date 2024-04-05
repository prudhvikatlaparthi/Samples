package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityLicenseRenewalBinding
import com.sgs.citytax.ui.fragments.PendingLicensesMasterFragment
import com.sgs.citytax.util.Constant

class PendingLicenseRenewalActivity : BaseActivity(), PendingLicensesMasterFragment.Listener{
    private lateinit var mBinding: ActivityLicenseRenewalBinding
    private var fromScreen: Constant.QuickMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_license_renewal)
        processIntent()
        setToolBarTitle()
        attachFragment()
    }

    private fun attachFragment() {
        addFragmentWithOutAnimation(PendingLicensesMasterFragment(), true, R.id.licenseContainer)
    }

    private fun processIntent() {
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun setToolBarTitle() {
        showToolbarBackButton(R.string.license_renewal)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scan, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_scan) {
            val intent = Intent(this, ScanActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
            startActivity(intent)
        } else if (id == android.R.id.home) {
            finish()
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.licenseContainer)
    }


}