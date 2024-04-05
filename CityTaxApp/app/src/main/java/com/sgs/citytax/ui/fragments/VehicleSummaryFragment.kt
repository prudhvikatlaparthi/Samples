package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.databinding.FragmentVehicleSummaryBinding
import com.sgs.citytax.model.COMDocumentReference
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.model.VehicleMaster
import com.sgs.citytax.ui.adapter.VehicleSummaryDocumentPreviewAdapter
import com.sgs.citytax.util.Constant
import com.sgs.citytax.util.IClickListener
import com.sgs.citytax.util.displayFormatDate

class VehicleSummaryFragment : BaseFragment(), View.OnClickListener, IClickListener {

    private lateinit var mBinding: FragmentVehicleSummaryBinding
    private var mListener: Listener? = null
    private var fromScreen: Any? = null
    private var comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()
    private var vehicleMaster: VehicleMaster? = null
    private var adapter: VehicleSummaryDocumentPreviewAdapter? = null

    private var sycoTaxId = ""
    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (arguments?.getSerializable(Constant.KEY_QUICK_MENU) != null)
                fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu

            vehicleMaster = arguments?.getParcelable(Constant.KEY_VEHICLE_DETAILS)

            if (arguments?.getSerializable(Constant.KEY_SYCO_TAX_ID) != null)
                sycoTaxId = arguments?.getSerializable(Constant.KEY_SYCO_TAX_ID) as String
        }
        //endregion
        bindData()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = if (parentFragment != null) {
                parentFragment as Listener
            } else {
                context as Listener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicle_summary, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }


    private fun bindData() {
        mBinding.tvVehicleSycotaxID.text = vehicleMaster?.vehicleSycotaxID
        mBinding.tvVehicleRegNo.text = vehicleMaster?.vehicleNo
        mBinding.tvVehicleRegDate.text = displayFormatDate(vehicleMaster?.registrationDate)
        mBinding.tvVehicleType.text = vehicleMaster?.vehicleTypeCode
        mBinding.tvPower.text = vehicleMaster?.power
//        mBinding.tvSeatCapacity.text = vehicleMaster?.loadCapacity.toString()
        mBinding.tvFuelType.text = vehicleMaster?.fuelType
        mBinding.tvHorsePower.text = vehicleMaster?.horsepower
 //       mBinding.tvLoadCapacity.text = vehicleMaster?.loadCapacity.toString()
        mBinding.tvValue.text = vehicleMaster?.value.toString()
        mBinding.tvVehicleOwner.text = vehicleMaster?.owner
        mBinding.tvPhoneNumber.text=vehicleMaster?.phoneNumbers
        mBinding.tvEmail.text=vehicleMaster?.emails

       if( vehicleMaster?.citizenSycotaxID!=null) {
           mBinding.tvSycotaxIDOf.text = getString(R.string.citizen_syco_tax_id)
           mBinding.tvSYcotaxID.text = vehicleMaster?.citizenSycotaxID
       }

        if( vehicleMaster?.citizenCardNo!=null) {
            mBinding.llIDCard.visibility = View.VISIBLE
            mBinding.tvIDCardNo.text = vehicleMaster?.citizenCardNo
        }

      if(vehicleMaster?.sycoTaxID!=null){
            mBinding.tvSycotaxIDOf.text=getString(R.string.receipt_id_sycotax)
            mBinding.tvSYcotaxID.text=vehicleMaster?.sycoTaxID
        }

        //region Address
        var address: String? = ""

        if (!vehicleMaster?.street.isNullOrEmpty()) {
           // mBinding.txtState.text = vehicleMaster.street
            address += vehicleMaster?.street
            address += ","
        } else {
           // mBinding.txtArdt.text = ""
            address += ""
        }

        if (!vehicleMaster?.city.isNullOrEmpty()) {
           // mBinding.txtCity.text = vehicleMaster.city
            address += vehicleMaster?.city
            address += ","
        } else {
           // mBinding.txtArdt.text = ""
            address += ""
        }

        //region Zone
        if (!vehicleMaster?.zn.isNullOrEmpty()) {
            //mBinding.txtArdt.text = vehicleMaster.zn
            address += vehicleMaster?.zn
            address += ","
        } else {
           // mBinding.txtArdt.text = ""
            address += ""
        }
        //endregion

        //region Sector
        if (!vehicleMaster?.sec.isNullOrEmpty()) {
           // mBinding.txtSector.text = vehicleMaster.sec
            address += vehicleMaster?.sec
            address += ","
        } else {
           // mBinding.txtSector.text = ""
            address += ""
        }
        //endregion


        //region LOT
        if (!vehicleMaster?.Block.isNullOrEmpty()) {
            address += vehicleMaster?.Block
           // mBinding.txtLot.text = vehicleMaster.block
            address += ","
        } else {
           // mBinding.txtLot.text = ""
            address += ""
        }
        //endregion

        //region Parcel
        if (!vehicleMaster?.doorno.isNullOrEmpty()) {
            //mBinding.txtParcel.text = vehicleMaster?.doorno
            address += vehicleMaster?.doorno
        } else {
           // mBinding.txtParcel.text = ""
            address += ""
        }
        //endregion

mBinding.tvAddress.text=address


        /* if (vehicleMaster?.street != null)
             mBinding.tvVehicleOwnerAddress.text = vehicleMaster?.street
         else
             mBinding.tvVehicleOwnerAddress.text = ""*/

        //receipt_id_sycotax


        setAddress(vehicleMaster)
        getVehicleDocuments(vehicleMaster?.vehicleNo)

        if( vehicleMaster?.loadCapacity!= null){
            mBinding.tvSeatCapacity.text = vehicleMaster?.loadCapacity.toString()
            mBinding.tvLoadCapacity.text = vehicleMaster?.loadCapacity.toString()
        }else{
            mBinding.tvSeatCapacity.text = ""
            mBinding.tvLoadCapacity.text = ""
        }
    }

    private fun setAddress(vehicleMaster: VehicleMaster?) {
        if (vehicleMaster != null) {
            val childData = GeoAddress()
            childData.state = vehicleMaster.street
            childData.city = vehicleMaster.city
            childData.zone = vehicleMaster.zn
            childData.sector = vehicleMaster.sec
            childData.plot = vehicleMaster.Plot
            childData.block = vehicleMaster.Block
            childData.doorNo = vehicleMaster.doorno


            mBinding.txtKeyOne.text = getString(R.string.state)
            mBinding.txtKeyTwo.text = getString(R.string.city)
            mBinding.txtKeyThree.text = getString(R.string.ardt)
            mBinding.txtKeyFour.text = getString(R.string.sector)
            mBinding.txtKeyFive.text = getString(R.string.txt_section)
            mBinding.txtKeySix.text = getString(R.string.txt_lot)
            mBinding.txtKeySeven.text = getString(R.string.txt_parcel)
           // mBinding.txtKeyEight.text = getString(R.string.state)

            if (!TextUtils.isEmpty(childData.state)) {
                childData.state?.let {
                    mBinding.txtValueOne.text = childData.state
                    mBinding.llOne.visibility = View.VISIBLE
                }
            }
            if (!TextUtils.isEmpty(childData.city)) {
                childData.city?.let {
                    mBinding.txtValueTwo.text = childData.city
                    mBinding.llTwo.visibility = View.VISIBLE
                }
            }
            if (!TextUtils.isEmpty(childData.zone)) {
                childData.zone?.let {
                    mBinding.txtValueThree.text = childData.zone
                    mBinding.llThree.visibility = View.VISIBLE
                }
            }
            if (!TextUtils.isEmpty(childData.sector)) {
                childData.sector?.let {
                    mBinding.txtValueFour.text = childData.sector
                    mBinding.llFour.visibility = View.VISIBLE
                }
            }
            if (!TextUtils.isEmpty(childData.plot)) {
                childData.plot?.let {
                    mBinding.txtValueFive.text = childData.plot
                    mBinding.llFive.visibility = View.VISIBLE
                }
            }
            if (!TextUtils.isEmpty(childData.block)) {
                childData.block?.let {
                    mBinding.txtValueSix.text = childData.block
                    mBinding.llSix.visibility = View.VISIBLE
                }
            }
            if (!TextUtils.isEmpty(childData.doorNo)) {
                childData.doorNo?.let {
                    mBinding.txtValueSeven.text = childData.doorNo
                    mBinding.llSeven.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getVehicleDocuments(vehicleNo: String?) {
        mListener?.showProgressDialog()
        APICall.getVehicleDocumentDetails(vehicleNo, "ADM_Vehicles", object : ConnectionCallBack<List<COMDocumentReference>> {
            override fun onSuccess(response: List<COMDocumentReference>) {
                mListener?.dismissDialog()
                comDocumentReferences = response as ArrayList<COMDocumentReference>
                if (comDocumentReferences.size > 0) {
                    adapter = VehicleSummaryDocumentPreviewAdapter(comDocumentReferences, this@VehicleSummaryFragment)
                    mBinding.recyclerView.adapter = adapter
                } else
                    mBinding.documentLayout.visibility = View.GONE //hiding the document key if empty
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mBinding.documentLayout.visibility = View.GONE
                if (message.isNotEmpty()) {
                }
                mListener?.showAlertDialog(message)
            }
        })
    }

    /* private fun getBusinessDocuments() {
         APICall.getDocumentDetails("${mStoreCustomerB2B?.organization?.organizationID!!}", "CRM_Organizations",
                 object : ConnectionCallBack<List<COMDocumentReference>> {
                     override fun onSuccess(response: List<COMDocumentReference>) {
                         mListener?.dismissDialog()
                         val comDocumentReferences = response as ArrayList<COMDocumentReference>
                         val list = arrayListOf<StoreCustomerB2B>()
                         val storeCustomerB2B = StoreCustomerB2B()
                         storeCustomerB2B.attachment = comDocumentReferences
                         list.add(storeCustomerB2B)
                           (mBinding.listView.expandableListAdapter as BusinessSummaryApprovalAdapter).update(getString(R.string.title_business_documents), list, mBinding.listView)
                     }

                     override fun onFailure(message: String) {
                         mListener?.dismissDialog()
                     }
                 })
     }*/


    override fun onClick(v: View?) {
        when (v?.id) {
            /*R.id.btnEdit -> {
                Handler().postDelayed({
                    mListener?.popBackStack()
                }, 300)
            }*/
        }
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
        fun setResult(resultCode: Int, intent: Intent)
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        view.let {
            when (view.id) {
                /*R.id.itemImageDocumentPreview -> {
                    val documentReferences: ArrayList<COMDocumentReference> = view.tag as ArrayList<COMDocumentReference>
                    val comDocumentReference = obj as COMDocumentReference
                    val intent = Intent(context, DocumentPreviewActivity::class.java)
                    documentReferences.remove(comDocumentReference)
                    documentReferences.add(0, comDocumentReference)
                    intent.putExtra(Constant.KEY_DOCUMENT, documentReferences)
                    startActivity(intent)
                }*/
            }
        }
    }

    override fun onLongClick(view: View, position: Int, obj: Any) {

    }

    fun onBackPressed() {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, null)
        mListener?.popBackStack()
    }
}