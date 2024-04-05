package com.sgs.citytax.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityBusinessSearchListBinding
import com.sgs.citytax.ui.fragments.BusinessSearchFragment


class BusinessSearchList : BaseActivity(), BusinessSearchFragment.Listener{

    private lateinit var binding: ActivityBusinessSearchListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_search_list)
        showToolbarBackButton(R.string.title_business_location)
        attachFragment()
    }

    private fun attachFragment() {
        addFragmentWithOutAnimation(BusinessSearchFragment(), false, R.id.businessContainer)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, addToBackStack, R.id.businessContainer)
    }

}