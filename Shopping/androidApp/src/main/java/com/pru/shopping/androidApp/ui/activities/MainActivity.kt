package com.pru.shopping.androidApp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.pru.shopping.androidApp.R
import com.pru.shopping.androidApp.databinding.ActivityMainBinding
import com.pru.shopping.androidApp.ui.BaseActivity
import com.pru.shopping.androidApp.utils.getDeviceMeasurements
import com.pru.shopping.androidApp.utils.toDp
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    lateinit var activityBinding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val TAG = "FireBase"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.homeFragment,
                ),
                activityBinding.drawerLayout
            )
        setSupportActionBar(activityBinding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        activityBinding.navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            run {
                Log.i(
                    TAG,
                    "onCreate: $controller ${destination.label} $arguments ${supportActionBar?.title}"
                )
            }
        }
        setupBottomAppBarViews()

        setupListeners()
        activityBinding.tvInboxView.setOnClickListener {
            navController.navigate(R.id.action_global_sampleBottomSheet)
        }
        activityBinding.checkOpen.setOnClickListener {
            navController.navigate(R.id.action_global_sampleBottomSheet)
        }
    }

    private fun setupListeners() {
        /*val headerView = activityBinding.navView.getHeaderView(0)

        val updateView = headerView.findViewById<TextView>(R.id.tv_update)
        val profileView = headerView.findViewById<FrameLayout>(R.id.fl_profile)

        updateView.setOnClickListener {
            navController.navigate(R.id.action_global_profileFragment)
        }

        profileView.setOnClickListener {
            navController.navigate(R.id.action_global_profileFragment)
        }

        activityBinding.fabCartIcon.setOnClickListener {
            Toast.makeText(this, "Fab", Toast.LENGTH_SHORT).show()
        }

        activityBinding.llAmountWrapper.setOnClickListener {
            Toast.makeText(this, "Credit", Toast.LENGTH_SHORT).show()
        }

        activityBinding.tvShoppingView.setOnClickListener {
            Toast.makeText(this, "Shopping", Toast.LENGTH_SHORT).show()
        }

        activityBinding.tvInboxView.setOnClickListener {
            Toast.makeText(this, "Inbox", Toast.LENGTH_SHORT).show()
        }*/
    }

    private fun setupBottomAppBarViews() {
        val deviceMeasure = getDeviceMeasurements()
        val viewWidth = deviceMeasure.widthPixels / 5
        val amountWrapperSize = (viewWidth * 1.8).toInt()
        val layoutParams =
            LinearLayout.LayoutParams(amountWrapperSize, LinearLayout.LayoutParams.MATCH_PARENT)
        activityBinding.llAmountWrapper.layoutParams = layoutParams
        val remainingSize = deviceMeasure.widthPixels - (amountWrapperSize + viewWidth)

        val layoutParams1 =
            LinearLayout.LayoutParams((remainingSize / 2), LinearLayout.LayoutParams.MATCH_PARENT)
        activityBinding.tvInboxView.layoutParams = layoutParams1
        activityBinding.tvShoppingView.layoutParams = layoutParams1

        val layoutParams2 =
            RelativeLayout.LayoutParams(viewWidth, viewWidth)
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_END)
        layoutParams2.setMargins(0, 0, 5.toDp(), (viewWidth / 1.5).toInt().toDp())

        activityBinding.fabCartIcon.layoutParams = layoutParams2
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }


    override fun onBackPressed() {
        if (activityBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun lockNavigationDrawer() {
        activityBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun unLockNavigationDrawer() {
        activityBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}
