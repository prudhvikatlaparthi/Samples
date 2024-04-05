package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityInventoryStatusBinding
import com.sgs.citytax.ui.fragments.InventoryStatusFragment


class InventoryStatusActivity : BaseActivity(), InventoryStatusFragment.Listener {
    private lateinit var binding: ActivityInventoryStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inventory_status)
        showToolbarBackButton(R.string.title_stock_status)
        attachFragment()
    }

    private fun attachFragment() {
        addFragmentWithOutAnimation(InventoryStatusFragment(), false, R.id.productTaskContainer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}