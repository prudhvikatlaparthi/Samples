package com.sgs.citytax.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityAssetBookingReturnListBinding
import com.sgs.citytax.ui.fragments.AssetPendingBookingListFragment
import com.sgs.citytax.ui.fragments.AssetReturnListFragment
import com.sgs.citytax.util.Constant

class AssetBookingAndReturnListActivity : BaseActivity(),
        AssetReturnListFragment.Listener,
        AssetPendingBookingListFragment.Listener {
    private lateinit var mBinding: ActivityAssetBookingReturnListBinding
    private var fromScreen: Constant.QuickMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_asset_booking_return_list)
        processIntent()
        setToolBarTitle()
        setUpMasterFragments()
    }

    private fun processIntent() {
        intent?.extras?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                fromScreen = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
    }

    private fun setToolBarTitle() {
        when (fromScreen) {
            Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT -> {
                showToolbarBackButton(R.string.asset_assignment)
            }
            Constant.QuickMenu.QUICK_MENU_ASSET_RETURN -> {
                showToolbarBackButton(R.string.return_asset)
            }
        }
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

    private fun setUpMasterFragments() {
        when (fromScreen) {
            Constant.QuickMenu.QUICK_MENU_ASSET_RETURN -> {
                val fragment = AssetReturnListFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                fragment.arguments = bundle
                addFragment(fragment, false)
            }
            Constant.QuickMenu.QUICK_MENU_ASSET_ASSIGNMENT ->{
                val fragment = AssetPendingBookingListFragment()
                val bundle = Bundle()
                bundle.putSerializable(Constant.KEY_QUICK_MENU, fromScreen)
                fragment.arguments = bundle
                addFragment(fragment, false)
            }
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragmentWithOutAnimation(fragment, addToBackStack, R.id.container)
    }
}