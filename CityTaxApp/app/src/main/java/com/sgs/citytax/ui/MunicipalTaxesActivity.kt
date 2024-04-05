package com.sgs.citytax.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.databinding.ActivityMunicipalTaxesBinding
import com.sgs.citytax.model.ProductDetails
import com.sgs.citytax.ui.adapter.MunicipalTaxesAdapter
import com.sgs.citytax.util.Constant

class MunicipalTaxesActivity : BaseActivity(), SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityMunicipalTaxesBinding
    private var searchView: SearchView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_municipal_taxes)
        if (prefHelper.agentTypeCode == Constant.AgentTypeCode.LEA.name || prefHelper.agentTypeCode == Constant.AgentTypeCode.LEI.name || prefHelper.agentTypeCode == Constant.AgentTypeCode.LES.name) {
            showToolbarBackButton(R.string.title_law_enforcement_taxes)
        } else if(prefHelper.allowParking == "Y"){
            showToolbarBackButton(R.string.title_parking_taxes)
        }else {
            showToolbarBackButton(R.string.title_products)
        }
        bindData()
    }

    private fun bindData() {
        showProgressDialog()
        APICall.getProducts(object : ConnectionCallBack<List<ProductDetails>> {
            override fun onSuccess(response: List<ProductDetails>) {
                binding.recyclerView.adapter = MunicipalTaxesAdapter(response)
                binding.recyclerView.addItemDecoration(DividerItemDecoration(this@MunicipalTaxesActivity,
                        DividerItemDecoration.VERTICAL))
                binding.recyclerView.itemAnimator = DefaultItemAnimator()
                dismissDialog()
            }

            override fun onFailure(message: String) {
                binding.recyclerView.adapter = null
                dismissDialog()
                showAlertDialog(message)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_products, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView = (menu?.findItem(R.id.app_bar_search)?.actionView as SearchView)

        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        searchView?.setOnQueryTextListener(this)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        (binding.recyclerView.adapter as MunicipalTaxesAdapter).filter.filter(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        (binding.recyclerView.adapter as MunicipalTaxesAdapter).filter.filter(query)
        return true
    }

    override fun onBackPressed() {
        if (!searchView?.isIconified!!) {
            searchView?.isIconified = true
        } else {
            super.onBackPressed()
        }
    }
}