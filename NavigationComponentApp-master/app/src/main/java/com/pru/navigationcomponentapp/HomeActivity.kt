package com.pru.navigationcomponentapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.pru.navigationcomponentapp.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    lateinit var homeBinding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navHostFragment: NavHostFragment
    private val TAG = "HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        appBarConfiguration =
            AppBarConfiguration(
                setOf(R.id.nav_one_fragment, R.id.nav_two_fragment, R.id.nav_three_fragment),
                homeBinding.drawerLayout
            )
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, homeBinding.drawerLayout, R.string.open, R.string.close)
        homeBinding.drawerLayout.addDrawerListener(actionBarDrawerToggle)

        NavigationUI.setupWithNavController(homeBinding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            run {
                Log.i(
                    TAG,
                    "onCreate: $controller ${destination.label} $arguments ${supportActionBar?.title}"
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val currentFragment: Fragment =
            navHostFragment.childFragmentManager.fragments.get(0)
        if (currentFragment is OnBackPressedListener) {
            (currentFragment as OnBackPressedListener).onBackPressed()

            return false
        } else {
            return  NavigationUI.navigateUp(
                navController,
                appBarConfiguration
            ) || super.onSupportNavigateUp()
        }
    }


    override fun onBackPressed() {
        if (homeBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            homeBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val currentFragment: Fragment =
                navHostFragment.childFragmentManager.fragments.get(0)
            if (currentFragment is OnBackPressedListener) (currentFragment as OnBackPressedListener).onBackPressed() else if (!navController.popBackStack()) super.onBackPressed()
        }
    }

    fun lockNavigationDrawer() {
        homeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun unLockNavigationDrawer() {
        homeBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}