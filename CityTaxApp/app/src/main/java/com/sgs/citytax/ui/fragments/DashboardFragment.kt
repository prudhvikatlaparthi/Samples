package com.sgs.citytax.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.GetAgentParkingPlaces
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentDashboardBinding
import com.sgs.citytax.model.AgentParkingPlace
import com.sgs.citytax.ui.BusinessSearchActivity
import com.sgs.citytax.util.Constant

class DashboardFragment : BaseFragment() {

    private lateinit var binding: FragmentDashboardBinding
    private var listener: Listener? = null
    private var mParkingPLaces: ArrayList<AgentParkingPlace> = arrayListOf()

    companion object {
        @JvmStatic
        fun newInstance() = DashboardFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        initComponents()
        return binding.root
    }

    override fun initComponents() {
        if (MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.ASO.name
                || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.PPS.name
            || MyApplication.getPrefHelper().agentTypeCode == Constant.AgentTypeCode.SLA.name) {
//            val manager: FragmentManager = childFragmentManager
//            val ft: FragmentTransaction = manager.beginTransaction()
//            val mapFragment: Fragment? = manager.findFragmentById(R.id.map)
//            mapFragment?.let {
//                ft.hide(mapFragment)
//                ft.commit()
//            }
            binding.flMap.visibility = GONE
//            binding.btnFullScreen.visibility = View.GONE
        } else {
            binding.flMap.visibility = VISIBLE
            if (checkAgentType()) {
                binding.btnReload.visibility = GONE
            } else {
                binding.btnReload.visibility = VISIBLE
            }
        }
        binding.btnFullScreen.setOnClickListener {
            val intent = Intent(context, BusinessSearchActivity::class.java)
            intent.putExtra(Constant.KEY_NAVIGATION_MENU, Constant.NavigationMenu.NAVIGATION_ONBOARDING)
            startActivity(intent)
        }

        binding.btnReload.setOnClickListener {
            listener?.onReloadClick()
        }

        if(MyApplication.getPrefHelper().allowParking == "Y"){
            binding.llParkingPlace.visibility = VISIBLE
            binding.flMap.visibility = GONE
            setEvents()
            bindSpinner()
        }
    }

    private fun bindSpinner() {
        listener?.showProgressDialog()
        val getAgentParkingPlaces = GetAgentParkingPlaces()
        APICall.getAgentParkingPlaces(getAgentParkingPlaces, object : ConnectionCallBack<List<AgentParkingPlace>> {
            override fun onSuccess(response: List<AgentParkingPlace>) {
                mParkingPLaces = arrayListOf()
                mParkingPLaces.add(AgentParkingPlace(-1, getString(R.string.select)))
                mParkingPLaces.addAll(response)
                if (mParkingPLaces.isNullOrEmpty())
                    binding.spnParkingPlace.adapter = null
                else {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mParkingPLaces)
                    binding.spnParkingPlace.adapter = adapter
                }
                listener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                binding.spnParkingPlace.adapter = null
                listener?.dismissDialog()
            }

        })
    }

    private fun setEvents() {
        binding.spnParkingPlace.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val parkingPlace = parent?.selectedItem as AgentParkingPlace?
                parkingPlace?.id?.let { it ->
                    MyApplication.getPrefHelper().parkingPlaceID = it
                    if (it != -1) {
                        parkingPlace.parking?.let {
                            MyApplication.getPrefHelper().parkingPlace = it
                        }
                    }
                }
            }

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

    interface Listener {
        fun showProgressDialog()
        fun dismissDialog()
        fun onReloadClick()
    }

}