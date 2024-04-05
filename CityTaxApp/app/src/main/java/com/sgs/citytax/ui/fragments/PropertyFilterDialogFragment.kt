package com.sgs.citytax.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sgs.citytax.R
import com.sgs.citytax.api.response.ComplaintIncidentDetailLocation
import com.sgs.citytax.databinding.FragmentComplaintsSearchInfoBinding
import com.sgs.citytax.databinding.FragmentPropertyFilterInfoBinding
import com.sgs.citytax.model.COMPropertyTypes
import com.sgs.citytax.model.PropertyDetailLocation
import com.sgs.citytax.util.*

class PropertyFilterDialogFragment : DialogFragment() {
    private lateinit var mBinding: FragmentPropertyFilterInfoBinding
    private var mListener: Listener? = null
    private var locations: MutableList<COMPropertyTypes> = arrayListOf()
    private var location: PropertyDetailLocation? = null

    /***
     * property id is for id value
     * property values is for spinner text
     */
    private val propertyIDArry: ArrayList<Int>? = arrayListOf()
    private val propertyTypeArry: ArrayList<String>? = arrayListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as Listener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(locations: MutableList<COMPropertyTypes>, location: PropertyDetailLocation?) = PropertyFilterDialogFragment().apply {
            this.locations = locations
            this.location = location
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_filter_info, container, false)
        initComponents()
        return mBinding.root
    }

    fun initComponents() {
        filterData()
        setListeners()
    }

    private fun filterData() {
        val sycoTaxList: ArrayList<String>? = arrayListOf()


        locations?.let {
            for ((index, value) in it.withIndex()) {
                /*sycoTaxList?.let { it1 ->
                    if ((it[index].PropertySycotaxID != null) && !it1.contains(it[index].PropertySycotaxID)) {
                        sycoTaxList.add(it[index].PropertySycotaxID.toString())
                    }
                }*/

                /***
                 *
                 * property values is for spinner text
                 */
                propertyTypeArry?.let { it1 ->
                    if ((it[index].propertyType != null) && !it1.contains(it[index].propertyType)) {
                        propertyTypeArry.add(it[index].propertyType.toString())
                        propertyIDArry!!.add(it[index].propertyTypeID!!.toInt())
                    }
                }
            }
        }

        sycoTaxList?.let {
            val incidentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, sycoTaxList)
            mBinding.edtSycoTaxID.setAdapter(incidentAdapter)
            mBinding.edtSycoTaxID.threshold = 2
        }

        propertyTypeArry?.let {
            it.add(0, getString(R.string.select))
            val incidentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, propertyTypeArry)
            mBinding.spnPropertyType.adapter = incidentAdapter
        }


        if (location != null) {

            location?.propertyVerificationRequestID?.let {
                mBinding.edtPropertyVerificationID.setText(it.toString())
            }

            /***
             * property id is for id value
             *
             */
            propertyIDArry?.let {
                it.add(0, 0) //for --select-- val
                for ((index, obj) in it.withIndex()) {
                    if (location?.propertyID == obj) {
                        mBinding.spnPropertyType.setSelection(index)
                        break
                    }
                }
            }

            location?.PropertySycotaxID?.let {
                mBinding.edtSycoTaxID.setText(it)
            }

            location?.owner?.let {
                mBinding.edtPropertyOwner.setText(it)
            }

            location?.fromDate?.let {
                mBinding.edtFromDate.setText(displayFormatDate(getDate(it, DateFormat, DateTimeTimeZoneMillisecondFormat)))
            }

            location?.toDate?.let {
                mBinding.edtToDate.isEnabled = true
                mBinding.edtToDate.setText(displayFormatDate(getDate(it, DateFormat, DateTimeTimeZoneMillisecondFormat)))
            }

        }
    }

    private fun setListeners() {
        mBinding.edtFromDate.setDisplayDateFormat(displayDateFormat)
        mBinding.edtToDate.setDisplayDateFormat(displayDateFormat)

        mBinding.edtFromDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.edtFromDate.text?.toString()?.let {
                    if (it.isNotEmpty()) {
                        mBinding.edtToDate.isEnabled = true
                        mBinding.edtToDate.setText("")
                        mBinding.edtToDate.setMinDate(parseDate(it, displayDateFormat).time)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        mBinding.btnCancel.setOnClickListener {
            resetValues()
        }

        mBinding.btnApply.setOnClickListener {

            location = PropertyDetailLocation()

            if (mBinding.edtPropertyVerificationID.text.toString().trim().isNotEmpty())
                location?.propertyVerificationRequestID = mBinding.edtPropertyVerificationID.text.toString().toInt()

            if (mBinding.spnPropertyType.selectedItem != null) {
               // location?.propertyID = (mBinding.spnPropertyType.selectedItem as PropertyDetailLocation).propertyID

                /***
                 * property id is for id value
                 * property values is for spinner text
                 */
                propertyTypeArry!!.forEachIndexed { index, value ->
                    if(value == mBinding.spnPropertyType.selectedItem && value != getString(R.string.select))
                        location?.propertyID = propertyIDArry?.get(index)
                }
                Log.e("this is loc proID",">>>>>>>>>."+location?.propertyID)
            }

            //(mBinding.spnPropertyType.sectedItem as COMPropertyTypes).propertyTypeID

            if (mBinding.edtSycoTaxID.text.toString().trim().isNotEmpty())
                location?.PropertySycotaxID = mBinding.edtSycoTaxID.text.toString()

            if (mBinding.edtPropertyOwner.text.toString().trim().isNotEmpty())
                location?.owner = mBinding.edtPropertyOwner.text.toString()

            if (mBinding.edtFromDate.text.toString().trim().isNotEmpty())
                location?.fromDate = serverFormatDate(mBinding.edtFromDate.text.toString())

            if (mBinding.edtToDate.text.toString().trim().isNotEmpty())
                location?.toDate = serverFormatDate(mBinding.edtToDate.text.toString())

            /*location = ComplaintIncidentDetailLocation()

            if (mBinding.edtFromDate.text.toString().trim().isNotEmpty() && mBinding.edtToDate.text.toString().trim().isEmpty()) {
                mListener?.showToast(getString(R.string.msg_provide) + " " + getString(R.string.to_date))
                return@setOnClickListener
            }

            if (mBinding.edtFromDate.text.toString().trim().isNotEmpty())
                location?.fromDate = serverFormatDate(mBinding.edtFromDate.text.toString())

            if (mBinding.edtToDate.text.toString().trim().isNotEmpty())
                location?.toDate = serverFormatDate(mBinding.edtToDate.text.toString())*/

            /*if (mBinding.spnComplaintCategory.selectedItem != null)
                location?.complaint = mBinding.spnComplaintCategory.selectedItem.toString()

            if (mBinding.spnComplaintSubCategory.selectedItem != null)
                location?.complaintSubtype = mBinding.spnComplaintSubCategory.selectedItem.toString()

            if (mBinding.spnZone.selectedItem != null)
                location?.zone = mBinding.spnZone.selectedItem.toString()

            if (mBinding.spnSector.selectedItem != null)
                location?.sector = mBinding.spnSector.selectedItem.toString()

            if (mBinding.spnStatus.selectedItem != null)
                location?.status = mBinding.spnStatus.selectedItem.toString()*/

           // mListener?.onApplyComplaintClick(location)
            mListener?.onApplyPropertyClick(location)
            dismiss()
        }
    }

    private fun resetValues() {
      //  location = null
        mListener?.onClearPropertyClick()

        mBinding.edtPropertyVerificationID.setText("")
        mBinding.spnPropertyType.setSelection(0)
        mBinding.edtSycoTaxID.setText("")
        mBinding.edtPropertyOwner.setText("")
        mBinding.edtFromDate.setText("")
        mBinding.edtToDate.setText("")


        /*mBinding.spnComplaintCategory.setSelection(0)
        mBinding.spnComplaintSubCategory.setSelection(0)
        mBinding.spnZone.setSelection(0)
        mBinding.spnSector.setSelection(0)
        mBinding.spnStatus.setSelection(0)*/
    }

    interface Listener {
        fun finish()
        fun showToast(message: String)
        fun showSnackbarMsg(message: String)
        fun onApplyPropertyClick(location: PropertyDetailLocation?)
        fun onClearPropertyClick()
    }
}
