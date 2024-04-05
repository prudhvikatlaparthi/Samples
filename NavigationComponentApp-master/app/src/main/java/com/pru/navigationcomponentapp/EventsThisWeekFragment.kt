package com.pru.navigationcomponentapp

import android.os.Bundle
import android.view.View
import com.pru.navigationcomponentapp.databinding.FragmentEventsThisWeekBinding

class EventsThisWeekFragment : BaseFragment(R.layout.fragment_events_this_week) {

    private lateinit var eventsThisWeekBinding: FragmentEventsThisWeekBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventsThisWeekBinding = FragmentEventsThisWeekBinding.bind(view)


    }
}