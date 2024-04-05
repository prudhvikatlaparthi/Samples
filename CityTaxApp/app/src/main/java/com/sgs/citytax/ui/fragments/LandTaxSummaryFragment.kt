package com.sgs.citytax.ui.fragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.SphericalUtil
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.VerificationDetails
import com.sgs.citytax.api.response.*
import com.sgs.citytax.databinding.FragmentLandSummaryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.ui.DocumentPreviewActivity
import com.sgs.citytax.ui.PropertyImagesPreviewActivity
import com.sgs.citytax.ui.PropertyPlansPreviewActivity
import com.sgs.citytax.ui.PropertySummaryReceiptActivity
import com.sgs.citytax.ui.adapter.PropertySummaryAdapter
import com.sgs.citytax.util.*
import java.util.*

class LandTaxSummaryFragment : BaseFragment(), IClickListener {
    private lateinit var mBinding: FragmentLandSummaryBinding
    private var mListener: Listener? = null
    private var summaries: List<PropertyDueSummary> = arrayListOf()
    private var propertyID: Int? = 0
    private var propertyTax: PropertyTax? = null
    private var mCode: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_NONE
    private var verificationDetails: VerificationDetails? = null

    private var mGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mParentGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()
    private var mLandGeoFenceLatLong: ArrayList<GeoFenceLatLong> = arrayListOf()


    var googleMap: GoogleMap? = null

    val POLYGON_PADDING_PREFERENCE: Int = 100
    var locationHelper: LocationHelper? = null
    private var latLong = LatLng(12.36566, -1.53388)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implment Listener")
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_land_summary, container, false)
        initComponents()
        return mBinding.root
    }

    override fun initComponents() {
        //region Arguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_PRIMARY_KEY))
                propertyID = it.getInt(Constant.KEY_PRIMARY_KEY)

            if (arguments?.getSerializable(Constant.KEY_QUICK_MENU) != null)
                mCode = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        //endregion
        setViews()
        getHeaderDetails()
        setListeners()
    }

    private fun setViews() {
        mBinding.listView.setAdapter(PropertySummaryAdapter(this))
        if (mCode == Constant.QuickMenu.QUICK_MENU_CREATE_LAND ||mCode == Constant.QuickMenu.QUICK_MENU_UPDATE_LAND || mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_CREATE_LAND|| mCode == Constant.QuickMenu.QUICK_MENU_BUSINESS_UPDATE_LAND)
        {
            mBinding.btnEdit.visibility = View.VISIBLE
        }

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
                LogHelper.writeLog(exception = e)
//                Log.e("LocateDialogFragment", "Can't find style. Error: ", e)
            }
            if (hasPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                map?.isMyLocationEnabled = true
            }
            googleMap?.uiSettings?.isZoomControlsEnabled = true

            locationHelper?.setListener(object : LocationHelper.Location {
                override fun found(latitude: Double, longitude: Double) {
                    mListener?.dismissDialog()
                    latLong = LatLng(latitude, longitude)
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 20.0f))
                    if (propertyTax != null) {
                        setGeoData(propertyTax?.geoLocationArea, propertyTax?.parentGeoLocationArea, propertyTax?.landGeoLocationArea)
                    }
                }

                override fun start() {
                    mListener?.showProgressDialog()
                }
            })
        }
    }

    private fun getHeaderDetails() {
        mListener?.showProgressDialog()
        APICall.getPropertyDetails(propertyID, object : ConnectionCallBack<PropertyTaxResponse> {
            override fun onSuccess(response: PropertyTaxResponse) {
                mListener?.dismissDialog()
                if (response.propertyTax != null && response.propertyTax.isNotEmpty())
                    propertyTax = response.propertyTax[0]
                if (response.verificationDetails != null && response.verificationDetails.isNotEmpty())
                    verificationDetails = response.verificationDetails[0]
                binData()
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })
    }

    private fun setListeners() {
        mBinding.btnProceed.setOnClickListener {
            val intent = Intent(requireContext(), PropertySummaryReceiptActivity::class.java)
            intent.putExtra(Constant.KEY_PRIMARY_KEY, propertyID)
            startActivity(intent)
            activity?.finish()
        }
        mBinding.btnEdit.setOnClickListener {
            Handler().postDelayed({
                mListener?.popBackStack()
            }, 200)
        }
    }

    private fun binData() {
        propertyTax?.let { mProperty ->
            mProperty.sycotaxID?.let {
                mBinding.txtSycoTaxID.text = it
                mBinding.llSycoTaxID.visibility = View.VISIBLE
            }
            mProperty.propertyName?.let {
                mBinding.txtPropertyName.text = it
                mBinding.llPropertyName.visibility = View.VISIBLE
            }
            mProperty.registrationNumber?.let {
                mBinding.txtRegistrationNumber.text = it
                mBinding.llRegistrationNumbr.visibility = View.VISIBLE
            }
            mProperty.surveyNumber?.let {
                mBinding.txtPropertySurveyNumber.text = it
                mBinding.llPropertySurveyNumber.visibility = View.VISIBLE
            }
            mProperty.regDate?.let {
                mBinding.txtRegistrationDate.text = displayFormatDate(it)
                mBinding.llRegistrationDate.visibility = View.VISIBLE
            }

            mProperty.propertyType?.let {
                mBinding.txtPropertyType.text = it
                mBinding.llPropertyType.visibility = View.VISIBLE
            }
            mProperty.onboardedBy?.let {
                mBinding.txtOnBoardBy.text = it
                mBinding.llOnBoardBy.visibility = View.VISIBLE
            }
            getPropertyOutstandingDues(mProperty.taxRuleBookCode?:"")
        }
        verificationDetails?.let{

            if (it.allowPhysicalVerification.equals("Y"))
            {
                mBinding.llVerifiedBy.visibility = View.VISIBLE
                mBinding.txtVerifiedBy.text =  it.physicalVerificationByUser
            }
        }
    }

    private fun setAdapter(response: PropertyDueSummaryResponse) {
        if (response.propertyDueSummary.isNotEmpty()) {
            summaries = response.propertyDueSummary.sortedByDescending { it.year }
            val list = summaries.groupBy { it.year }
            for (group in list) {
                group.key?.let {
                    (mBinding.listView.expandableListAdapter as PropertySummaryAdapter).update(getString(R.string.tax_year) + " " + group.key.toString(), group.value, mBinding.listView)
                }
            }
        }
    }

    private fun getPropertyOutstandingDues(taxRuleBookCode:String) {
        mListener?.showProgressDialog()
        APICall.getPropertyTaxDueYearSummary(taxRuleBookCode, propertyID, object : ConnectionCallBack<PropertyDueSummaryResponse> {
            override fun onSuccess(response: PropertyDueSummaryResponse) {
                mListener?.dismissDialog()
                setAdapter(response)
                getDocuments()
                getPropertyImages()
                getPropertyPlans()
                getPropertyOwners()
                setGeoData(propertyTax?.geoLocationArea, propertyTax?.parentGeoLocationArea, propertyTax?.landGeoLocationArea)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }
        })
    }


    private fun setGeoData(geoLocationArea: String?, parentGeoLocationArea: String?, landGeoLocationArea: String?) {
        if (!TextUtils.isEmpty(geoLocationArea)) {
            val arrayListTutorialType = object : TypeToken<java.util.ArrayList<GeoFenceLatLong>>() {}.type
            mGeoFenceLatLong = Gson().fromJson(geoLocationArea, arrayListTutorialType)
        }
        if (!TextUtils.isEmpty(parentGeoLocationArea)) {
            val arrayListTutorialType = object : TypeToken<java.util.ArrayList<GeoFenceLatLong>>() {}.type
            if (!parentGeoLocationArea.isNullOrEmpty())
                mParentGeoFenceLatLong = Gson().fromJson(parentGeoLocationArea, arrayListTutorialType)
        }
        if (!TextUtils.isEmpty(landGeoLocationArea)) {
            val arrayListTutorialType = object : TypeToken<java.util.ArrayList<GeoFenceLatLong>>() {}.type
            if (!landGeoLocationArea.isNullOrEmpty())
                mLandGeoFenceLatLong = Gson().fromJson(landGeoLocationArea, arrayListTutorialType)
        }


        bindMarkerData()
    }

    private fun bindMarkerData() {
        googleMap?.clear()

        setLandPropertyMark()
        setParentPropertyMark()
        setChildPropertyMark()
    }


    private fun setChildPropertyMark() {
        val latLngArray: ArrayList<LatLng> = arrayListOf()
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

            setAreaMarkerFromLatLng("${mBinding.root.context.getString(R.string.area)}: ${calculateArea(mGeoFenceLatLong)}", Centroid(latLngArray))

        }
    }

    private fun calculateArea(mGeoFenceLatLong: ArrayList<GeoFenceLatLong>): String {
        val polygonList: MutableList<LatLng> = ArrayList()
        for (latLng in mGeoFenceLatLong)
            polygonList.add(LatLng(latLng.latitude, latLng.longitude))
//        Log.e(TAG, "computeArea " + SphericalUtil.computeArea(polygonList))
        var text = ""
        if (SphericalUtil.computeArea(polygonList) > 0) {
            text = Constant.df.format(SphericalUtil.computeArea(polygonList)) + " " + getString(R.string.meter_square)
//            mBinding.tvArea.text = text
        } else {
//            mBinding.tvArea.text = "---"
        }
        return text
    }

    private fun setAreaMarkerFromLatLng(text: String, latLng: LatLng?) {
        val obm = writeTextOnDrawable(R.drawable.map_area_bg, text, Color.WHITE)
        val markerOptions = latLng?.let {
            MarkerOptions().icon(
                    BitmapDescriptorFactory.fromBitmap(obm))
                    .position(it)
        }
        googleMap!!.addMarker(markerOptions)
    }

    private fun setParentPropertyMark() {
        val parentLatLngArray: ArrayList<LatLng> = arrayListOf()

        if (mParentGeoFenceLatLong.size > 0) {
//            for ((position:Int,location: GeoFenceLatLong?) in this.mParentGeoFenceLatLong.withIndex()) {
//                location?.let {
//                    val markerOptions = MarkerOptions()
//                    location.latitude.let {
//                        it.toDouble().let { latitude ->
//                            location.longitude.let { it ->
//                                it.toDouble().let { longitude ->
//                                    markerOptions.position(LatLng(latitude, longitude))
//                                    markerOptions.icon(bitmapDescriptorFromVector(requireContext(), "${position+1}"))
//                                    val marker = googleMap?.addMarker(markerOptions)
//                                    marker?.tag = location
//                                }
//                            }
//                        }
//                    }
//                }
//            }
            for (geo in mParentGeoFenceLatLong) {
                parentLatLngArray.add(LatLng(geo.latitude, geo.longitude))
            }

            googleMap!!.addPolygon(PolygonOptions().addAll(parentLatLngArray).fillColor(0xffffa500.toInt()).strokeWidth(2F).strokeColor(R.color.colorliteAccent))
            val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(parentLatLngArray)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))

            //calculateDistance(mParentGeoFenceLatLong, 3F)

        }
    }

    private fun setLandPropertyMark() {
        val landLatLngArray: ArrayList<LatLng> = arrayListOf()

        if (mLandGeoFenceLatLong.size > 0) {

//            for ((position:Int,location: GeoFenceLatLong?) in this.mLandGeoFenceLatLong.withIndex()) {
//                location?.let {
//                    val markerOptions = MarkerOptions()
//                    location.latitude.let {
//                        it.toDouble().let { latitude ->
//                            location.longitude.let { it ->
//                                it.toDouble().let { longitude ->
//                                    markerOptions.position(LatLng(latitude, longitude))
//                                    markerOptions.icon(bitmapDescriptorFromVector(requireContext(), "${position+1}"))
//                                    val marker = googleMap?.addMarker(markerOptions)
//                                    marker?.tag = location
//                                }
//                            }
//                        }
//                    }
//                }
//            }

            for (geo in mLandGeoFenceLatLong) {
                landLatLngArray.add(LatLng(geo.latitude, geo.longitude))
            }

            googleMap!!.addPolygon(PolygonOptions().addAll(landLatLngArray).fillColor(0xffffa500.toInt()).strokeWidth(2F).strokeColor(R.color.colorRed))
            val latLngBounds: LatLngBounds? = getPolygonLatLngBounds(landLatLngArray)
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE))

            //calculateDistance(mLandGeoFenceLatLong, 8F)

        }
    }

    private fun calculateDistance(mGeoFenceLatLong: ArrayList<GeoFenceLatLong>, distance: Float) {
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


            val latLngArray: ArrayList<LatLng> = arrayListOf()

            for (geo in mGeoFenceLatLong) {
                latLngArray.add(LatLng(geo.latitude, geo.longitude))
            }
//            setAreaMarkerFromLatLng("Area: ${calculateArea(mGeoFenceLatLong)}", Centroid(latLngArray))

            setMarkerFromLat(Constant.df.format(distanceInMeters) + " m", loc1, loc2, distance)
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
        val markerOptions = MarkerOptions().icon(
                BitmapDescriptorFactory.fromBitmap(obm))
                .position(point)
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

    private fun getPolygonLatLngBounds(polygon: List<LatLng>): LatLngBounds? {
        val centerBuilder = LatLngBounds.builder()
        for (point in polygon) {
            centerBuilder.include(point)
        }
        return centerBuilder.build()
    }

    private fun getDocuments() {
        mListener?.showProgressDialog()
        APICall.getDocumentDetails(propertyID.toString(), "COM_PropertyMaster", object : ConnectionCallBack<List<COMDocumentReference>> {
            override fun onSuccess(response: List<COMDocumentReference>) {
                mListener?.dismissDialog()
                val comDocumentReferences = response as ArrayList<COMDocumentReference>
                val list = arrayListOf<PropertyTax>()
                val propertyTax = PropertyTax()
                propertyTax.documents = comDocumentReferences
                list.add(propertyTax)
                if (list.isNotEmpty())
                    (mBinding.listView.expandableListAdapter as PropertySummaryAdapter).update(getString(R.string.documents), list, mBinding.listView)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })

    }

    private fun getPropertyImages() {
        mListener?.showProgressDialog()
        APICall.getPropertyImages(propertyID
                ?: 0, object : ConnectionCallBack<PropertyImageResponse> {
            override fun onSuccess(response: PropertyImageResponse) {
                mListener?.dismissDialog()
                val images = response.propertyImages
                val list = arrayListOf<PropertyTax>()
                val propertyTax = PropertyTax()
                propertyTax.propertyImages = images
                list.add(propertyTax)
                if (list.isNotEmpty())
                    (mBinding.listView.expandableListAdapter as PropertySummaryAdapter).update(getString(R.string.land_images), list, mBinding.listView)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })
    }

    private fun getPropertyPlans() {
        mListener?.showProgressDialog()
        APICall.getPropertyPlans(propertyID
                ?: 0, object : ConnectionCallBack<PropertyPlanImageResponse> {
            override fun onSuccess(response: PropertyPlanImageResponse) {
                mListener?.dismissDialog()
                val plans = response.propertyplans
                val list = arrayListOf<PropertyTax>()
                val propertyTax = PropertyTax()
                propertyTax.propertyPlans = plans
                list.add(propertyTax)
                if (list.isNotEmpty())
                    (mBinding.listView.expandableListAdapter as PropertySummaryAdapter).update(getString(R.string.land_plan_images), list, mBinding.listView)

            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })
    }

    private fun getPropertyOwners() {
        mListener?.showProgressDialog()
        APICall.getPropertyOwners(propertyID, object : ConnectionCallBack<PropertyOwnerResponse> {
            override fun onSuccess(response: PropertyOwnerResponse) {
                mListener?.dismissDialog()
                if (response.propertyOwners?.isNotEmpty() == true)
                    (mBinding.listView.expandableListAdapter as PropertySummaryAdapter).update(getString(R.string.land_owner), response.propertyOwners!!, mBinding.listView)
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
            }
        })
    }


    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (it.id) {
                R.id.itemImageDocumentPreview -> {
                    val documentReferences: ArrayList<COMDocumentReference> = view.tag as ArrayList<COMDocumentReference>
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    documentReferences.remove(comDocumentReference)
                    documentReferences.add(0, comDocumentReference)
                    intent.putExtra(Constant.KEY_DOCUMENT, documentReferences)
                    startActivity(intent)
                }
                R.id.itemPropertyImagePreview -> {
                    val images = view.tag as ArrayList<COMPropertyImage>
                    val image = obj as COMPropertyImage
                    val intent = Intent(context, PropertyImagesPreviewActivity::class.java)
                    images.remove(image)
                    images.add(0, image)
                    intent.putExtra(Constant.KEY_PROPERTY_IMAGE, images)
                    startActivity(intent)
                }
                R.id.itemPropertyPlanImagePreview -> {
                    val plans = view.tag as ArrayList<COMPropertyPlanImage>
                    val plan = obj as COMPropertyPlanImage
                    val intent = Intent(context, PropertyPlansPreviewActivity::class.java)
                    plans.remove(plan)
                    plans.add(0, plan)
                    intent.putExtra(Constant.KEY_PROPERTY_PLAN_IMAGE, plans)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }


    interface Listener {
        fun dismissDialog()
        fun hideKeyBoard()
        fun showAlertDialog(message: String)
        fun showAlertDialog(msg: String, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener?, view: View)
        fun showProgressDialog()
        fun showSnackbarMsg(msg: String)
        fun showSnackbarMsg(message: Int)
        fun showToolbarBackButton(title: Int)
        fun popBackStack()
        fun showToast(message: Int)
        fun finish()
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showAlertDialog(message: Int, positiveButton: Int, positiveListener: View.OnClickListener, neutralButton: Int, neutralListener: View.OnClickListener?, negativeButton: Int, negativeListener: View.OnClickListener?, view: View?)
    }
}