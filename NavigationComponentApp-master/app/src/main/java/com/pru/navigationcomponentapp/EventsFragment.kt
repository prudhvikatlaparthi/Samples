package com.pru.navigationcomponentapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.pru.navigationcomponentapp.databinding.EventsFragmentBinding


class EventsFragment : BaseFragment(R.layout.events_fragment) {

    private lateinit var eventsFragmentBinding: EventsFragmentBinding
    private val TAG = "EventsFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventsFragmentBinding = EventsFragmentBinding.bind(view)
        setupToolBar("Events")

        eventsFragmentBinding.bottomEventsMenu.setOnNavigationItemSelectedListener { item ->
            Log.i(TAG, "onNavigationItemSelected: " + item.itemId)
            true
        }
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.bottom_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        eventsFragmentBinding.bottomEventsMenu
            .setupWithNavController(navController)
    }
}