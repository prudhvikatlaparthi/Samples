package com.sgs.citytax.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentMapBinding
import com.sgs.citytax.model.GeoFenceLatLong
import com.sgs.citytax.model.PropertyDetailLocation
import com.sgs.citytax.ui.custom.DialogAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.Pagination
import com.sgs.citytax.util.hasPermission

class LocateAgentFragment : BaseFragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnPolygonClickListener {
    private var mListener: Listener? = null
    private lateinit var binding: FragmentMapBinding
    private lateinit var rootView: View
    private var latLong = LatLng(12.36566, -1.53388)
    var googleMap: GoogleMap? = null
    private var fromScreenCode: Any? = null
    private var complaintLocations: ArrayList<ComplaintIncidentDetailLocation> = arrayListOf()
    private var propertyLocations: ArrayList<PropertyDetailLocation> = arrayListOf()
    private var locations: ArrayList<IncidentDetailLocation> = arrayListOf()
    private var agentLocations: ArrayList<AgentLocations> = arrayListOf()
    val POLYGON_PADDING_PREFERENCE: Int = 100

    lateinit var adapter: DialogAdapter
    lateinit var pagination: Pagination

    companion object {
        fun newInstance(latLong: LatLng, fromScreen: Any) = LocateAgentFragment().apply {
            if (latLong.latitude != 0.0)
                this.latLong = latLong
            fromScreenCode = fromScreen
        }
    }

    @SuppressLint("MissingPermission")
    override fun initComponents() {
        val supportMapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment

        supportMapFragment.getMapAsync { map ->
            googleMap = map
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                val success = googleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))
                if (!success) {
                    Log.e("LOcateDialogFragment", "Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e("LOcateDialogFragment", "Can't find style. Error: ", e)
            }
            if (hasPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                map?.isMyLocationEnabled = true
            }

            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 20.0f))
            map?.uiSettings?.isZoomControlsEnabled = true
            when (fromScreenCode) {
                Constant.NavigationMenu.NAVIGATION_LOCATION -> {
                    binding.btnFullScreen.visibility = View.VISIBLE
                }
                Constant.NavigationMenu.NAVIGATION_INCIDENT -> {
                    binding.btnFullScreen.visibility = View.VISIBLE
                }
                Constant.NavigationMenu.NAVIGATION_PROPERTY -> {
                    binding.btnFullScreen.visibility = View.VISIBLE
                }
                else -> {
                    binding.btnFullScreen.visibility = View.VISIBLE
                }
            }
        }

        pagination = Pagination(1, 50, null) { pageNumber, PageSize ->
            when (fromScreenCode) {
                Constant.NavigationMenu.NAVIGATION_LOCATION -> {
                    getAgentLocations(pageNumber, PageSize)
                    //Swetha - Changes made when removed Marker popup and replaced with Custom dialog - AgentLiveTracking - 10/3/2022
//                    googleMap?.setInfoWindowAdapter(this)
//                    googleMap?.setOnInfoWindowClickListener(this)
                    googleMap?.setOnMarkerClickListener(this)
                }
                Constant.NavigationMenu.NAVIGATION_INCIDENT -> {
                    getIncidentsLocations(pageNumber, PageSize)
                    googleMap?.setOnMarkerClickListener(this)
                }
                Constant.NavigationMenu.NAVIGATION_PROPERTY -> {
                    getPropertyLocations(pageNumber, PageSize)
                    googleMap?.setOnMarkerClickListener(this)
                    googleMap?.setOnPolygonClickListener(this)
                }
                else -> {
                    getComplaintsLocations(pageNumber, PageSize)
                    googleMap?.setOnMarkerClickListener(this)
                }
            }
        }

        binding.btnFullScreen.setOnClickListener {
            pagination.setDefaultValues()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: java.lang.ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    //Swetha - Changes made when, removed Marker popup and replaced with Custom dialog - AgentLiveTracking - 10/3/2022
    //commented, as created custom dialog
    /*override fun getInfoContents(marker: Marker?): View? {
        val context: Context = requireContext()
        val info = LinearLayout(context)
        info.orientation = LinearLayout.VERTICAL
        val title = TextView(context)
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER
        title.text = marker?.title
        val snippet = TextView(context)
        snippet.setTextColor(Color.GRAY)
        snippet.text = marker?.snippet

        //Implement onclick for Agent Live tracking
        //while this button onclick isn't implemented - events can't be performed on google map marker popup
        val button = Button(context)
        button.text = getString(R.string.title_agent)
        button.gravity = Gravity.CENTER
        button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
        button.setTextColor(Color.WHITE)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(20, 15, 20, 15)
        button.layoutParams = params

        info.addView(title)
        info.addView(snippet)

        //do not display button when, agent is same as logged in agent
        val agent: AgentLocations = marker?.tag as AgentLocations
        agent.agentAccountID?.let {
            if(it != prefHelper.accountId){
                info.addView(button)
            }
        }
        return info
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
*/

    /*override fun onInfoWindowClick(marker: Marker?) {
        val agent: AgentLocations = marker?.tag as AgentLocations

        //when agent is same as current logged in agent, donot perform action
        agent.agentAccountID?.let {
            if(agent.agentAccountID == prefHelper.accountId){
                return
            }
         getCRMAgentDetails(it)
        }
    }*/
//End

    private fun getAgentLocations(pageNumber: Int, pageSize: Int) {
        val accountId: Int = MyApplication.getPrefHelper().accountId
        if (pageNumber == 1) {
            mListener?.showProgressDialog()
        }
        APICall.getAgentCurrentLocations(accountId, pageNumber, pageSize, object : ConnectionCallBack<AgentCurrentLocations> {
            override fun onSuccess(response: AgentCurrentLocations) {
                if (pageNumber == 1) {
                    mListener?.dismissDialog()
                    pagination.totalRecords = response.totalRecordCounts
                    googleMap?.clear()
                    agentLocations.clear()
                }
                agentLocations.addAll(response.agentLocations)
                mListener?.updateData(agentLocations, Constant.NavigationMenu.NAVIGATION_LOCATION)
                bindAgentData(response.agentLocations)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }


    private fun getIncidentsLocations(pageNumber: Int, pageSize: Int) {
        val accountId: Int = MyApplication.getPrefHelper().accountId
        if (pageNumber == 1) {
            mListener?.showProgressDialog()
        }
        APICall.getIncidentLocations(accountId, pageNumber, pageSize, object : ConnectionCallBack<IncidentLocations> {
            override fun onSuccess(response: IncidentLocations) {
                if (pageNumber == 1) {
                    mListener?.dismissDialog()
                    pagination.totalRecords = response.totalRecordCounts
                    googleMap?.clear()
                    locations.clear()
                }
                locations.addAll(response.incidentDetailLocation as ArrayList<IncidentDetailLocation>)
                mListener?.updateData(locations, Constant.NavigationMenu.NAVIGATION_INCIDENT)
                bindIncidentData(response.incidentDetailLocation as ArrayList<IncidentDetailLocation>)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun getComplaintsLocations(pageNumber: Int, pageSize: Int) {
        if (pageNumber == 1) {
            mListener?.showProgressDialog()
        }
        APICall.getComplaintLocations(pageNumber, pageSize, object : ConnectionCallBack<ComplaintLocations> {
            override fun onSuccess(response: ComplaintLocations) {
                if (pageNumber == 1) {
                    mListener?.dismissDialog()
                    pagination.totalRecords = response.totalRecordCounts
                    googleMap?.clear()
                    complaintLocations.clear()
                }
                complaintLocations.addAll(response.complaintsDetailLocations as ArrayList<ComplaintIncidentDetailLocation>)
                mListener?.updateData(complaintLocations, Constant.NavigationMenu.NAVIGATION_COMPLAINT)
                bindComplaintData(response.complaintsDetailLocations as ArrayList<ComplaintIncidentDetailLocation>)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }

    private fun getPropertyLocations(pageNumber: Int, pageSize: Int) {
        if (pageNumber == 1) {
            mListener?.showProgressDialog()
        }
        APICall.getPropertyLocations(pageNumber, pageSize, object : ConnectionCallBack<PropertyLocations> {
            override fun onSuccess(response: PropertyLocations) {
                if (pageNumber == 1) {
                    mListener?.dismissDialog()
                    pagination.totalRecords = response.totalRecordCounts
                    googleMap?.clear()
                    propertyLocations.clear()
                }
                propertyLocations.addAll(response.propertyDetailLocations as ArrayList<PropertyDetailLocation>)
                mListener?.updateData(propertyLocations, Constant.NavigationMenu.NAVIGATION_PROPERTY)
                bindPropertyData(response.propertyDetailLocations as ArrayList<PropertyDetailLocation>)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })
    }

    private fun getMarkerColor(color: String?): BitmapDescriptor {
        val hsv = FloatArray(3)
        Color.colorToHSV(Color.parseColor(color), hsv)
        return BitmapDescriptorFactory.defaultMarker(hsv[0])
    }

    fun updateMarker(incidentDetailLocation: ArrayList<IncidentDetailLocation>?) {
        locations.clear()
        googleMap?.clear()
        incidentDetailLocation?.let {
            locations.addAll(it)
            bindIncidentData(locations, true)
        }
    }

    fun updateComplaintMarker(complaintIncidentDetailLocation: ArrayList<ComplaintIncidentDetailLocation>?) {
        complaintLocations.clear()
        googleMap?.clear()
        complaintIncidentDetailLocation?.let {
            complaintLocations.addAll(it)
            bindComplaintData(complaintLocations, true)
        }
    }

    fun updatePropertyMarker(propertyDetailLocation: ArrayList<PropertyDetailLocation>?) {
        propertyLocations.clear()
        googleMap?.clear()
        propertyDetailLocation?.let {
            propertyLocations.addAll(it)
            bindPropertyData(propertyLocations, true)
        }
    }

    private fun bindAgentData(list: List<AgentLocations>, isForUpdate: Boolean = false) {
        pagination.setIsScrolled(false)
        if (list.isNotEmpty()) {
            pagination.stopPagination(list.size)
        } else {
            pagination.stopPagination(0)
        }
        for (agentInfo: AgentLocations? in list) {
            agentInfo?.latitude?.let {
                it.toDouble().let { latitude ->
                    agentInfo.longitude?.let { it ->
                        it.toDouble().let { longitude ->
                            val markerOptions = MarkerOptions()
                            markerOptions.position(LatLng(latitude, longitude))
                            markerOptions.icon(getMarkerColor(agentInfo.color))
                            markerOptions.title(agentInfo.agent)
                            //Removed Marker default popup - 10/3/2022
//                            markerOptions.snippet(agentInfo.getMarkerInfo(requireContext()))
                            val marker = googleMap?.addMarker(markerOptions)
                            marker?.tag = agentInfo
                        }
                    }
                }
            }
        }
        if (agentLocations.size < pagination.totalRecords && !isForUpdate) {
            pagination.doForceNextCall()
        }
    }


    private fun bindIncidentData(list: ArrayList<IncidentDetailLocation>, isForUpdate: Boolean = false) {
        pagination.setIsScrolled(false)
        if (list.isNotEmpty()) {
            pagination.stopPagination(list.size)
        } else {
            pagination.stopPagination(0)
        }
        for (incidentInfo: IncidentDetailLocation? in list) {
            val markerOptions = MarkerOptions()
            incidentInfo?.latitude?.let {
                it.toDouble().let { latitude ->
                    incidentInfo.longitude?.let { it ->
                        it.toDouble().let { longitude ->
                            markerOptions.position(LatLng(latitude, longitude))
                            markerOptions.icon(getMarkerColor(incidentInfo.color))
                            markerOptions.title(context?.resources?.getString(R.string.status) + " : ${incidentInfo.status ?: ""}")
                            val marker = googleMap?.addMarker(markerOptions)
                            marker?.tag = incidentInfo
                        }
                    }
                }
            }
        }
        if (locations.size < pagination.totalRecords && !isForUpdate) {
            pagination.doForceNextCall()
        }
    }


    private fun bindComplaintData(list: ArrayList<ComplaintIncidentDetailLocation>, isForUpdate: Boolean = false) {
        pagination.setIsScrolled(false)
        if (list.isNotEmpty()) {
            pagination.stopPagination(list.size)
        } else {
            pagination.stopPagination(0)
        }
        for (complaintInfo: ComplaintIncidentDetailLocation? in list) {
            val markerOptions = MarkerOptions()
            complaintInfo?.latitude?.let {
                it.toDouble().let { latitude ->
                    complaintInfo.longitude?.let { it ->
                        it.toDouble().let { longitude ->
                            markerOptions.position(LatLng(latitude, longitude))
                            markerOptions.icon(getMarkerColor(complaintInfo.color))
                            markerOptions.title(context?.resources?.getString(R.string.status) + " : ${complaintInfo.status ?: ""}")
                            val marker = googleMap?.addMarker(markerOptions)
                            marker?.tag = complaintInfo
                        }
                    }
                }
            }
        }
        if (complaintLocations.size < pagination.totalRecords && !isForUpdate) {
            pagination.doForceNextCall()
        }
    }

    private fun bindPropertyData(list: ArrayList<PropertyDetailLocation>, isForUpdate: Boolean = false) {
        pagination.setIsScrolled(false)
        if (list.isNotEmpty()) {
            pagination.stopPagination(list.size)
        } else {
            pagination.stopPagination(0)
        }
        for ((_, propertyInfo) in list.withIndex()) {
            if (propertyInfo.GeoLocationArea != null) {
                setGeoData(propertyInfo.GeoLocationArea, propertyInfo)
            }
        }
        if (propertyLocations.size < pagination.totalRecords && !isForUpdate) {
            pagination.doForceNextCall()
        }
    }

    private fun setGeoData(geoLocationArea: String?, propertyInfo: PropertyDetailLocation) {
        if (!TextUtils.isEmpty(geoLocationArea)) {
            Thread().run {
                val arrayListTutorialType = object : TypeToken<ArrayList<GeoFenceLatLong>>() {}.type
                val mGeoFenceLatLong: ArrayList<GeoFenceLatLong> = Gson().fromJson(geoLocationArea, arrayListTutorialType)
                addPolyline(mGeoFenceLatLong, propertyInfo)
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker.let {
            if (fromScreenCode == Constant.NavigationMenu.NAVIGATION_INCIDENT) {
                val location: IncidentDetailLocation = marker?.tag as IncidentDetailLocation
                val dialogFragment: ComplaintIncidentInfoDialogFragment = ComplaintIncidentInfoDialogFragment.newInstance(location, null, fromScreenCode)
                dialogFragment.show(childFragmentManager, ComplaintIncidentInfoDialogFragment::class.java.simpleName)
            } else if (fromScreenCode == Constant.NavigationMenu.NAVIGATION_COMPLAINT) {
                val location: ComplaintIncidentDetailLocation = marker?.tag as ComplaintIncidentDetailLocation
                val dialogFragment: ComplaintIncidentInfoDialogFragment = ComplaintIncidentInfoDialogFragment.newInstance(null, location, fromScreenCode)
                dialogFragment.show(childFragmentManager, ComplaintIncidentInfoDialogFragment::class.java.simpleName)
            }else if (fromScreenCode == Constant.NavigationMenu.NAVIGATION_LOCATION){
                //Swetha - Changes made when, removed Marker popup and replaced with Custom dialog - AgentLiveTracking - 10/3/2022
                val agentLocationInfo: AgentLocations = marker?.tag as AgentLocations
                val dialogFragment: AgentInfoDialogFragment = AgentInfoDialogFragment.newInstance(agentLocationInfo, fromScreenCode)
                dialogFragment.show(childFragmentManager, AgentInfoDialogFragment::class.java.simpleName)
            }
        }
        return true
    }

    /*   private fun bindMarkerData(mGeoFenceLatLong: ArrayList<GeoFenceLatLong>, propertyInfo: PropertyDetailLocation) {
        for (location: GeoFenceLatLong? in mGeoFenceLatLong) {
            location?.let {
                val markerOptions = MarkerOptions()
                location.latitude.let {
                    it.toDouble().let { latitude ->
                        location.longitude.let { it ->
                            it.toDouble().let { longitude ->
                                markerOptions.position(LatLng(latitude, longitude))
                                markerOptions.icon(bitmapDescriptorFromVector(requireContext(), "123"))
                                val marker = googleMap?.addMarker(markerOptions)
                                marker?.tag = location
                            }
                        }
                    }
                }
            }
        }
        addPolyline(mGeoFenceLatLong, propertyInfo)
    }*/

    private fun addPolyline(mGeoFenceLatLong: ArrayList<GeoFenceLatLong>, propertyInfo: PropertyDetailLocation) {
        val latLngArray: ArrayList<LatLng> = arrayListOf()

        for (geo in mGeoFenceLatLong) {
            latLngArray.add(LatLng(geo.latitude, geo.longitude))
        }

        if (latLngArray.size > 0) {
            var color = context?.resources?.getColor(R.color.lite_blue) ?: 0
            if (propertyInfo.taxRuleBookCode == Constant.TaxRuleBook.RES_PROP.Code) {
                color = context?.resources?.getColor(R.color.lite_blue) ?: 0
            } else if (propertyInfo.taxRuleBookCode == Constant.TaxRuleBook.COM_PROP.Code) {
                color = context?.resources?.getColor(R.color.colorRedDark) ?: 0
            } else if (propertyInfo.taxRuleBookCode == Constant.TaxRuleBook.LAND_PROP.Code) {
                color = context?.resources?.getColor(R.color.colorAccent) ?: 0
            } else if (propertyInfo.taxRuleBookCode == Constant.TaxRuleBook.LAND_CONTRIBUTION.Code) {
                color = context?.resources?.getColor(R.color.colorAccent) ?: 0
            }
            val polygonOptions = PolygonOptions()
                    .addAll(latLngArray)
                    .strokeColor(R.color.colorAccent)
                    .strokeWidth(2F)
            val polygon: Polygon = googleMap!!.addPolygon(polygonOptions)
            polygon.fillColor = color
            polygon.strokeColor = color
            polygon.tag = propertyInfo
            polygon.isClickable = true
        }
    }

    /*private fun bitmapDescriptorFromVector(context: Context, color: String?): BitmapDescriptor? {
        var icon = 0
        if (color != null && color.isNotEmpty()) {
            when (color.toUpperCase(Locale.getDefault())) {
                "123" -> {
                    icon = R.drawable.ic_map_marker_impound
                }
            }
        }

        val vectorDrawable = ContextCompat.getDrawable(context, icon)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    private fun getPolygonLatLngBounds(polygon: List<LatLng>): LatLngBounds? {
        val centerBuilder = LatLngBounds.builder()
        for (point in polygon) {
            centerBuilder.include(point)
        }
        return centerBuilder.build()
    }*/

    interface Listener {
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showProgressDialog()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun updateData(any: ArrayList<*>, navigation: Constant.NavigationMenu)
    }

    override fun onPolygonClick(p0: Polygon?) {
        if (fromScreenCode == Constant.NavigationMenu.NAVIGATION_PROPERTY) {
            val propertyInfo = p0?.tag as PropertyDetailLocation
            val dialogFragment = PropertyLocationInfoDialogFragment.newInstance(propertyInfo, fromScreenCode)
            dialogFragment.show(childFragmentManager, PropertyLocationInfoDialogFragment::class.java.simpleName)
        }
    }
}
