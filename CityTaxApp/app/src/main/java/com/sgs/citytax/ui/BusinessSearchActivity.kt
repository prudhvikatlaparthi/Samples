package com.sgs.citytax.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.BusinessMapFilterdata
import com.sgs.citytax.api.payload.GetDropdownFiltersForBusinessSearch
import com.sgs.citytax.api.payload.GetDropdownFiltersForLAWSearchResponse
import com.sgs.citytax.api.payload.ImpondmentMapFilterData
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.BaseActivity
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.ActivityBusinessSearchBinding
import com.sgs.citytax.model.BusinessLocations
import com.sgs.citytax.model.LawPendingTransactionLocations
import com.sgs.citytax.ui.adapter.BusinessMapAdapter
import com.sgs.citytax.ui.adapter.LawAgentsMapAdapter
import com.sgs.citytax.ui.fragments.*
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.LocationHelper
import com.sgs.citytax.util.LogHelper
import com.sgs.citytax.util.Pagination


class BusinessSearchActivity : BaseActivity(), MapFragment.Listener, BusinessSearchDialogFragment.Listener,
        BusinessInfoDialogFragment.Listener, LawPendingTransactionInfoDialogFragment.Listener, LawPendingTransactionsSearchDialogFragment.Listener {

    private lateinit var binding: ActivityBusinessSearchBinding
    private var fromScreen: Constant.NavigationMenu? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var locations: ArrayList<BusinessLocations> = arrayListOf()
    private var mLawPendingTransactionLocations: ArrayList<LawPendingTransactionLocations> = arrayListOf()
    private var location: BusinessLocations? = null
    private val filterArrayList: ArrayList<BusinessLocations> = arrayListOf()
    private val lawFilterArrayList: ArrayList<LawPendingTransactionLocations> = arrayListOf()
    private var mLawPendingTransactionLocation: LawPendingTransactionLocations? = null
    private var locationHelper: LocationHelper? = null
    private var dropdownFiltersForBusinessSearchResponse: GetDropdownFiltersForBusinessSearchResponse? = null
    private var getDropdownFiltersForLAWSearchResponse: GetDropdownFiltersForLAWSearchResponse? = null

    lateinit var pagination: Pagination
    private var businessAdapter: BusinessMapAdapter? = null
    private var lawAgentAdapter: LawAgentsMapAdapter? = null

    private var businessMapFilterdata: BusinessMapFilterdata? = null
    private var impondmentMapFilterData: ImpondmentMapFilterData? = null
    private var latLong = LatLng(12.36566, -1.53388)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_search)
        if (checkAgentType()) {
            showToolbarBackButton(R.string.pending_violoation_impound)
        } else {
            showToolbarBackButton(R.string.title_business_location)
        }
        getLocation()
        processIntent()
        initViews()
        setViews()

        if (checkAgentType()) {
            pagination = Pagination(1, 20, binding.rcvMapList) { pageNumber, PageSize ->
                getLawAgentLocations(pageNumber, PageSize)
            }
            pagination.setDefaultValues()
        } else {
            getBusinessLocations()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filter_search_business, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun processIntent() {
        intent?.let {
            if (intent.hasExtra(Constant.KEY_QUICK_MENU))
                fromScreen = intent.getSerializableExtra(Constant.KEY_QUICK_MENU) as? Constant.NavigationMenu
        }
    }

    private fun initViews() {
        binding.rcvMapList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        drawerToggle = ActionBarDrawerToggle(this, binding.drwLayout, R.string.open, R.string.close)
        drawerToggle?.isDrawerIndicatorEnabled = false
        addFragment(MapFragment.newInstance(latLong), false, R.id.businessSearchContainer)
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

    private fun getBusinessLocations(isFromReload:Boolean = false) {
        if (MyApplication.getPrefHelper().assignedZoneCode == Constant.AssignedZoneSectorCode.NZ.name) {
            return
        }
        if (isFromReload) {
            showProgressDialog()
        }
        val accountId: Int = MyApplication.getPrefHelper().accountId
        APICall.getBusinessLocationForAgent(accountId, object : ConnectionCallBack<BusinessLocation4Agent> {
            override fun onSuccess(response: BusinessLocation4Agent) {
                dismissDialog()
                locations.clear()
                locations.addAll(response.businessLocations as ArrayList<BusinessLocations>)
                if (locations.size > 0) {
                    setData(response.businessLocations as ArrayList<BusinessLocations>)
                } else {
                    showAlertDialog(getString(R.string.msg_no_data))
                }
            }

            override fun onFailure(message: String) {
                dismissDialog()
                if (message == getString(R.string.msg_no_data) || message.isEmpty()) {
                    showAlertDialog(getString(R.string.msg_no_data))
                }
            }
        })
    }

    private fun getLawAgentLocations(pageNumber: Int, PageSize: Int) {
        if (MyApplication.getPrefHelper().assignedZoneCode == Constant.AssignedZoneSectorCode.NZ.name) {
            return
        }
        val accountId: Int = MyApplication.getPrefHelper().agentID
        APICall.getLawPendingTransactionLocationForAgent(accountId, pageNumber, PageSize, impondmentMapFilterData,
                object : ConnectionCallBack<LAWPendingTransaction4Agent> {
                    override fun onSuccess(response: LAWPendingTransaction4Agent) {
                        if (pageNumber <= 1) {
                            mLawPendingTransactionLocations.clear()
                            pagination.totalRecords = response.totalRecordCounts
                        }
                        mLawPendingTransactionLocations.addAll(response.lawPendingTransactionLocations as ArrayList<LawPendingTransactionLocations>)
                        setLawData(response.lawPendingTransactionLocations as ArrayList<LawPendingTransactionLocations>)
                    }

                    override fun onFailure(message: String) {
                        if (message == getString(R.string.msg_no_data)) {
                            showAlertDialog(message)
                        }
                    }
                })
    }

    private fun setData(mLocations: ArrayList<BusinessLocations>?) {
        mLocations?.reverse()
        if (businessAdapter == null) {
            businessAdapter = BusinessMapAdapter(mLocations!!) { businessLocations: BusinessLocations -> itemClicked(businessLocations) }
            binding.rcvMapList.adapter = businessAdapter
        } else {
            businessAdapter?.clearList()
            businessAdapter?.updateList(mLocations!!)
        }
        updateMarker()
    }

    private fun setLawData(mLawPendingTransactionLocations: ArrayList<LawPendingTransactionLocations>?) {
        pagination.setIsScrolled(false)
        if (mLawPendingTransactionLocations != null) {
            pagination.stopPagination(mLawPendingTransactionLocations.size)
        } else {
            pagination.stopPagination(0)
        }
        if (lawAgentAdapter == null) {
            lawAgentAdapter = LawAgentsMapAdapter(mLawPendingTransactionLocations!!) { agentLocations: LawPendingTransactionLocations -> lawItemClicked(agentLocations) }
            binding.rcvMapList.adapter = lawAgentAdapter
        } else {
            lawAgentAdapter?.updateList(mLawPendingTransactionLocations!!)
        }
        updateMarker()
        pagination.doNextCall()
    }

    private fun itemClicked(businessLocations: BusinessLocations) {
        val location: BusinessLocations = businessLocations
        val dialogFragment: BusinessInfoDialogFragment = BusinessInfoDialogFragment.newInstance(location)
        this.supportFragmentManager.let {
            dialogFragment.show(it, BusinessInfoDialogFragment::class.java.simpleName)
        }
    }

    private fun lawItemClicked(lawLocations: LawPendingTransactionLocations) {
        val dialogFragment: LawPendingTransactionInfoDialogFragment = LawPendingTransactionInfoDialogFragment.newInstance(lawLocations)
        this.supportFragmentManager.let {
            dialogFragment.show(it, LawPendingTransactionInfoDialogFragment::class.java.simpleName)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        if (item.itemId == R.id.action_search) {
            if (checkAgentType()) {
                //LAW_SEARCH_DIALOG
                if (getDropdownFiltersForLAWSearchResponse == null) {
                    showProgressDialog()
                    val getDropdownFiltersForBusinessSearch = GetDropdownFiltersForBusinessSearch(
                            tablname = "ViolationMapFilter"
                    )

                    APICall.getDropdownFiltersForLAWSearch(getDropdownFiltersForBusinessSearch, object : ConnectionCallBack<GetDropdownFiltersForLAWSearchResponse> {
                        override fun onSuccess(response: GetDropdownFiltersForLAWSearchResponse) {
                            dismissDialog()
                            getDropdownFiltersForLAWSearchResponse = response
                            openLAWSearchDialogFragment()
                        }

                        override fun onFailure(message: String) {
                            dismissDialog()
                        }
                    })
                } else {
                    openLAWSearchDialogFragment()
                }
            } else {
                if (dropdownFiltersForBusinessSearchResponse == null) {
                    showProgressDialog()
                    dropdownFiltersForBusinessSearchResponse = getDropdownFiltersForBusinessSearchData()
                    openBusinessSearchDialogFragment()
                } else {
                    openBusinessSearchDialogFragment()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDropdownFiltersForBusinessSearchData(): GetDropdownFiltersForBusinessSearchResponse {
        val cOMZoneMaster: ArrayList<COMZoneMasterS> = ArrayList()
        val cOMSectors: ArrayList<COMSector> = ArrayList()
        val vUCRMTaxSubTypes: ArrayList<VUCRMTaxSubType> = ArrayList()
        val cRMActivityDomains: ArrayList<CRMActivityDomainS> = ArrayList()
        val cRMActivityClasses: ArrayList<CRMActivityClassS> = ArrayList()
        val vUINVProducts: ArrayList<VUINVProducts> = ArrayList()


        val listTaxType = ArrayList<String>()
        val listSubTaxType = ArrayList<String>()

        for (businessLocation in locations) {
            if (cOMZoneMaster.size > 0) {
                var isContain = false
                for (v in cOMZoneMaster) {
                    if (v.toString() == (businessLocation.zone)) {
                        isContain = true
                        break
                    }
                }
                if (!isContain && !businessLocation.zone.isNullOrEmpty()) {
                    cOMZoneMaster.add(COMZoneMasterS(businessLocation.zone))
                }
            } else {
                if (!businessLocation.zone.isNullOrEmpty()) {
                    cOMZoneMaster.add(COMZoneMasterS(businessLocation.zone))
                }
            }

            if (cOMSectors.size > 0) {
                var isContain = false
                for (v in cOMSectors) {
                    if (v.toString() == (businessLocation.sector)) {
                        isContain = true
                        break
                    }
                }
                if (!isContain && !businessLocation.sector.isNullOrEmpty()) {
                    cOMSectors.add(COMSector(businessLocation.sector))
                }
            } else {
                if (!businessLocation.sector.isNullOrEmpty()) {
                    cOMSectors.add(COMSector(businessLocation.sector))
                }
            }

            if (cRMActivityDomains.size > 0) {
                var isContain = false
                for (v in cRMActivityDomains) {
                    if (v.toString() == (businessLocation.activityDomain)) {
                        isContain = true
                        break
                    }
                }
                if (!isContain && !businessLocation.activityDomain.isNullOrEmpty()) {
                    cRMActivityDomains.add(CRMActivityDomainS(businessLocation.activityDomain))
                }
            } else {
                if (!businessLocation.activityDomain.isNullOrEmpty()) {
                    cRMActivityDomains.add(CRMActivityDomainS(businessLocation.activityDomain))
                }
            }

            if (cRMActivityClasses.size > 0) {
                var isContain = false
                for (v in cRMActivityClasses) {
                    if (v.toString() == (businessLocation.activityClass)) {
                        isContain = true
                        break
                    }
                }
                if (!isContain && !businessLocation.activityClass.isNullOrEmpty()) {
                    cRMActivityClasses.add(CRMActivityClassS(businessLocation.activityClass))
                }
            } else {
                if (!businessLocation.activityClass.isNullOrEmpty()) {
                    cRMActivityClasses.add(CRMActivityClassS(businessLocation.activityClass))
                }
            }

            try {
                if (businessLocation.taxType != null) {
                    businessLocation.taxType?.let {
                        for (t in getTaxTypesList(it)) {
                            if (t.isNotEmpty() && !listTaxType.contains(t)) {
                                listTaxType.add(t)
                                vUINVProducts.add(VUINVProducts(TaxType = t, TaxTypeCode = t))
                            }
                        }
                    }
                }
                if (businessLocation.taxSubType != null) {
                    businessLocation.taxSubType?.let {
                        for (t in getTaxTypesList(it)) {
                            if (t.isNotEmpty() && !listSubTaxType.contains(t)) {
                                listSubTaxType.add(t)
                                vUCRMTaxSubTypes.add(VUCRMTaxSubType(t))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                LogHelper.writeLog(exception = e)
            }
        }
        cOMZoneMaster.sortBy { it.zone }
        val tempSectors = cOMSectors.sortedWith(compareBy {
            it.sector?.toCharArray()?.filter { char ->
                char.isDigit()
            }?.joinToString("")
        })
        cOMSectors.clear()
        cOMSectors.addAll(tempSectors)

        dismissDialog()

        return GetDropdownFiltersForBusinessSearchResponse(cOMZoneMaster = cOMZoneMaster,
                cOMSectors = cOMSectors, vUCRMTaxSubTypes = vUCRMTaxSubTypes, cRMActivityDomains = cRMActivityDomains,
                cRMActivityClasses = cRMActivityClasses, vUINVProducts = vUINVProducts)
    }

    private fun getTaxTypesList(taxType: String): List<String> {
        if (taxType.contains(";")) {
            return taxType.split(";")
        }
        return listOf(taxType)
    }

    private fun openBusinessSearchDialogFragment() {
        val dialogFragment: BusinessSearchDialogFragment = BusinessSearchDialogFragment.newInstance(dropdownFiltersForBusinessSearchResponse!!, location)
        supportFragmentManager.let {
            binding.drwLayout.closeDrawers()
            dialogFragment.show(it, BusinessSearchDialogFragment::class.java.simpleName)
        }
    }

    private fun openLAWSearchDialogFragment() {
        val dialogFragment: LawPendingTransactionsSearchDialogFragment = LawPendingTransactionsSearchDialogFragment.newInstance(getDropdownFiltersForLAWSearchResponse, mLawPendingTransactionLocation)
        this.supportFragmentManager.let {
            binding.drwLayout.closeDrawers()
            dialogFragment.show(it, LawPendingTransactionsSearchDialogFragment::class.java.simpleName)
        }
    }

    private fun checkAgentType(): Boolean {
        if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.LEA.name
                || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.LEI.name
                || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.LES.name) {
            return true
        }
        return false
    }

    override fun onApplyClick(businessLocation: BusinessLocations?) {
        filterArrayList.clear()
        location = businessLocation
        businessAdapter = null
        getFilteredData()
        if (filterArrayList.isNotEmpty()) {
            setData(filterArrayList)
        } else {
            showToast(getString(R.string.msg_no_data))
            setData(locations)
        }
    }

    private fun getFilteredData() {

        var isSelected = false

        if (location != null && locations.isNotEmpty()) {
            location?.sycotaxID?.let {
                if (it.isNotEmpty()) {
                    for ((index, obj) in locations.withIndex()) {
                        locations[index].sycotaxID?.let { it1 ->
                            if (it1.contains(it)) {
                                filterArrayList.add(locations[index])
                                isSelected = true
                            }
                        }
                    }
                }
            }

            location?.business?.let {
                if (it.isNotEmpty()) {
                    val filterBusinessName: ArrayList<BusinessLocations> = arrayListOf()
                    filterBusinessName.addAll(filterArrayList)
                    filterArrayList.clear()
                    if (filterBusinessName.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterBusinessName.withIndex()) {
                            filterBusinessName[index].business?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(filterBusinessName[index])
                            }
                        }
                    } else if (!isSelected && locations.isNotEmpty()) {
                        for ((index, obj) in locations.withIndex()) {
                            locations[index].business?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(locations[index])
                            }
                        }
                    }
                }
            }

            location?.email?.let {
                if (it.isNotEmpty()) {
                    val filterEmail: ArrayList<BusinessLocations> = arrayListOf()
                    filterEmail.addAll(filterArrayList)
                    filterArrayList.clear()
                    if (filterEmail.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterEmail.withIndex()) {
                            filterEmail[index].email?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(filterEmail[index])
                            }
                        }
                    } else if (!isSelected && locations.isNotEmpty()) {
                        for ((index, obj) in locations.withIndex()) {
                            locations[index].email?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(locations[index])
                            }
                        }
                    }
                }
            }

            location?.phone?.let {
                if (it.isNotEmpty()) {
                    val filterPhone: ArrayList<BusinessLocations> = arrayListOf()
                    filterPhone.addAll(filterArrayList)
                    filterArrayList.clear()
                    if (filterPhone.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterPhone.withIndex()) {
                            filterPhone[index].phone?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(filterPhone[index])
                            }
                        }
                    } else if (!isSelected && locations.isNotEmpty()) {
                        for ((index, obj) in locations.withIndex()) {
                            locations[index].phone?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(locations[index])
                            }
                        }
                    }
                }
            }


            location?.onboardingYear?.let {
                if (it != getString(R.string.select)) {
                    val filterYearOnBoard: ArrayList<BusinessLocations> = arrayListOf()
                    filterYearOnBoard.addAll(filterArrayList)
                    filterArrayList.clear()
                    if (filterYearOnBoard.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterYearOnBoard.withIndex()) {
                            filterYearOnBoard[index].onboardingYear?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(filterYearOnBoard[index])
                            }
                        }
                    } else if (!isSelected && locations.isNotEmpty()) {
                        for ((index, obj) in locations.withIndex()) {
                            locations[index].onboardingYear?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(locations[index])
                            }
                        }
                    }
                }
            }

            location?.onboardingMonth?.let {
                if (it != getString(R.string.select)) {
                    val filterMonthOnBoard: ArrayList<BusinessLocations> = arrayListOf()
                    filterMonthOnBoard.addAll(filterArrayList)
                    filterArrayList.clear()
                    if (filterMonthOnBoard.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterMonthOnBoard.withIndex()) {
                            filterMonthOnBoard[index].onboardingMonth?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(filterMonthOnBoard[index])
                            }
                        }
                    } else if (!isSelected && locations.isNotEmpty()) {
                        for ((index, obj) in locations.withIndex()) {
                            locations[index].onboardingMonth?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(locations[index])
                            }
                        }
                    }
                }
            }

            location?.sector?.let {
                if (it != getString(R.string.select)) {
                    val filterSector: ArrayList<BusinessLocations> = arrayListOf()
                    filterSector.addAll(filterArrayList)
                    filterArrayList.clear()
                    if (filterSector.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterSector.withIndex()) {
                            filterSector[index].sector?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(filterSector[index])
                            }
                        }
                    } else if (!isSelected && locations.isNotEmpty()) {
                        for ((index, obj) in locations.withIndex()) {
                            locations[index].sector?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(locations[index])
                            }
                        }
                    }
                }
            }

            location?.zone?.let {
                if (it != getString(R.string.select)) {
                    val filterZone: ArrayList<BusinessLocations> = arrayListOf()
                    filterZone.addAll(filterArrayList)
                    filterArrayList.clear()
                    if (filterZone.isNotEmpty()) {
                        isSelected = true
                        for ((index, obj) in filterZone.withIndex()) {
                            filterZone[index].zone?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(filterZone[index])
                            }
                        }
                    } else if (!isSelected && locations.isNotEmpty()) {
                        for ((index, obj) in locations.withIndex()) {
                            locations[index].zone?.let { it2 ->
                                if (it2.contains(it))
                                    filterArrayList.add(locations[index])
                            }
                        }
                    }
                }
            }
        }

        location?.activityDomain?.let {
            if (it != getString(R.string.select)) {
                val filterActivityDomain: ArrayList<BusinessLocations> = arrayListOf()
                filterActivityDomain.addAll(filterArrayList)
                filterArrayList.clear()
                if (filterActivityDomain.isNotEmpty()) {
                    isSelected = true
                    for ((index, obj) in filterActivityDomain.withIndex()) {
                        filterActivityDomain[index].activityDomain?.let { it2 ->
                            if (it2.contains(it))
                                filterArrayList.add(filterActivityDomain[index])
                        }
                    }
                } else if (!isSelected && locations.isNotEmpty()) {
                    for ((index, obj) in locations.withIndex()) {
                        locations[index].activityDomain?.let { it2 ->
                            if (it2.contains(it))
                                filterArrayList.add(locations[index])
                        }
                    }
                }
            }
        }
        location?.taxSubType?.let {
            if (it != getString(R.string.select)) {
                val filterTaxSubTypes: ArrayList<BusinessLocations> = arrayListOf()
                filterTaxSubTypes.addAll(filterArrayList)
                filterArrayList.clear()
                if (filterTaxSubTypes.isNotEmpty()) {
                    isSelected = true
                    for ((index, obj) in filterTaxSubTypes.withIndex()) {
                        filterTaxSubTypes[index].taxSubType?.let { it2 ->
                            if (it2.contains(it))
                                filterArrayList.add(filterTaxSubTypes[index])
                        }
                    }
                } else if (!isSelected && locations.isNotEmpty()) {
                    for ((index, obj) in locations.withIndex()) {
                        locations[index].taxSubType?.let { it2 ->
                            if (it2.contains(it))
                                filterArrayList.add(locations[index])
                        }
                    }
                }
            }
        }
        location?.taxType?.let {
            if (it != getString(R.string.select) && it != "0") {
                val filterTaxSubTypes: ArrayList<BusinessLocations> = arrayListOf()
                filterTaxSubTypes.addAll(filterArrayList)
                filterArrayList.clear()
                if (filterTaxSubTypes.isNotEmpty()) {
                    isSelected = true
                    for ((index, obj) in filterTaxSubTypes.withIndex()) {
                        filterTaxSubTypes[index].taxType?.let { it2 ->
                            if (it2.contains(it))
                                filterArrayList.add(filterTaxSubTypes[index])
                        }
                    }
                } else if (!isSelected && locations.isNotEmpty()) {
                    for ((index, obj) in locations.withIndex()) {
                        locations[index].taxType?.let { it2 ->
                            if (it2.contains(it))
                                filterArrayList.add(locations[index])
                        }
                    }
                }
            }
        }

        location?.activityClass?.let { it1 ->
            if (it1 != getString(R.string.select)) {
                val filterActivityClass: ArrayList<BusinessLocations> = arrayListOf()
                filterActivityClass.addAll(filterArrayList)
                filterArrayList.clear()
                if (filterActivityClass.isNotEmpty()) {
                    isSelected = true
                    for ((index, obj) in filterActivityClass.withIndex()) {
                        filterActivityClass[index].activityClass?.let { it2 ->
                            if (it2.contains(it1))
                                filterArrayList.add(filterActivityClass[index])
                        }
                    }
                } else if (!isSelected && locations.isNotEmpty()) {
                    for ((index, obj) in locations.withIndex()) {
                        locations[index].activityClass?.let { it2 ->
                            if (it2.contains(it1))
                                filterArrayList.add(locations[index])
                        }
                    }
                }
            }
        }
    }

    private fun updateMarker() {
        val fragment = supportFragmentManager.findFragmentById(R.id.businessSearchContainer) as MapFragment?
        if (checkAgentType()) {
            if (lawFilterArrayList.isNotEmpty()) {
                fragment?.updateLawMarker(lawFilterArrayList)
            } else {
                fragment?.updateLawMarker(mLawPendingTransactionLocations)
            }
        } else {
            if (filterArrayList.isNotEmpty()) {
                fragment?.updateMarker(filterArrayList)
            } else {
                fragment?.updateMarker(locations)
            }
        }
    }

    override fun onLawApplyClick(location: LawPendingTransactionLocations?) {
        lawFilterArrayList.clear()
        mLawPendingTransactionLocation = location
        lawAgentAdapter = null
        impondmentMapFilterData = ImpondmentMapFilterData(
                vehicleNo = location?.VehicleNo,
                phone = location?.VehicleOwnerMobile,
                violationType = location?.ViolationType,
                violationSubType = location?.ViolationClass,
                impoundmentType = location?.ImpoundmentType,
                impoundmentSubType = location?.ImpoundmentSubType
        )
        pagination.setDefaultValues()
    }

    override fun onClearClick() {
        location = null
        filterArrayList.clear()
        lawFilterArrayList.clear()
        businessMapFilterdata = null
        binding.drwLayout.closeDrawers()
        if (checkAgentType()) {
            binding.rcvMapList.adapter = LawAgentsMapAdapter(mLawPendingTransactionLocations) { agentLocations: LawPendingTransactionLocations -> lawItemClicked(agentLocations) }
        } else {
            binding.rcvMapList.adapter = BusinessMapAdapter(locations) { businessLocations: BusinessLocations -> itemClicked(businessLocations) }
        }
        updateMarker()
    }

    override fun onClick() {
        binding.drwLayout.closeDrawers()
        /*  binding.rcvMapList.visibility = View.GONE
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
                val fragment = supportFragmentManager.findFragmentById(R.id.businessSearchContainer) as MapFragment?
                fragment?.moveMarker(latLong)
                if(!checkAgentType())
                    fragment?.setReloadBtnVisibility()
            }

            override fun start() {
            }
        })
    }

    override fun onReloadClick() {
        getBusinessLocations(true)
    }

}