package com.sgs.citytax.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityBusinessSearchBinding
import com.sgs.citytax.model.PropertyDetailLocation
import com.sgs.citytax.ui.adapter.ComplaintMapAdapter
import com.sgs.citytax.ui.adapter.IncidentMapAdapter
import com.sgs.citytax.ui.adapter.PropertyMapAdapter
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper
import com.sgs.citytax.util.getDateDifference


class ComplaintIncidentSearchActivity : BaseActivity(), MapFragment.Listener,
        ComplaintIncidentInfoDialogFragment.Listener, PropertyLocationInfoDialogFragment.Listener, IncidentsDialogFragment.Listener,
        ComplaintsDialogFragment.Listener, PropertyDialogFragment.Listener, LocateAgentFragment.Listener, IncidentEntryFragment.Listener, LocateDialogFragment.Listener {

    private lateinit var binding: ActivityBusinessSearchBinding
    private var fromScreen: Any? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var locationHelper: LocationHelper? = null

    private var incidentLocations: ArrayList<IncidentDetailLocation> = arrayListOf()
    private var incidentLocation: IncidentDetailLocation? = null
    private val filterIncidentArrayList: ArrayList<IncidentDetailLocation> = arrayListOf()

    private var complaintLocations: ArrayList<ComplaintIncidentDetailLocation> = arrayListOf()
    private var propertyLocations: ArrayList<PropertyDetailLocation> = arrayListOf()
    private var complaintLocation: ComplaintIncidentDetailLocation? = null
    private var propertyLocation: PropertyDetailLocation? = null
    private val filterComplaintArrayList: ArrayList<ComplaintIncidentDetailLocation> = arrayListOf()
    private val filterPropertyArrayList: ArrayList<PropertyDetailLocation> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_search)
        processIntent()
        setTitle()
        initViews()
        setViews()
        getLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_search_business, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setTitle() {
        when (fromScreen) {
            Constant.NavigationMenu.NAVIGATION_INCIDENT -> showToolbarBackButton(R.string.title_incident_location)
            Constant.NavigationMenu.NAVIGATION_COMPLAINT -> showToolbarBackButton(R.string.title_complaints_location)
            Constant.NavigationMenu.NAVIGATION_PROPERTY -> showToolbarBackButton(R.string.title_property_location)
            else -> {

            }
        }
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_NAVIGATION_MENU))
                fromScreen = intent.getSerializableExtra(Constant.KEY_NAVIGATION_MENU) as? Constant.NavigationMenu
        }
    }

    private fun initViews() {
        binding.rcvMapList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        drawerToggle = ActionBarDrawerToggle(this, binding.drwLayout, R.string.open, R.string.close)
        drawerToggle?.isDrawerIndicatorEnabled = false
    }

    private fun setViews() {
        drawerToggle = object : ActionBarDrawerToggle(this, binding.drwLayout, R.string.open, R.string.close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu()
            }
        }

        binding.drwLayout.addDrawerListener(drawerToggle as ActionBarDrawerToggle)
        (drawerToggle as ActionBarDrawerToggle).isDrawerIndicatorEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        if (item.itemId == R.id.action_search) {
            when (fromScreen) {
                Constant.NavigationMenu.NAVIGATION_INCIDENT -> {
                    val dialogFragment: IncidentsDialogFragment = IncidentsDialogFragment.newInstance(incidentLocations, incidentLocation)
                    this.supportFragmentManager.let {
                        binding.drwLayout.closeDrawers()
                        val dialog = dialogFragment
                        dialogFragment.show(it, IncidentsDialogFragment::class.java.simpleName)
                    }
                }
                Constant.NavigationMenu.NAVIGATION_COMPLAINT -> {
                    val dialogFragment: ComplaintsDialogFragment = ComplaintsDialogFragment.newInstance(complaintLocations, complaintLocation)
                    this.supportFragmentManager.let {
                        binding.drwLayout.closeDrawers()
                        val dialog = dialogFragment
                        dialogFragment.show(it, IncidentsDialogFragment::class.java.simpleName)
                    }
                }
                Constant.NavigationMenu.NAVIGATION_PROPERTY -> {
                    val dialogFragment: PropertyDialogFragment = PropertyDialogFragment.newInstance(propertyLocations, propertyLocation)
                    this.supportFragmentManager.let {
                        binding.drwLayout.closeDrawers()
                        val dialog = dialogFragment
                        dialogFragment.show(it, PropertyDialogFragment::class.java.simpleName)
                    }
                }
                else -> {

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onApplyIncidentClick(incidentDetailsLocation: IncidentDetailLocation?) {
        filterIncidentArrayList.clear()
        incidentLocation = incidentDetailsLocation
        var isSelected = false

        if (incidentLocation != null && incidentLocations.isNotEmpty()) {
            incidentLocation?.incidentNo?.let {
                for ((index, obj) in incidentLocations.withIndex()) {
                    incidentLocations[index].incidentNo?.let { it1 ->
                        if (it1 == it) {
                            filterIncidentArrayList.add(incidentLocations[index])
                            isSelected = true
                        }
                    }
                }
            }

            incidentLocation?.incidentType?.let {
                if (it != getString(R.string.select)) {
                    val filterIncidentType: ArrayList<IncidentDetailLocation> = arrayListOf()
                    filterIncidentType.addAll(filterIncidentArrayList)
                    filterIncidentArrayList.clear()
                    if (filterIncidentType.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterIncidentType.withIndex()) {
                            filterIncidentType[index].incidentType?.let { it2 ->
                                if (it2.contains(it))
                                    filterIncidentArrayList.add(filterIncidentType[index])
                            }
                        }
                    } else if (!isSelected && incidentLocations.isNotEmpty()) {
                        for ((index, obj) in incidentLocations.withIndex()) {
                            incidentLocations[index].incidentType?.let { it2 ->
                                if (it2.contains(it))
                                    filterIncidentArrayList.add(incidentLocations[index])
                            }
                        }
                    }
                }
            }

            incidentLocation?.incidentSubtype?.let {
                if (it != getString(R.string.select)) {
                    val filterSubType: ArrayList<IncidentDetailLocation> = arrayListOf()
                    filterSubType.addAll(filterIncidentArrayList)
                    filterIncidentArrayList.clear()
                    if (filterSubType.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterSubType.withIndex()) {
                            filterSubType[index].incidentSubtype?.let { it2 ->
                                if (it2.contains(it))
                                    filterIncidentArrayList.add(filterSubType[index])
                            }
                        }
                    } else if (!isSelected && incidentLocations.isNotEmpty()) {
                        for ((index, obj) in incidentLocations.withIndex()) {
                            incidentLocations[index].incidentSubtype?.let { it2 ->
                                if (it2.contains(it))
                                    filterIncidentArrayList.add(incidentLocations[index])
                            }
                        }
                    }
                }
            }

            incidentLocation?.fromDate?.let { it ->
                incidentLocation?.toDate?.let { it1 ->
                    if (it.isNotEmpty() && it1.isNotEmpty()) {
                        val filterDate: ArrayList<IncidentDetailLocation> = arrayListOf()
                        filterDate.addAll(filterIncidentArrayList)
                        filterIncidentArrayList.clear()
                        if (filterDate.isNotEmpty()) {
                            isSelected = true
                            for ((index, obj) in filterDate.withIndex()) {
                                filterDate[index].incidentDate?.let { date ->
                                    if (getDateDifference(date, it, it1)) {
                                        filterIncidentArrayList.add(filterDate[index])
                                    }
                                }
                            }
                        } else if (!isSelected && incidentLocations.isNotEmpty()) {
                            for ((index, obj) in incidentLocations.withIndex()) {
                                incidentLocations[index].incidentDate?.let { it2 ->
                                    if (it2.contains(it))
                                        filterIncidentArrayList.add(incidentLocations[index])
                                }
                            }
                        }
                    }
                }
            }


            incidentLocation?.sector?.let {
                if (it != getString(R.string.select)) {
                    val filterSector: ArrayList<IncidentDetailLocation> = arrayListOf()
                    filterSector.addAll(filterIncidentArrayList)
                    filterIncidentArrayList.clear()
                    if (filterSector.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterSector.withIndex()) {
                            filterSector[index].sector?.let { it2 ->
                                if (it2.contains(it))
                                    filterIncidentArrayList.add(filterSector[index])
                            }
                        }
                    } else if (!isSelected && incidentLocations.isNotEmpty()) {
                        for ((index, obj) in incidentLocations.withIndex()) {
                            incidentLocations[index].sector?.let { it2 ->
                                if (it2.contains(it))
                                    filterIncidentArrayList.add(incidentLocations[index])
                            }
                        }
                    }
                }
            }

            incidentLocation?.zone?.let {
                if (it != getString(R.string.select)) {
                    val filterZone: ArrayList<IncidentDetailLocation> = arrayListOf()
                    filterZone.addAll(filterIncidentArrayList)
                    filterIncidentArrayList.clear()
                    if (filterZone.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterZone.withIndex()) {
                            filterZone[index].zone?.let { it2 ->
                                if (it2.contains(it))
                                    filterIncidentArrayList.add(filterZone[index])
                            }
                        }
                    } else if (!isSelected && incidentLocations.isNotEmpty()) {
                        for ((index, obj) in incidentLocations.withIndex()) {
                            incidentLocations[index].zone?.let { it2 ->
                                if (it2.contains(it))
                                    filterIncidentArrayList.add(incidentLocations[index])
                            }
                        }
                    }
                }
            }
        }

        incidentLocation?.status?.let {
            if (it != getString(R.string.select)) {
                val filterStatus: ArrayList<IncidentDetailLocation> = arrayListOf()
                filterStatus.addAll(filterIncidentArrayList)
                filterIncidentArrayList.clear()
                if (filterStatus.isNotEmpty()) {
                    isSelected = true
                    for ((index, obj) in filterStatus.withIndex()) {
                        filterStatus[index].status?.let { it2 ->
                            if (it2.contains(it))
                                filterIncidentArrayList.add(filterStatus[index])
                        }
                    }
                } else if (!isSelected && incidentLocations.isNotEmpty()) {
                    for ((index, obj) in incidentLocations.withIndex()) {
                        incidentLocations[index].status?.let { it2 ->
                            if (it2.contains(it))
                                filterIncidentArrayList.add(incidentLocations[index])
                        }
                    }
                }
            }
        }

        if (filterIncidentArrayList.isNotEmpty()) {
            binding.rcvMapList.adapter = IncidentMapAdapter(filterIncidentArrayList) { incidentDetailLocations: IncidentDetailLocation -> itemIncidentClicked(incidentDetailLocations) }
        } else {
            showSnackbarMsg(R.string.no_record)
            binding.drwLayout.closeDrawers()
        }
        updateMarker()
    }

    override fun onApplyComplaintClick(location: ComplaintIncidentDetailLocation?) {
        filterComplaintArrayList.clear()
        complaintLocation = location
        var isSelected = false

        if (complaintLocation != null && complaintLocations.isNotEmpty()) {
            complaintLocation?.complaintNo?.let {
                for ((index, obj) in complaintLocations.withIndex()) {
                    complaintLocations[index].complaintNo?.let { it1 ->
                        if (it1 == it) {
                            filterComplaintArrayList.add(complaintLocations[index])
                            isSelected = true
                        }
                    }
                }
            }

            complaintLocation?.complaint?.let {
                if (it != getString(R.string.select)) {
                    val filterComplaintType: ArrayList<ComplaintIncidentDetailLocation> = arrayListOf()
                    filterComplaintType.addAll(filterComplaintArrayList)
                    filterComplaintArrayList.clear()
                    if (filterComplaintType.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterComplaintType.withIndex()) {
                            filterComplaintType[index].complaint?.let { it2 ->
                                if (it2.contains(it))
                                    filterComplaintArrayList.add(filterComplaintType[index])
                            }
                        }
                    } else if (!isSelected && complaintLocations.isNotEmpty()) {
                        for ((index, obj) in complaintLocations.withIndex()) {
                            complaintLocations[index].complaint?.let { it2 ->
                                if (it2.contains(it))
                                    filterComplaintArrayList.add(complaintLocations[index])
                            }
                        }
                    }
                }
            }

            complaintLocation?.complaintSubtype?.let {
                if (it != getString(R.string.select)) {
                    val filterSubType: ArrayList<ComplaintIncidentDetailLocation> = arrayListOf()
                    filterSubType.addAll(filterComplaintArrayList)
                    filterComplaintArrayList.clear()
                    if (filterSubType.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterSubType.withIndex()) {
                            filterSubType[index].complaintSubtype?.let { it2 ->
                                if (it2.contains(it))
                                    filterComplaintArrayList.add(filterSubType[index])
                            }
                        }
                    } else if (!isSelected && complaintLocations.isNotEmpty()) {
                        for ((index, obj) in complaintLocations.withIndex()) {
                            complaintLocations[index].complaintSubtype?.let { it2 ->
                                if (it2.contains(it))
                                    filterComplaintArrayList.add(complaintLocations[index])
                            }
                        }
                    }
                }
            }

            complaintLocation?.fromDate?.let { it ->
                complaintLocation?.toDate?.let { it1 ->
                    if (it.isNotEmpty() && it1.isNotEmpty()) {
                        val filterDate: ArrayList<ComplaintIncidentDetailLocation> = arrayListOf()
                        filterDate.addAll(filterComplaintArrayList)
                        filterComplaintArrayList.clear()
                        if (filterDate.isNotEmpty()) {
                            isSelected = true
                            for ((index, obj) in filterDate.withIndex()) {
                                filterDate[index].complaintDate?.let { date ->
                                    if (getDateDifference(date, it, it1)) {
                                        filterComplaintArrayList.add(filterDate[index])
                                    }
                                }
                            }
                        } else if (!isSelected && complaintLocations.isNotEmpty()) {
                            for ((index, obj) in complaintLocations.withIndex()) {
                                complaintLocations[index].complaintDate?.let { it2 ->
                                    if (it2.contains(it))
                                        filterComplaintArrayList.add(complaintLocations[index])
                                }
                            }
                        }
                    }
                }
            }


            complaintLocation?.sector?.let {
                if (it != getString(R.string.select)) {
                    val filterSector: ArrayList<ComplaintIncidentDetailLocation> = arrayListOf()
                    filterSector.addAll(filterComplaintArrayList)
                    filterComplaintArrayList.clear()
                    if (filterSector.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterSector.withIndex()) {
                            filterSector[index].sector?.let { it2 ->
                                if (it2.contains(it))
                                    filterComplaintArrayList.add(filterSector[index])
                            }
                        }
                    } else if (!isSelected && complaintLocations.isNotEmpty()) {
                        for ((index, obj) in complaintLocations.withIndex()) {
                            complaintLocations[index].sector?.let { it2 ->
                                if (it2.contains(it))
                                    filterComplaintArrayList.add(complaintLocations[index])
                            }
                        }
                    }
                }
            }

            complaintLocation?.zone?.let {
                if (it != getString(R.string.select)) {
                    val filterZone: ArrayList<ComplaintIncidentDetailLocation> = arrayListOf()
                    filterZone.addAll(filterComplaintArrayList)
                    filterComplaintArrayList.clear()
                    if (filterZone.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterZone.withIndex()) {
                            filterZone[index].zone?.let { it2 ->
                                if (it2.contains(it))
                                    filterComplaintArrayList.add(filterZone[index])
                            }
                        }
                    } else if (!isSelected && complaintLocations.isNotEmpty()) {
                        for ((index, obj) in complaintLocations.withIndex()) {
                            complaintLocations[index].zone?.let { it2 ->
                                if (it2.contains(it))
                                    filterComplaintArrayList.add(complaintLocations[index])
                            }
                        }
                    }
                }
            }
        }

        complaintLocation?.status?.let {
            if (it != getString(R.string.select)) {
                val filterStatus: ArrayList<ComplaintIncidentDetailLocation> = arrayListOf()
                filterStatus.addAll(filterComplaintArrayList)
                filterComplaintArrayList.clear()
                if (filterStatus.isNotEmpty()) {
                    isSelected = true
                    for ((index, obj) in filterStatus.withIndex()) {
                        filterStatus[index].status?.let { it2 ->
                            if (it2.contains(it))
                                filterComplaintArrayList.add(filterStatus[index])
                        }
                    }
                } else if (!isSelected && complaintLocations.isNotEmpty()) {
                    for ((index, obj) in complaintLocations.withIndex()) {
                        complaintLocations[index].status?.let { it2 ->
                            if (it2.contains(it))
                                filterComplaintArrayList.add(complaintLocations[index])
                        }
                    }
                }
            }
        }

        if (filterComplaintArrayList.isNotEmpty()) {
            binding.rcvMapList.adapter = ComplaintMapAdapter(filterComplaintArrayList) { mComplaintLocations: ComplaintIncidentDetailLocation -> itemComplaintClicked(mComplaintLocations) }
        } else {
            showSnackbarMsg(R.string.no_record)
            binding.drwLayout.closeDrawers()
        }
        updateComplaintMarker()
    }

    override fun onApplyPropertyClick(location: PropertyDetailLocation?) {
        filterPropertyArrayList.clear()
        propertyLocation = location
        var isSelected = false

        if (propertyLocation != null && propertyLocations.isNotEmpty()) {
            propertyLocation?.PropertySycotaxID?.let {
                for ((index, obj) in propertyLocations.withIndex()) {
                    propertyLocations[index].PropertySycotaxID?.let { it1 ->
                        if (it1 == it) {
                            filterPropertyArrayList.add(propertyLocations[index])
                            isSelected = true
                        }
                    }
                }
            }

            propertyLocation?.RegistrationNo?.let {
                if (it != getString(R.string.select)) {
                    val filterPropertyDetailLocation: ArrayList<PropertyDetailLocation> = arrayListOf()
                    filterPropertyDetailLocation.addAll(filterPropertyArrayList)
                    filterPropertyArrayList.clear()
                    if (filterPropertyDetailLocation.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterPropertyDetailLocation.withIndex()) {
                            filterPropertyDetailLocation[index].RegistrationNo?.let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(filterPropertyDetailLocation[index])
                            }
                        }
                    } else if (!isSelected && propertyLocations.isNotEmpty()) {
                        for ((index, obj) in propertyLocations.withIndex()) {
                            propertyLocations[index].RegistrationNo?.let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(propertyLocations[index])
                            }
                        }
                    }
                }
            }
            propertyLocation?.PropertyType?.let {
                if (it != getString(R.string.select)) {
                    val filterPropertyDetailLocation: ArrayList<PropertyDetailLocation> = arrayListOf()
                    filterPropertyDetailLocation.addAll(filterPropertyArrayList)
                    filterPropertyArrayList.clear()
                    if (filterPropertyDetailLocation.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterPropertyDetailLocation.withIndex()) {
                            filterPropertyDetailLocation[index].PropertyType?.let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(filterPropertyDetailLocation[index])
                            }
                        }
                    } else if (!isSelected && propertyLocations.isNotEmpty()) {
                        for ((index, obj) in propertyLocations.withIndex()) {
                            propertyLocations[index].PropertyType?.let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(propertyLocations[index])
                            }
                        }
                    }
                }
            }
            propertyLocation?.Zone?.let {
                if (it != getString(R.string.select)) {
                    val filterPropertyDetailLocation: ArrayList<PropertyDetailLocation> = arrayListOf()
                    filterPropertyDetailLocation.addAll(filterPropertyArrayList)
                    filterPropertyArrayList.clear()
                    if (filterPropertyDetailLocation.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterPropertyDetailLocation.withIndex()) {
                            filterPropertyDetailLocation[index].Zone?.let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(filterPropertyDetailLocation[index])
                            }
                        }
                    } else if (!isSelected && propertyLocations.isNotEmpty()) {
                        for ((index, obj) in propertyLocations.withIndex()) {
                            propertyLocations[index].Zone?.let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(propertyLocations[index])
                            }
                        }
                    }
                }
            }

            propertyLocation?.Sector?.let {
                if (it != getString(R.string.select)) {
                    val filterPropertyDetailLocation: ArrayList<PropertyDetailLocation> = arrayListOf()
                    filterPropertyDetailLocation.addAll(filterPropertyArrayList)
                    filterPropertyArrayList.clear()
                    if (filterPropertyDetailLocation.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterPropertyDetailLocation.withIndex()) {
                            filterPropertyDetailLocation[index].Sector?.let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(filterPropertyDetailLocation[index])
                            }
                        }
                    } else if (!isSelected && propertyLocations.isNotEmpty()) {
                        for ((index, obj) in propertyLocations.withIndex()) {
                            propertyLocations[index].Sector?.let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(propertyLocations[index])
                            }
                        }
                    }
                }
            }

            propertyLocation?.OnboardingMonth.toString().let {
                if (it != "-1") {
                    val filterPropertyDetailLocation: ArrayList<PropertyDetailLocation> = arrayListOf()
                    filterPropertyDetailLocation.addAll(filterPropertyArrayList)
                    filterPropertyArrayList.clear()
                    if (filterPropertyDetailLocation.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterPropertyDetailLocation.withIndex()) {
                            filterPropertyDetailLocation[index].OnboardingMonth.toString().let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(filterPropertyDetailLocation[index])
                            }
                        }
                    } else if (!isSelected && propertyLocations.isNotEmpty()) {
                        for ((index, obj) in propertyLocations.withIndex()) {
                            propertyLocations[index].OnboardingMonth.toString().let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(propertyLocations[index])
                            }
                        }
                    }
                }
            }

            propertyLocation?.OnboardingYear.toString().let {
                if (it != "-1") {
                    val filterPropertyDetailLocation: ArrayList<PropertyDetailLocation> = arrayListOf()
                    filterPropertyDetailLocation.addAll(filterPropertyArrayList)
                    filterPropertyArrayList.clear()
                    if (filterPropertyDetailLocation.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterPropertyDetailLocation.withIndex()) {
                            filterPropertyDetailLocation[index].OnboardingYear.toString().let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(filterPropertyDetailLocation[index])
                            }
                        }
                    } else if (!isSelected && propertyLocations.isNotEmpty()) {
                        for ((index, obj) in propertyLocations.withIndex()) {
                            propertyLocations[index].OnboardingYear.toString().let { it2 ->
                                if (it2.contains(it))
                                    filterPropertyArrayList.add(propertyLocations[index])
                            }
                        }
                    }
                }
            }
        }

        if (filterPropertyArrayList.isNotEmpty()) {
            binding.rcvMapList.adapter = PropertyMapAdapter(filterPropertyArrayList) { propertyDetailsLocation: PropertyDetailLocation -> itemPropertyClicked(propertyDetailsLocation) }
        } else {
            showSnackbarMsg(R.string.no_record)
            binding.drwLayout.closeDrawers()
        }
        updatePropertyMarker()
    }

    override fun onClearComplaintClick() {
        incidentLocation = null
        filterIncidentArrayList.clear()
        binding.drwLayout.closeDrawers()
        binding.rcvMapList.adapter = ComplaintMapAdapter(complaintLocations) { mComplaintLocation: ComplaintIncidentDetailLocation -> itemComplaintClicked(mComplaintLocation) }
        updateComplaintMarker()
    }

    override fun onClearPropertyClick() {
        propertyLocation = null
        filterPropertyArrayList.clear()
        binding.drwLayout.closeDrawers()
        binding.rcvMapList.adapter = PropertyMapAdapter(propertyLocations) { propertyDetailsLocation: PropertyDetailLocation -> itemPropertyClicked(propertyDetailsLocation) }
        updatePropertyMarker()
    }

    private fun updateMarker() {
        val fragment = supportFragmentManager.findFragmentById(R.id.businessSearchContainer) as LocateAgentFragment
        if (filterIncidentArrayList.isNotEmpty())
            fragment.updateMarker(filterIncidentArrayList)
        else {
            fragment.updateMarker(incidentLocations)
            binding.rcvMapList.adapter = IncidentMapAdapter(incidentLocations) { incidentDetailLocations: IncidentDetailLocation -> itemIncidentClicked(incidentDetailLocations) }
        }
    }

    private fun updateComplaintMarker() {
        val fragment = supportFragmentManager.findFragmentById(R.id.businessSearchContainer) as LocateAgentFragment
        if (filterComplaintArrayList.isNotEmpty())
            fragment.updateComplaintMarker(filterComplaintArrayList)
        else {
            fragment.updateComplaintMarker(complaintLocations)
            binding.rcvMapList.adapter = ComplaintMapAdapter(complaintLocations) { mComplaintLocation: ComplaintIncidentDetailLocation -> itemComplaintClicked(mComplaintLocation) }
        }
    }

    private fun updatePropertyMarker() {
        val fragment = supportFragmentManager.findFragmentById(R.id.businessSearchContainer) as LocateAgentFragment
        if (filterPropertyArrayList.isNotEmpty())
            fragment.updatePropertyMarker(filterPropertyArrayList)
        else {
            fragment.updatePropertyMarker(propertyLocations)
            binding.rcvMapList.adapter = PropertyMapAdapter(filterPropertyArrayList) { propertyDetailsLocation: PropertyDetailLocation -> itemPropertyClicked(propertyDetailsLocation) }
        }
    }

    override fun onClick() {
        binding.drwLayout.closeDrawers()
       /* binding.rcvMapList.visibility = View.GONE
        binding.rcvMapList.adapter = null*/
    }

    private fun getLocation() {
        showProgressDialog(R.string.msg_location_fetching)
        locationHelper = LocationHelper(this, binding.rcvMapList, this)
        locationHelper?.fetchLocation()
        locationHelper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                dismissDialog()
                val latLong = LatLng(latitude, longitude)
                fromScreen?.let {
                    addFragment(LocateAgentFragment.newInstance(latLong, it), false, R.id.businessSearchContainer)
//                    getComplaintsIncidentLocations()
                }
            }

            override fun start() {
            }
        })
    }

    override fun onClearIncidentClick() {
        incidentLocation = null
        filterIncidentArrayList.clear()
        binding.drwLayout.closeDrawers()
        binding.rcvMapList.adapter = IncidentMapAdapter(incidentLocations) { incidentDetailsLocations: IncidentDetailLocation -> itemIncidentClicked(incidentDetailsLocations) }
        updateMarker()
    }

    private fun itemIncidentClicked(incidentDetailsLocations: IncidentDetailLocation) {
        val location: IncidentDetailLocation = incidentDetailsLocations
        val dialogFragment: ComplaintIncidentInfoDialogFragment = ComplaintIncidentInfoDialogFragment.newInstance(location, null, fromScreen)
        this.supportFragmentManager.let {
            dialogFragment.show(it, ComplaintIncidentInfoDialogFragment::class.java.simpleName)
        }
    }

    private fun itemComplaintClicked(complaintIncidentDetailLocation: ComplaintIncidentDetailLocation) {
        val location: ComplaintIncidentDetailLocation = complaintIncidentDetailLocation
        val dialogFragment: ComplaintIncidentInfoDialogFragment = ComplaintIncidentInfoDialogFragment.newInstance(null, location, fromScreen)
        this.supportFragmentManager.let {
            dialogFragment.show(it, ComplaintIncidentInfoDialogFragment::class.java.simpleName)
        }
    }

    private fun itemPropertyClicked(propertyDetailLocation: PropertyDetailLocation) {
        val dialogFragment = PropertyLocationInfoDialogFragment.newInstance(propertyDetailLocation, fromScreen)
        this.supportFragmentManager.let {
            dialogFragment.show(it, PropertyLocationInfoDialogFragment::class.java.simpleName)
        }
    }

    override fun addFragment(fragment: Fragment, addToBackStack: Boolean) {
        addFragment(fragment, true, R.id.businessSearchContainer)
    }

    override fun updateData(any: ArrayList<*>, navigation: Constant.NavigationMenu) {
        when (navigation) {
            Constant.NavigationMenu.NAVIGATION_INCIDENT -> {
                incidentLocations.clear()
                incidentLocation=null
                incidentLocations.addAll(any as ArrayList<IncidentDetailLocation>)
                binding.rcvMapList.adapter = IncidentMapAdapter(incidentLocations) { incidentDetailLocations: IncidentDetailLocation -> itemIncidentClicked(incidentDetailLocations) }
            }
            Constant.NavigationMenu.NAVIGATION_COMPLAINT -> {
                complaintLocations.clear()
                complaintLocation=null
                complaintLocations.addAll(any as ArrayList<ComplaintIncidentDetailLocation>)
                binding.rcvMapList.adapter = ComplaintMapAdapter(complaintLocations) { mComplaintLocation: ComplaintIncidentDetailLocation -> itemComplaintClicked(mComplaintLocation) }
            }
            Constant.NavigationMenu.NAVIGATION_PROPERTY -> {
                propertyLocations.clear()
                propertyLocation=null
                propertyLocations.addAll(any as ArrayList<PropertyDetailLocation>)
                binding.rcvMapList.adapter = PropertyMapAdapter(propertyLocations) { propertyDetailsLocation: PropertyDetailLocation -> itemPropertyClicked(propertyDetailsLocation) }
            }
        }
    }

    override fun onLatLonFound(latitude: Double?, longitude: Double?) {
        latitude?.let { lat ->
            longitude?.let {
                val fragment = this.supportFragmentManager.findFragmentById(R.id.businessSearchContainer)
                if (fragment is IncidentEntryFragment)
                    fragment.updateText(lat, it)
            }
        }
    }

    override fun onReloadClick() {
    }
}