package cloud.mariapps.chatapp

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import cloud.mariapps.chatapp.databinding.ActivityMainBinding
import cloud.mariapps.chatapp.listeners.OnBackPressListener
import cloud.mariapps.chatapp.navigation.AppController
import cloud.mariapps.chatapp.ui.composables.Alert
import cloud.mariapps.chatapp.ui.composables.AlertItem
import cloud.mariapps.chatapp.ui.composables.Loader
import cloud.mariapps.chatapp.ui.home.HomeFragment
import cloud.mariapps.chatapp.ui.login.LoginFragment
import cloud.mariapps.chatapp.utils.Global
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navHostFragment: NavHostFragment
    private var loaderMessage: String = ""
    private var loaderState by mutableStateOf(false)
    private var inlineProgressState by mutableStateOf(false)
    private var alertItem = AlertItem(message = "",
        posBtnText = "",
        negBtnText = null,
        posBtnListener = {},
        negBtnListener = {})
    private var alertDialogState by mutableStateOf(false)

    @Inject
    lateinit var appController: AppController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment), binding.drawerLayout
        )
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.setupWithNavController(navController)

        binding.composeView.setContent {
            Loader(message = loaderMessage, showLoader = loaderState, onDismissRequest = {
                loaderState = false
            })
            Alert(
                showAlert = alertDialogState,
                alertTitle = alertItem.alertTitle,
                message = alertItem.message,
                posBtnText = alertItem.posBtnText,
                negBtnText = alertItem.negBtnText,
                posBtnListener = {
                    alertDialogState = false
                    alertItem.posBtnListener.invoke()
                },
                negBtnListener = {
                    alertItem.negBtnListener?.let {
                        alertDialogState = false
                        it.invoke()
                    }
                },
                onDismissRequest = {
                    alertDialogState = false
                },
                dismissOnBackPress = alertItem.dismissOnBackPress,
                dismissOnClickOutside = alertItem.dismissOnClickOutside
            )
        }

        binding.pbView.setContent {
            if (inlineProgressState) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        setEvents()
        backPressCallback()
        observeNavigation()
    }

    private fun observeNavigation() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                appController.navChannel.collect { intent ->
                    when (intent) {
                        is AppController.ControlIntent.Navigate -> {
                            navController.navigate(
                                intent.resId, navOptions = intent.navOptions, args = null
                            )
                        }
                        is AppController.ControlIntent.NavigateDirections -> {
                            navController.navigate(
                                intent.directions, navOptions = intent.navOptions
                            )
                        }
                        is AppController.ControlIntent.NavigateUp -> {
                            navController.navigateUp()
                        }
                        is AppController.ControlIntent.PopBackStack -> {
                            navController.popBackStack()
                        }
                        is AppController.ControlIntent.PopBackStackWithID -> {
                            navController.popBackStack(
                                destinationId = intent.destinationId, inclusive = intent.inclusive
                            )
                        }
                        is AppController.ControlIntent.DismissLoader -> {
                            loaderMessage = Global.getString(R.string.loading)
                            loaderState = false
                            inlineProgressState = false
                        }
                        is AppController.ControlIntent.ShowLoader -> {
                            if (intent.isForInlineProgress) {
                                inlineProgressState = true
                            } else {
                                loaderMessage = intent.message
                                loaderState = true
                            }
                        }
                        is AppController.ControlIntent.ShowAlertDialog -> {
                            alertItem = intent.alertItem
                            alertDialogState = true
                        }
                    }
                }
            }
        }
    }

    private fun setEvents() {
        binding.tvChat.setOnClickListener {
            checkDrawer()
        }
    }

    private fun getCurrentFragment(): Fragment? {
        return navHostFragment.childFragmentManager.fragments.getOrNull(0)
    }

    private fun isDrawerOpen(): Boolean = binding.drawerLayout.isDrawerOpen(GravityCompat.START)

    private fun closeDrawer() = binding.drawerLayout.closeDrawer(GravityCompat.START)

    private fun checkDrawer() {
        if (isDrawerOpen()) {
            closeDrawer()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    fun hideActionBarCheck(
        title: String? = null, hideActionBar: Boolean, hideBackArrow: Boolean
    ) {
        if (hideActionBar) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.title = title ?: getString(R.string.empty)
            supportActionBar?.show()
            supportActionBar?.setDisplayHomeAsUpEnabled(!hideBackArrow)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val currentFragment = getCurrentFragment()
        return if (currentFragment is OnBackPressListener && currentFragment !is HomeFragment) {
            currentFragment.backPress()
            false
        } else {
            NavigationUI.navigateUp(
                navController, appBarConfiguration
            ) || super.onSupportNavigateUp()
        }
    }

    fun lockNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun unLockNavigationDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    private fun backPressCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitOnBackPressed()
            }
        })
    }

    private fun exitOnBackPressed() {
        if (isDrawerOpen()) {
            closeDrawer()
            return
        }
        when (val currentFragment = getCurrentFragment()) {
            is OnBackPressListener -> {
                currentFragment.backPress()
            }
            is LoginFragment -> {
                finish()
            }
            else -> {
                navController.popBackStack()
            }
        }
    }
}