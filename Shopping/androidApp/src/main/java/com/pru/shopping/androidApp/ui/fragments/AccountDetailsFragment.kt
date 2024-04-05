package com.pru.shopping.androidApp.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.pru.shopping.androidApp.R
import com.pru.shopping.androidApp.databinding.FragmentAccountDetailsBinding
import com.pru.shopping.androidApp.ui.BaseFragment

class AccountDetailsFragment : BaseFragment(R.layout.fragment_account_details) {

    private lateinit var accountDetailsBinding: FragmentAccountDetailsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountDetailsBinding = FragmentAccountDetailsBinding.bind(view)
        setupToolBar("Account Details", false, hideActionBar = false)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.call_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_call -> {
                Toast.makeText(requireContext(), "Call", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}