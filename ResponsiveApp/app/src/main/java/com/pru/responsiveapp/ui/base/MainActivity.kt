package com.pru.responsiveapp.ui.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.pru.responsiveapp.R
import com.pru.responsiveapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = if (isTablet()) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        setupViews()
    }

    private fun setupViews() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        activityMainBinding.myToolBarBack.setOnClickListener {
            onBackPressed()
        }
    }

    fun isTablet(): Boolean = resources.getBoolean(R.bool.isTablet)

    /*override fun onBackPressed() {
        if (navHostFragment.childFragmentManager.fragments[0] is BackPressListener){
            (navHostFragment.childFragmentManager.fragments[0] as BackPressListener).onBackPress(false)
        } else {
            super.onBackPressed()
        }
    }*/
}