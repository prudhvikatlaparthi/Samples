package com.pru.hiltarchi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.pru.hiltarchi.R
import com.pru.hiltarchi.databinding.ActivityMainBinding
import com.pru.hiltarchi.listeners.OnBackPressedListener
import com.pru.hiltarchi.listeners.OnResumeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val currentFragment: Fragment =
            navHostFragment.childFragmentManager.fragments.get(0)
        if (currentFragment is OnBackPressedListener) {
            currentFragment.onBackPressCalled()
            return false
        } else {
            return navController.navigateUp() || super.onSupportNavigateUp()
        }
    }


    override fun onBackPressed() {
        val currentFragment: Fragment =
            navHostFragment.childFragmentManager.fragments.get(0)
        if (currentFragment is OnBackPressedListener) currentFragment.onBackPressCalled() else if (!navController.popBackStack()) super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        val currentFragment: Fragment =
            navHostFragment.childFragmentManager.fragments[0]
        if (currentFragment is OnResumeListener)
            (currentFragment as OnResumeListener).onResumedCalled()
    }

}