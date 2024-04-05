package com.pru.singleactivity.ui.dashboard

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.pru.singleactivity.R
import com.pru.singleactivity.base.BaseFragment
import com.pru.singleactivity.databinding.FragmentDashBoardBinding
import com.pru.singleactivity.ui.address.AddressArgs
import com.pru.singleactivity.ui.address.AddressDialogFragment
import com.pru.singleactivity.ui.address.AddressResult
import com.pru.singleactivity.ui.settings.SettingsArgs
import com.pru.singleactivity.ui.settings.SettingsFragment
import com.pru.singleactivity.ui.settings.SettingsResult
import com.pru.singleactivity.utils.CommonUtils.navigate
import com.pru.singleactivity.utils.CommonUtils.setResultListener

class DashBoardFragment :
    BaseFragment<FragmentDashBoardBinding>(FragmentDashBoardBinding::inflate) {

    private lateinit var toggle: ActionBarDrawerToggle
    override fun resume() {
        super.resume()
        setHasOptionsMenu(true)
    }
    override fun setup() {
        setBack(true)
        toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun observers() {
        setResultListener {
            when (it) {
                is SettingsResult -> {
                    Toast.makeText(requireContext(), "Setting ${it.settingId}", Toast.LENGTH_SHORT)
                        .show()
                    if (it.isFromAddressDialog) {
                        binding.addressBtn.performClick()
                    }
                }
                is AddressResult -> {
                    Toast.makeText(requireContext(), "Address ${it.isRefresh}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun listeners() {
        val userId = (0..100).random()
        binding.settingsBtn.setOnClickListener {
            navigate(
                SettingsFragment(), args = SettingsArgs(userId = userId)
            )
        }
        binding.addressBtn.setOnClickListener {
            navigate(AddressDialogFragment(), args = AddressArgs(userId = userId))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> toggle.onOptionsItemSelected(menuItem)
            R.id.action_settings -> {
                navigate(fragment = SettingsFragment(), args = SettingsArgs(userId = 56))
                true
            }
            else -> false
        }
    }

    /*fun optionsItemSelected(item: MenuItem) {
        toggle.onOptionsItemSelected(item)
    }*/
}