package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.MarkerManager
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.response.BusinessLocation4Agent
import com.sgs.citytax.api.response.LAWPendingTransaction4Agent
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentMapBinding
import com.sgs.citytax.model.BusinessLocations
import com.sgs.citytax.model.LawPendingTransactionLocations
import com.sgs.citytax.util.*
import java.util.*


class MapFragment : BaseFragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var rootView: View
    private var locations: ArrayList<BusinessLocations> = arrayListOf()
    private var mLawPendingTransactionLocations: ArrayList<LawPendingTransactionLocations> = arrayListOf()
    private var mListener: Listener? = null
    private var latLong = LatLng(12.36566, -1.53388)
    private var googleMap: GoogleMap? = null

    lateinit var pagination: Pagination

    private var mClusterManager: ClusterManager<BusinessLocations>? = null
    private var mLawClusterManager: ClusterManager<LawPendingTransactionLocations>? = null

    companion object {
        fun newInstance(latLong: LatLng) = MapFragment().apply {
            if (latLong.latitude != 0.0)
                this.latLong = latLong
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement listener")
        }
    }

    fun setReloadBtnVisibility(){
        if (binding.btnFullScreen.visibility != View.VISIBLE) {
            binding.btnFullScreen.visibility = View.VISIBLE
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        rootView = binding.root
        initComponents()
        return rootView
    }

    override fun initComponents() {
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
                LogHelper.writeLog(e,"Can't find style. Error: ")
            }
            if (hasPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                map?.isMyLocationEnabled = true
            }

            val markerManager = MarkerManager(googleMap)
            if (checkAgentType()) {
                mLawClusterManager = ClusterManager(activity, googleMap, markerManager)
                mLawClusterManager?.renderer = RenderLawClusterInfoWindow(requireContext(), this.googleMap, mLawClusterManager)
            } else {
                mClusterManager = ClusterManager(activity, googleMap, markerManager)
                mClusterManager?.renderer = RenderClusterInfoWindow(requireContext(), this.googleMap, mClusterManager)
            }

            googleMap?.setOnMarkerClickListener(markerManager)
            googleMap?.setOnCameraIdleListener(this)
            googleMap?.uiSettings?.isZoomControlsEnabled = true

           moveMarker(latLong)

            if (checkAgentType()) {
                pagination = Pagination(1, 20, null) { pageNumber, PageSize ->
                    getLawAgentLocations(pageNumber, PageSize)
                }
                pagination.setDefaultValues()
            } else {
                getBusinessLocations()
                binding.btnFullScreen.setOnClickListener(object: OnSingleClickListener(){
                    override fun onSingleClick(v: View?) {
                        mListener?.onReloadClick()
                    }
                })
            }
        }
    }

    fun moveMarker(latLong: LatLng) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 20.0f))
    }

    private fun checkAgentType(): Boolean {
        if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.LEA.name
                || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.LEI.name
                || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.LES.name) {
            return true
        }
        return false
    }

    private fun getLawAgentLocations(pageNumber: Int, PageSize: Int) {
        if (MyApplication.getPrefHelper().assignedZoneCode == Constant.AssignedZoneSectorCode.NZ.name) {
            return
        }
        val accountId: Int = MyApplication.getPrefHelper().accountId
        APICall.getLawPendingTransactionLocationForAgent(accountId, pageNumber, PageSize, null, object : ConnectionCallBack<LAWPendingTransaction4Agent> {
            override fun onSuccess(response: LAWPendingTransaction4Agent) {
                var doClear = false
                if (pageNumber <= 1) {
                    mLawPendingTransactionLocations.clear()
                    pagination.totalRecords = response.totalRecordCounts
                    doClear = true
                }
                mLawPendingTransactionLocations.addAll(response.lawPendingTransactionLocations as ArrayList<LawPendingTransactionLocations>)
                bindLawAgentMarkers(response.lawPendingTransactionLocations as ArrayList<LawPendingTransactionLocations>, doClear)
            }

            override fun onFailure(message: String) {
            }
        })

    }

    fun getBusinessLocations(isFromReload:Boolean = false) {
        if (MyApplication.getPrefHelper().assignedZoneCode == Constant.AssignedZoneSectorCode.NZ.name) {
            return
        }
        if (isFromReload) {
            mListener?.showProgressDialog()
        }
        val accountId: Int = MyApplication.getPrefHelper().accountId
        APICall.getBusinessLocationForAgent(accountId, /*pageNumber, PageSize, null,*/ object : ConnectionCallBack<BusinessLocation4Agent> {
            override fun onSuccess(response: BusinessLocation4Agent) {
                if (isFromReload) {
                    mListener?.dismissDialog()
                }
                locations.clear()
                locations.addAll(response.businessLocations as ArrayList<BusinessLocations>)
                bindBusinessMarkers(response.businessLocations as ArrayList<BusinessLocations>, true)
            }

            override fun onFailure(message: String) {
                if (isFromReload) {
                    mListener?.dismissDialog()
                }
            }
        })
    }

    fun bindBusinessMarkers(mLocations: ArrayList<BusinessLocations>, doClear: Boolean, isForUpdate: Boolean = false) {
        if (doClear) {
            mClusterManager?.clearItems()
            mClusterManager?.cluster()
        }
        mClusterManager?.addItems(mLocations)
        mClusterManager?.setOnClusterItemClickListener {
            showMarkerDialog(it)
        }
        mClusterManager?.setOnClusterClickListener {
            showMarkerDialog(it.items.first())
        }
        mClusterManager?.setOnClusterInfoWindowClickListener {
            showMarkerDialog(it.items.first())
        }
        Handler().postDelayed({ mClusterManager!!.cluster() }, 100)
    }

    fun bindLawAgentMarkers(mLocations: ArrayList<LawPendingTransactionLocations>?, doClear: Boolean, isForUpdate: Boolean = false) {
        if (doClear) {
            mLawClusterManager?.clearItems()
            mLawClusterManager?.cluster()
        }
        mLawClusterManager?.addItems(mLocations)
        mLawClusterManager?.setOnClusterItemClickListener {
            showLawMarkerDialog(it)
        }
        mLawClusterManager?.setOnClusterClickListener {
            showLawMarkerDialog(it.items.first())
        }
        mLawClusterManager?.setOnClusterInfoWindowClickListener {
            showLawMarkerDialog(it.items.first())
        }
        if (locations.size < pagination.totalRecords && !isForUpdate) {
            pagination.doForceNextCall()
        }
        Handler().postDelayed(Runnable { mLawClusterManager!!.cluster() }, 100)
    }

    private fun showMarkerDialog(location: BusinessLocations): Boolean {
        val dialogFragment: BusinessInfoDialogFragment = BusinessInfoDialogFragment.newInstance(location)
        dialogFragment.show(childFragmentManager, BusinessInfoDialogFragment::class.java.simpleName)
        return true
    }

    private fun showLawMarkerDialog(location: LawPendingTransactionLocations): Boolean {
        val dialogFragment: LawPendingTransactionInfoDialogFragment = LawPendingTransactionInfoDialogFragment.newInstance(location)
        dialogFragment.show(childFragmentManager, LawPendingTransactionInfoDialogFragment::class.java.simpleName)
        return true
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker.let {
            if (checkAgentType()) {
                val location: LawPendingTransactionLocations = marker?.tag as LawPendingTransactionLocations
                val dialogFragment: LawPendingTransactionInfoDialogFragment = LawPendingTransactionInfoDialogFragment.newInstance(location)
                dialogFragment.show(childFragmentManager, LawPendingTransactionInfoDialogFragment::class.java.simpleName)
            } else {
                val location: BusinessLocations = marker?.tag as BusinessLocations
                val dialogFragment: BusinessInfoDialogFragment = BusinessInfoDialogFragment.newInstance(location)
                dialogFragment.show(childFragmentManager, BusinessInfoDialogFragment::class.java.simpleName)
            }

        }
        return true
    }

    private class RenderClusterInfoWindow constructor(var context: Context, var map: GoogleMap?, clusterManager: ClusterManager<BusinessLocations>?) : DefaultClusterRenderer<BusinessLocations>(context, map, clusterManager) {
        override fun onBeforeClusterItemRendered(item: BusinessLocations, markerOptions: MarkerOptions) {
            val descriptor = bitmapDescriptorFromVector(context = context, color = item.color)
            markerOptions.icon(descriptor)
        }

        override fun onBeforeClusterRendered(cluster: Cluster<BusinessLocations>?, markerOptions: MarkerOptions?) {
            var descriptor: BitmapDescriptor? = bitmapDescriptorFromVector(context = context, color = "")
            try {
                val item = (cluster?.items as ArrayList<BusinessLocations>)[0]
                descriptor = bitmapDescriptorFromVector(context = context, color = item.color)
            } catch (e: Exception) {
                LogHelper.writeLog(exception = e)
            }
            markerOptions?.icon(descriptor)
        }

        override fun shouldRenderAsCluster(cluster: Cluster<BusinessLocations>?): Boolean {
            return false
            // return cluster?.size!! > 1
        }
    }

    private class RenderLawClusterInfoWindow constructor(var context: Context, var map: GoogleMap?, clusterManager: ClusterManager<LawPendingTransactionLocations>?) : DefaultClusterRenderer<LawPendingTransactionLocations>(context, map, clusterManager) {
        override fun onBeforeClusterItemRendered(item: LawPendingTransactionLocations, markerOptions: MarkerOptions) {
            val descriptor = bitmapDescriptorFromVector(context = context, color = item.color)
            markerOptions.icon(descriptor)
        }

        override fun onBeforeClusterRendered(cluster: Cluster<LawPendingTransactionLocations>?, markerOptions: MarkerOptions?) {
            var descriptor: BitmapDescriptor? = bitmapDescriptorFromVector(context = context, color = "")
            try {
                val item = (cluster?.items as ArrayList<LawPendingTransactionLocations>)[0]
                descriptor = bitmapDescriptorFromVector(context = context, color = item.color)
            } catch (e: Exception) {
                LogHelper.writeLog(exception = e)
            }
            markerOptions?.icon(descriptor)
        }


        override fun shouldRenderAsCluster(cluster: Cluster<LawPendingTransactionLocations>?): Boolean {
            return false
            //return cluster!!.size>1
        }
    }

    fun updateMarker(businessLocations: ArrayList<BusinessLocations>?) {
        locations.clear()
        businessLocations?.let {
            locations.addAll(it)
            bindBusinessMarkers(businessLocations, doClear = true, isForUpdate = true)
        }
    }

    fun updateLawMarker(lawAgentLocations: ArrayList<LawPendingTransactionLocations>?) {
        mLawPendingTransactionLocations.clear()
        lawAgentLocations?.let {
            mLawPendingTransactionLocations.addAll(it)
            bindLawAgentMarkers(lawAgentLocations, doClear = true, isForUpdate = true)
        }
    }

    interface Listener {
        fun dismissDialog()
        fun showAlertDialog(message: String?)
        fun showAlertDialogFailure(message: String, noRecordsFound: Int, onClickListener: DialogInterface.OnClickListener)
        fun showProgressDialog()
        fun onReloadClick()
    }

    override fun onCameraIdle() {
        mClusterManager?.cluster()
    }
}