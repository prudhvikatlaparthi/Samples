package com.sgs.citytax.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import com.sgs.citytax.R
import com.sgs.citytax.databinding.FragmentGeoFenceMapBinding
import com.sgs.citytax.model.GeoFenceLatLong
import com.sgs.citytax.ui.FragmentCommunicator
import com.sgs.citytax.ui.adapter.GeoFenceLatLongAdapter
import com.sgs.citytax.util.*


class GeoFenceMapFragment : BaseFragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private lateinit var binding: FragmentGeoFenceMapBinding
    private lateinit var rootView: View
    private var mGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mParentGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mLandGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mListener: FragmentCommunicator? = null
    private var latLong = LatLng(12.36566, -1.53388)
    var googleMap: GoogleMap? = null
    private var icon: Int? = 0
    var locationHelper: LocationHelper? = null
    var adapterLatLong: GeoFenceLatLongAdapter? = null
    var markerOptionsList: ArrayList<MarkerOptions> = arrayListOf()
    var adapterListener: AdapterListener? = null;
    val POLYGON_PADDING_PREFERENCE: Int = 100
    var mFromScreen: String = ""

    companion object {
        fun newInstance(mGeoFenceLatLong: ArrayList<GeoFenceLatLong>, mParentGeoFenceLatLong: ArrayList<GeoFenceLatLong>, mLandGeoFenceLatLong: ArrayList<GeoFenceLatLong>, mFromScreen: String) = GeoFenceMapFragment().apply {
            this.mGeoFenceLatLong = mGeoFenceLatLong
            this.mParentGeoFenceLatLong = mParentGeoFenceLatLong
            this.mLandGeoFenceLatLong = mLandGeoFenceLatLong
            this.mFromScreen = mFromScreen
        }

        private const val TAG = "GeoFenceMapFragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as FragmentCommunicator
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_geo_fence_map, container, false)
        rootView = binding.root
        initComponents()
        binding.tvArea.text =getString(R.string.zero_meter_square)
        return rootView
    }

    override fun initComponents() {
        binding.frag = this
        locationHelper = LocationHelper(requireContext(), binding.view, activity = requireActivity())
        locationHelper?.fetchLocation()

        val supportMapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.frg) as SupportMapFragment

        supportMapFragment.getMapAsync { map ->
            googleMap = map
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                val success = googleMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json))
                if (!success) {
                    Log.e("LocateDialogFragment", "Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
//                Log.e("LocateDialogFragment", "Can't find style. Error: ", e)
                LogHelper.writeLog(e)
            }
            if (hasPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //   int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@getMapAsync
                }
                map?.isMyLocationEnabled = true
            }
            googleMap?.uiSettings?.isZoomControlsEnabled = true

            googleMap?.setOnMarkerClickListener(this)
            googleMap?.setOnMapLongClickListener(this)

            locationHelper?.setListener(object : LocationHelper.Location {
                override fun found(latitude: Double, longitude: Double) {
                    mListener?.dismissDialog()
                    latLong = LatLng(latitude, longitude)
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 20.0f))
                    bindMarkerData()
                    if (mFromScreen == "1") {
                        binding.llLandLegend.visibility = View.GONE
                        binding.propertyCol.setText(binding.propertyCol.context.getString(R.string.land))
                        binding.parentCol.text = getString(R.string.parent_land)
                    }
                }

                override fun start() {
                    mListener?.showProgressDialog()
                }
            })
        }
        adapterListener = object : AdapterListener {
            override fun removeMarker(position: Int) {
                mRemoveMarker(position)
            }
        }
    }

    override fun onMapLongClick(latLng: LatLng?) {
        addGeoMarker(latLng)
    }

    private fun addGeoMarker(latLng: LatLng?) {
        latLng?.latitude?.let { GeoFenceLatLong(it, latLng.longitude) }?.let { mGeoFenceLatLong.add(it) }
        bindMarkerData()
    }

    private fun calculateArea(mGeoFenceLatLong: ArrayList<GeoFenceLatLong>): String {
        val polygonList: MutableList<LatLng> = ArrayList()
        for (latLng in mGeoFenceLatLong)
            polygonList.add(LatLng(latLng.latitude, latLng.longitude))
        Log.e(TAG, "computeArea " + SphericalUtil.computeArea(polygonList))
        var text = ""
        if (SphericalUtil.computeArea(polygonList) > 0) {
            text = Constant.df.format(SphericalUtil.computeArea(polygonList)) + " " + getString(R.string.meter_square)
            binding.tvArea.text = text
        } else {
            binding.tvArea.text = getString(R.string.zero_meter_square)
        }
        return text
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return true
    }

    fun updateGeoMarkersList(geoFenceLatLong: ArrayList<GeoFenceLatLong>) {
        mGeoFenceLatLong.clear()
        mGeoFenceLatLong.addAll(geoFenceLatLong)
        bindMarkerData()
    }

    private fun bindMarkerData() {
        googleMap?.clear()

        setLandPropertyMark()
        setParentPropertyMark()
        setChildPropertyMark()

        updateListAdapter()
    }

    private fun setChildPropertyMark() {
        val latLngArray: java.util.ArrayList<LatLng> = arrayListOf()
        if (mGeoFenceLatLong.size > 0) {
            for ((position: Int, location: GeoFenceLatLong?) in this.mGeoFenceLatLong.withIndex()) {
                location?.let {
                    val markerOptions = MarkerOptions()
                    location.latitude.let {
                        it.toDouble().let { latitude ->
                            location.longitude.let { it ->
                                it.toDouble().let { longitude ->
                                    markerOptions.position(LatLng(latitude, longitude))
                                    markerOptions.icon(bitmapDescriptorFromVector(requireContext(), "${position + 1}"))
                                    val marker = googleMap?.addMarker(markerOptions)
                                    marker?.tag = location
                                }
                            }
                        }
                    }
                }
            }
            for (geo in mGeoFenceLatLong) {
                latLngArray.add(LatLng(geo.latitude, geo.longitude))
            }
            if (latLngArray.size > 0) {
                googleMap!!.addPolygon(PolygonOptions().addAll(latLngArray).fillColor(0xffff6600.toInt()).strokeWidth(2F).strokeColor(R.color.colorAccent))
                val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(latLngArray)
                googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))
            }

            calculateDistance(mGeoFenceLatLong, 2F)

            if (mGeoFenceLatLong.size > 2) {
                setAreaMarkerFromLatLng("${binding.root.context.getString(R.string.area)}: ${calculateArea(mGeoFenceLatLong)}", Centroid(latLngArray))
            }
        } else {
            calculateArea(mGeoFenceLatLong)
        }
    }

    private fun setAreaMarkerFromLatLng(text: String, latLng: LatLng?) {
        val obm = writeTextOnDrawable(R.drawable.map_area_bg, text, Color.WHITE)
        val markerOptions = latLng?.let { MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(obm)).position(it) }
        googleMap!!.addMarker(markerOptions)
    }

    private fun setParentPropertyMark() {
        val parentLatLngArray: java.util.ArrayList<LatLng> = arrayListOf()

        if (mParentGeoFenceLatLong.size > 0) {
            for (geo in mParentGeoFenceLatLong) {
                parentLatLngArray.add(LatLng(geo.latitude, geo.longitude))
            }

            googleMap!!.addPolygon(PolygonOptions().addAll(parentLatLngArray).fillColor(0xffffa500.toInt()).strokeWidth(2F).strokeColor(R.color.colorliteAccent))
            val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(parentLatLngArray)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))

            //  calculateDistance(mParentGeoFenceLatLong, 3F)
        }
    }

    private fun setLandPropertyMark() {
        val landLatLngArray: java.util.ArrayList<LatLng> = arrayListOf()

        if (mLandGeoFenceLatLong.size > 0) {

            for (geo in mLandGeoFenceLatLong) {
                landLatLngArray.add(LatLng(geo.latitude, geo.longitude))
            }

            googleMap!!.addPolygon(PolygonOptions().addAll(landLatLngArray).fillColor(0x99999999.toInt()).strokeWidth(2F).strokeColor(R.color.colorRed))
            val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(landLatLngArray)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))

            // calculateDistance(mLandGeoFenceLatLong, 8F)
        }
    }

    private fun calculateDistance(mGeoFenceLatLong: java.util.ArrayList<GeoFenceLatLong>, distance: Float) {
        var prevLatLng = LatLng(0.0, 0.0)
        mGeoFenceLatLong.forEachIndexed { index, latLng ->
            prevLatLng = if (index == 0)
                LatLng(latLng.latitude, latLng.longitude)
            else {
                // Log.e("message val", "($i, $index)")
                val loc1 = Location("")
                loc1.latitude = prevLatLng.latitude
                loc1.longitude = prevLatLng.longitude

                val loc2 = Location("")
                loc2.latitude = latLng.latitude
                loc2.longitude = latLng.longitude
                var distanceInMeters = distanceCalculateInMeters(loc1, loc2)
                Log.e("message val", "($distanceInMeters)")
                setMarkerFromLat(Constant.df.format(distanceInMeters) + " m", loc1, loc2, distance)
                LatLng(latLng.latitude, latLng.longitude)
            }
        }
        if (mGeoFenceLatLong.size > 2) {
            val loc1 = Location("")
            loc1.latitude = mGeoFenceLatLong[mGeoFenceLatLong.size - 1].latitude
            loc1.longitude = mGeoFenceLatLong[mGeoFenceLatLong.size - 1].longitude

            val loc2 = Location("")
            loc2.latitude = mGeoFenceLatLong[0].latitude
            loc2.longitude = mGeoFenceLatLong[0].longitude
            var distanceInMeters = distanceCalculateInMeters(loc1, loc2)
            Log.e("message last val", "($distanceInMeters)")
            setMarkerFromLat(Constant.df.format(distanceInMeters) + " m", loc1, loc2, distance)


            val latLngArray: java.util.ArrayList<LatLng> = arrayListOf()

            for (geo in mGeoFenceLatLong) {
                latLngArray.add(LatLng(geo.latitude, geo.longitude))
            }

            setMarkerFromLat(Constant.df.format(distanceInMeters.toDouble()) + " m", loc1, loc2, distance)
            return
        }
    }

    private fun setMarkerFromLat(distanceInMeters: String, prevLatLng: Location, latLng: Location, distance: Float) {
        val obm = writeTextOnDrawable(R.drawable.map_distance_bg, distanceInMeters, Color.BLACK)
        var point = LatLng((prevLatLng.latitude + latLng.latitude) / 2, (prevLatLng.longitude + latLng.longitude) / 2);
        if (distance == 3F) {
            point = LatLng((prevLatLng.latitude + point.latitude) / 2, (prevLatLng.longitude + point.longitude) / 2);
        } else if (distance == 8F) {
            point = LatLng((point.latitude + latLng.latitude) / 2, (point.longitude + latLng.longitude) / 2);
        }
        val markerOptions = MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(obm)).position(point)
        googleMap!!.addMarker(markerOptions)
    }

    private fun bitmapDescriptorFromVector(context: Context, color: String): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_location_pin ?: 0)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = writeTextOnDrawable(R.drawable.ic_location_pin, color, Color.BLACK) //Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnClear -> onRecordClick()
            R.id.btnSubmit -> onSaveClick()
            R.id.btnRecord -> getCurrentLocation()
        }
    }

    private fun onSaveClick() {
        val intent = Intent()
        intent.putExtra(Constant.KEY_GEO_AREA_LATLONG, getMapsList())
        intent.putExtra(Constant.KEY_MAP_AREA, binding.tvArea.text.toString())
        requireActivity().setResult(Activity.RESULT_OK, intent)
        mListener?.finish()
    }

    private fun getCurrentLocation() {
        try {
            if (googleMap != null) {
                val lat = googleMap!!.myLocation.latitude
                val long = googleMap!!.myLocation.longitude
                addGeoMarker(LatLng(lat, long))
            } else {
                mListener!!.showToast(R.string.msg_please_wait)
            }
        }
        catch (e:Exception){
            LogHelper.writeLog(e)
            mListener!!.showToast(R.string.msg_please_wait)
        }
    }

    private fun onRecordClick() {
        Log.d(Companion.TAG, "onRecordClick: ")
        mGeoFenceLatLong.clear()
        bindMarkerData()
    }

//    private fun addPolyline() {
//        val landLatLngArray: ArrayList<LatLng> = arrayListOf()
//        for (geo in mLandGeoFenceLatLong) {
//            landLatLngArray.add(LatLng(geo.latitude, geo.longitude))
//        }
//        if (landLatLngArray.size > 0) {
//            googleMap!!.addPolygon(PolygonOptions().addAll(landLatLngArray).fillColor(0x99999999.toInt()).strokeWidth(2F).strokeColor(R.color.colorliteAccent))
//            val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(landLatLngArray)
//            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))
//        }
//        val parentLatLngArray: ArrayList<LatLng> = arrayListOf()
//        for (geo in mParentGeoFenceLatLong) {
//            parentLatLngArray.add(LatLng(geo.latitude, geo.longitude))
//        }
//        if (parentLatLngArray.size > 0) {
//            googleMap!!.addPolygon(PolygonOptions().addAll(parentLatLngArray).fillColor(0xffffa500.toInt()).strokeWidth(2F).strokeColor(R.color.colorliteAccent))
//            val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(parentLatLngArray)
//            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))
//        }
//
//        val latLngArray: ArrayList<LatLng> = arrayListOf()
//
//        for (geo in mGeoFenceLatLong) {
//            latLngArray.add(LatLng(geo.latitude, geo.longitude))
//        }
//        if (latLngArray.size > 0) {
//            googleMap!!.addPolygon(PolygonOptions().addAll(latLngArray).fillColor(0xffff6600.toInt()).strokeWidth(2F).strokeColor(R.color.colorAccent))
//            val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(latLngArray)
//            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))
//        }
//    }

    private fun getPolygonLatLngBounds(polygon: List<LatLng>): LatLngBounds? {
        val centerBuilder = LatLngBounds.builder()
        for (point in polygon) {
            centerBuilder.include(point)
        }
        return centerBuilder.build()
    }

    private fun updateListAdapter() {
        if (adapterLatLong == null) {
            adapterLatLong = GeoFenceLatLongAdapter(adapterListener)
            binding.recyclerView.adapter = adapterLatLong
        }
        adapterLatLong!!.updateList(mGeoFenceLatLong)
    }

    fun mRemoveMarker(position: Int) {
        mGeoFenceLatLong.removeAt(position)
        bindMarkerData()
    }

    fun getMapsList(): ArrayList<GeoFenceLatLong> {
        return mGeoFenceLatLong
    }


    interface AdapterListener {
        fun removeMarker(position: Int)
    }


}