package com.sgs.citytax.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.sgs.citytax.R
import com.sgs.citytax.util.LocationHelper
import java.util.*

class LocateDialogFragment : DialogFragment(), OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private var mLocationMarkerText: TextView? = null
    var btnSave: Button? = null
    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0
    private var listener: Listener? = null
    private var mHelper: LocationHelper? = null

    companion object {
        @JvmStatic
        fun newInstance(latitude: Double = 12.2383, longitude: Double = 1.5616) = LocateDialogFragment().apply {
            this.mLatitude = latitude
            this.mLongitude = longitude
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onStart() {
        super.onStart()
        //getLocation()
    }

    private fun getLocation() {
        listener?.showProgressDialog(R.string.msg_location_fetching)
        mHelper = LocationHelper(requireContext(), mLocationMarkerText?.rootView!!, requireActivity())
        mHelper?.fetchLocation()
        mHelper?.setListener(object : LocationHelper.Location {
            override fun found(latitude: Double, longitude: Double) {
                listener?.dismissDialog()
                if ((latitude!=null && latitude>0.0) && (longitude!=null && longitude>0.0)) {
                    mLatitude = latitude
                    mLongitude = longitude
                }
                onMapReady(googleMap)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mLatitude, mLongitude), 20f))
            }

            override fun start() {
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_locate, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.retainInstance = true
        mapFragment?.getMapAsync(this)
        mLocationMarkerText = rootView.findViewById(R.id.locationMarkertext) as TextView

        btnSave = rootView.findViewById(R.id.btnSave) as Button

        btnSave?.setOnClickListener {
            listener?.onLatLonFound(String.format(Locale.ENGLISH,"%.7f", mLatitude).toDouble(), String.format(Locale.ENGLISH,"%.7f", mLongitude).toDouble())
            dismiss()
        }

        return rootView
    }

    override fun onMapReady(mMap: GoogleMap?) {
        googleMap = mMap
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mLatitude, mLongitude), 20f))

        googleMap?.setOnCameraIdleListener {
            googleMap?.let {
                val target = it.cameraPosition.target
                mLatitude = target.latitude
                mLongitude = target.longitude
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val fragment: Fragment? = parentFragmentManager.findFragmentById(R.id.map)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        fragment?.let { ft?.remove(it) }
        ft?.commit()
    }

    interface Listener {
        fun onLatLonFound(latitude: Double?, longitude: Double?)
        fun dismissDialog()
        fun showProgressDialog(message: Int)
    }

}