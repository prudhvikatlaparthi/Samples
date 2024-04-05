package com.sgs.citytax.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.AssetDistanceAndDurationRateResponse
import com.sgs.citytax.api.response.BookingTenureBookingAdvanceResponse
import com.sgs.citytax.api.response.DataResponse
import com.sgs.citytax.databinding.FragmentAssetBookingLineEntryBinding
import com.sgs.citytax.model.*
import com.sgs.citytax.util.*
import com.sgs.citytax.util.Constant.KEY_FROM_DATE
import com.sgs.citytax.util.Constant.KEY_TO_DATE
import com.sgs.citytax.util.Constant.REQUEST_CODE_DATE_SELECTION
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class AssetBookingLineEntryFragment : BaseFragment() {

    private lateinit var mBinding: FragmentAssetBookingLineEntryBinding
    private var mListener: Listener? = null
    private var destinationCountriesList: List<COMCountryMaster> = arrayListOf()
    private var pickUpZonesList: List<COMZoneMaster> = arrayListOf()
    private var mCode = Constant.QuickMenu.QUICK_MENU_NONE
    private var mAssetTypes: List<AssetType> = arrayListOf()
    private var pickUpCitiesList: List<VUCOMCityMaster> = arrayListOf()
    private var mTenurePeriod: List<Tenure> = arrayListOf()
    private var mRentType: List<AssetRentType> = arrayListOf()
    private var mAssetCategories: List<AssetCategory> = arrayListOf()
    private var mCustomer: BusinessOwnership? = null
    private var mRequestLine: AssetBookingRequestLine? = null
    private var mBookingAdvance: BigDecimal = BigDecimal.ZERO
    private var mSecurityDeposit: BigDecimal = BigDecimal.ZERO
    private var mPaymentCycle: String = ""
    private var mDurationRate: String = ""
    private var mDistanceRate: String = ""
    private var pickUpCountriesList: List<COMCountryMaster> = arrayListOf()
    private var pickUpStatesList: List<COMStateMaster> = arrayListOf()
    private var pickUpSectorsList: List<COMSectors> = arrayListOf()
    private var destinationStatesList: List<COMStateMaster> = arrayListOf()
    private var destinationCitiesList: List<VUCOMCityMaster> = arrayListOf()
    private var destinationZonesList: List<COMZoneMaster> = arrayListOf()
    private var mAssets: List<Asset> = arrayListOf()
    private var destinationSectorsList: List<COMSectors> = arrayListOf()
    private var mSelectedAssetCategories: ArrayList<Int>? = arrayListOf()
    private var mSelectedAssets: ArrayList<Int>? = arrayListOf()
    private var mBranchID: Int = 0
    private var newTenurePeriod: Int = 0

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            if (it.containsKey(Constant.KEY_QUICK_MENU))
                mCode = it.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
            if (it.containsKey(Constant.KEY_BRANCH_ID))
                mBranchID = it.getInt(Constant.KEY_BRANCH_ID)
            if (it.containsKey(Constant.KEY_ASSET_BOOKING_LINE))
                mRequestLine = it.getParcelable(Constant.KEY_ASSET_BOOKING_LINE)
            if (it.containsKey(Constant.KEY_SELECTED_ASSET_CATEGORIES))
                mSelectedAssetCategories = it.getIntegerArrayList(Constant.KEY_SELECTED_ASSET_CATEGORIES)
            if (it.containsKey(Constant.KEY_SELECTED_ASSETS))
                mSelectedAssets = it.getIntegerArrayList(Constant.KEY_SELECTED_ASSETS)
            if (it.containsKey(Constant.KEY_ASSET_CATEGORIES) && !it.getParcelableArrayList<AssetCategory>(Constant.KEY_ASSET_CATEGORIES).isNullOrEmpty())
                mAssetCategories = it.getParcelableArrayList<AssetCategory>(Constant.KEY_ASSET_CATEGORIES) as ArrayList<AssetCategory>
            if (it.containsKey(Constant.KEY_ASSETS) && !it.getParcelableArrayList<Asset>(Constant.KEY_ASSETS).isNullOrEmpty())
                mAssets = it.getParcelableArrayList<Asset>(Constant.KEY_ASSETS) as ArrayList<Asset>
            if (it.containsKey(Constant.KEY_ASSET_TYPES) && !it.getParcelableArrayList<AssetType>(Constant.KEY_ASSET_TYPES).isNullOrEmpty())
                mAssetTypes = it.getParcelableArrayList<AssetType>(Constant.KEY_ASSET_TYPES) as ArrayList<AssetType>
            if (it.containsKey(Constant.KEY_PICK_UP_COUNTRIES) && !it.getParcelableArrayList<AssetType>(Constant.KEY_PICK_UP_COUNTRIES).isNullOrEmpty())
                pickUpCountriesList = it.getParcelableArrayList<COMCountryMaster>(Constant.KEY_PICK_UP_COUNTRIES) as ArrayList<COMCountryMaster>
            if (it.containsKey(Constant.KEY_DESTINATION_COUNTRIES) && !it.getParcelableArrayList<AssetType>(Constant.KEY_DESTINATION_COUNTRIES).isNullOrEmpty())
                destinationCountriesList = it.getParcelableArrayList<COMCountryMaster>(Constant.KEY_DESTINATION_COUNTRIES) as ArrayList<COMCountryMaster>
            if (it.containsKey(Constant.KEY_PICK_UP_STATES) && !it.getParcelableArrayList<COMStateMaster>(Constant.KEY_PICK_UP_STATES).isNullOrEmpty())
                pickUpStatesList = it.getParcelableArrayList<COMStateMaster>(Constant.KEY_PICK_UP_STATES) as ArrayList<COMStateMaster>
            if (it.containsKey(Constant.KEY_DESTINATION_STATES) && !it.getParcelableArrayList<COMStateMaster>(Constant.KEY_DESTINATION_STATES).isNullOrEmpty())
                destinationStatesList = it.getParcelableArrayList<COMStateMaster>(Constant.KEY_DESTINATION_STATES) as ArrayList<COMStateMaster>
            if (it.containsKey(Constant.KEY_PICK_UP_CITIES) && !it.getParcelableArrayList<VUCOMCityMaster>(Constant.KEY_PICK_UP_CITIES).isNullOrEmpty())
                pickUpCitiesList = it.getParcelableArrayList<VUCOMCityMaster>(Constant.KEY_PICK_UP_CITIES) as ArrayList<VUCOMCityMaster>
            if (it.containsKey(Constant.KEY_DESTINATION_CITIES) && !it.getParcelableArrayList<VUCOMCityMaster>(Constant.KEY_DESTINATION_CITIES).isNullOrEmpty())
                destinationCitiesList = it.getParcelableArrayList<VUCOMCityMaster>(Constant.KEY_DESTINATION_CITIES) as ArrayList<VUCOMCityMaster>
            if (it.containsKey(Constant.KEY_PICK_UP_ZONES) && !it.getParcelableArrayList<COMZoneMaster>(Constant.KEY_PICK_UP_ZONES).isNullOrEmpty())
                pickUpZonesList = it.getParcelableArrayList<COMZoneMaster>(Constant.KEY_PICK_UP_ZONES) as ArrayList<COMZoneMaster>
            if (it.containsKey(Constant.KEY_DESTINATION_ZONES) && !it.getParcelableArrayList<COMZoneMaster>(Constant.KEY_DESTINATION_ZONES).isNullOrEmpty())
                destinationZonesList = it.getParcelableArrayList<COMZoneMaster>(Constant.KEY_DESTINATION_ZONES) as ArrayList<COMZoneMaster>
            if (it.containsKey(Constant.KEY_PICK_UP_SECTORS) && !it.getParcelableArrayList<COMSectors>(Constant.KEY_PICK_UP_SECTORS).isNullOrEmpty())
                pickUpSectorsList = it.getParcelableArrayList<COMSectors>(Constant.KEY_PICK_UP_SECTORS) as ArrayList<COMSectors>
            if (it.containsKey(Constant.KEY_DESTINATION_SECTORS) && !it.getParcelableArrayList<COMSectors>(Constant.KEY_DESTINATION_SECTORS).isNullOrEmpty())
                destinationSectorsList = it.getParcelableArrayList<COMSectors>(Constant.KEY_DESTINATION_SECTORS) as ArrayList<COMSectors>
        }

        //endregion
        arguments = null
        setViews()
        setEvents()
        bindSpinner()
        if (mRequestLine == null)
            bindDefaultData()


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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_asset_booking_line_entry, container, false)
        initComponents()
        return mBinding.root
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCustomer = null
    }

    private fun setViews() {
        mBinding.edtStartDate.isEnabled = false
        if(mCode==Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING){
            mBinding.spnAssetCategory.isEnabled=false
            mBinding.spnAsset.isEnabled=false
            mBinding.spnPickUpCountry.isEnabled = false
            mBinding.spnPickUpState.isEnabled = false
            mBinding.spnPickUpCity.isEnabled = false
            mBinding.spnPickUpZone.isEnabled = false
            mBinding.spnPickUpSector.isEnabled = false
            mBinding.edtPickUpZipCode.isEnabled = false
            mBinding.edtPickUpStreet.isEnabled = false
            mBinding.edtPickUpSection.isEnabled = false
            mBinding.edtPickUpLot.isEnabled = false
            mBinding.edtPickUpParcel.isEnabled = false
            mBinding.spnDestinationExistingAddress.isEnabled = false
            mBinding.spnDestinationState.isEnabled = false
            mBinding.spnDestinationCity.isEnabled = false
            mBinding.spnDestinationZone.isEnabled = false
            mBinding.spnDestinationSector.isEnabled = false
            mBinding.edtDestinationStreet.isEnabled = false
            mBinding.edtDestinationZipCode.isEnabled = false
            mBinding.edtDestinationSection.isEnabled = false
            mBinding.edtDestinationLot.isEnabled = false
            mBinding.edtDestinationParcel.isEnabled = false
        }
    }

    private fun bindSpinner() {
        mListener?.showProgressDialog()
        filterPickUpCountries()
        filterDestinationCountries()
        if (mAssetCategories.isNullOrEmpty())
            mBinding.spnAssetCategory.adapter = null
        else {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mAssetCategories)
            mBinding.spnAssetCategory.adapter = adapter
        }
        bindData()
        mListener?.dismissDialog()
    }

    private fun bindDefaultData() {
        mBinding.edtQuantity.setText("1")
        mBinding.edtEstimatedAmount.setText("${formatWithPrecision(0.0)}")
        updateEstimatedAmount()
        updateBookingAdvance()
        updateSecurityDeposit()
        updatePaymentCycle()
        updateDistancePrice()
        updateDurationPrice()
    }

    private fun updateEstimatedAmount(amount: Double? = 0.0) {
        mBinding.edtEstimatedRentAmount.setText("${formatWithPrecision(amount)}")
    }

    private fun updateBookingAdvance() {
        val quantity = BigDecimal(if (mBinding.edtQuantity.text != null && !TextUtils.isEmpty(mBinding.edtQuantity.text)) mBinding.edtQuantity.text?.toString() else "0")
        mBinding.edtBookingAdvance.setText("${formatWithPrecision(quantity.times(mBookingAdvance).toDouble())}")
    }

    private fun updateSecurityDeposit() {
        val quantity = BigDecimal(if (mBinding.edtQuantity.text != null && !TextUtils.isEmpty(mBinding.edtQuantity.text)) mBinding.edtQuantity.text?.toString() else "0")
        mBinding.edtBookingSecurityDeposit.setText("${formatWithPrecision(quantity.times(mSecurityDeposit).toDouble())}")
    }

    private fun updateDistancePrice() {
        mBinding.edtDistancePrice.setText(getTariffWithCurrency(mDistanceRate))
    }

    private fun updateDurationPrice() {
        mBinding.edtDurationPrice.setText(getTariffWithCurrency(mDurationRate))
    }

    private fun updatePaymentCycle() {
        mBinding.edtPaymentCycle.setText(mPaymentCycle)
    }

    private fun fetchEstimatedPrice() {

        // region AssetCategory
        var assetCategoryID: Int? = null
        if (mBinding.spnAssetCategory.selectedItem != null) {
            val category = mBinding.spnAssetCategory.selectedItem as AssetCategory?
            category?.assetCategoryID?.let {
                if (it != -1)
                    assetCategoryID = it
            }
        }
        // endregion

        // region Asset
        var assetID: Int? = null
        if (mBinding.llAsset.visibility == VISIBLE) {
            if (mBinding.spnAsset.selectedItem != null) {
                val asset = mBinding.spnAsset.selectedItem as Asset?
                asset?.assetID?.let {
                    if (it != -1)
                        assetID = it
                }
            }
        }
        // endregion

        // region Date
        var fromDate = ""
        var endDate = ""
        if (mBinding.edtBusinessStartDate.text != null && !TextUtils.isEmpty(mBinding.edtBusinessStartDate.text.toString()))
            fromDate = formatDate(mBinding.edtBusinessStartDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyyhhmmssaa, Constant.DateFormat.DFyyyyMMddHHmmss)
        if (mBinding.edtBusinessEndDate.text != null && !TextUtils.isEmpty(mBinding.edtBusinessEndDate.text.toString()))
            endDate = formatDate(mBinding.edtBusinessEndDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyyhhmmssaa, Constant.DateFormat.DFyyyyMMddHHmmss)
        // endregion

        // region Quantity
        var quantity = 1
        if (mBinding.edtQuantity.text != null && !TextUtils.isEmpty(mBinding.edtQuantity.text.toString()))
            quantity = mBinding.edtQuantity.text.toString().trim().toInt()
        // endregion

        // region Distance
        var distance = 0
        if (mBinding.edtDistance.text != null && !TextUtils.isEmpty(mBinding.edtDistance.text.toString()))
            distance = mBinding.edtDistance.text.toString().trim().toInt()
        // endregion

        // region Tenure
        var tenurePeriod: Int? = null
        var area: Int? = null
        if (mBinding.llTenurePeriod.visibility == VISIBLE) {
            if (mBinding.spnTenurePeriod.selectedItem != null) {
                val tenure = mBinding.spnTenurePeriod.selectedItem as Tenure?
                tenure?.tenurePeriod?.let {
                    tenurePeriod = it
                    newTenurePeriod = it
                }
            }
            if (mBinding.edtArea.text != null && !TextUtils.isEmpty(mBinding.edtArea.text.toString()))
                area = mBinding.edtArea.text.toString().trim().toInt()
        } else {
            newTenurePeriod = 0
        }
        // endregion

        // region Rent Type
        var rentTypeID: Int? = null
        if (mBinding.llAssetRentType.visibility == VISIBLE) {
            if (mBinding.spnAssetRentType.selectedItem != null) {
                val rentType = mBinding.spnAssetRentType.selectedItem as AssetRentType?
                rentType?.assetRentTypeID?.let {
                    rentTypeID = it
                }
            }
        }
        // endregion

        if (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(endDate)) {
            mListener?.showProgressDialog()
            val assetBookingEstimatedPrice = AssetBookingEstimatedPrice()
            assetBookingEstimatedPrice.assetCategoryID = assetCategoryID
            assetBookingEstimatedPrice.assetID = assetID
            assetBookingEstimatedPrice.startDate = fromDate
            assetBookingEstimatedPrice.endDate = endDate
            assetBookingEstimatedPrice.quantity = quantity
            assetBookingEstimatedPrice.distance = distance
            assetBookingEstimatedPrice.tenure = tenurePeriod
            assetBookingEstimatedPrice.rentTypeID = rentTypeID
            assetBookingEstimatedPrice.area = area
            APICall.getAssetBookingEstimatedPrice(assetBookingEstimatedPrice, object : ConnectionCallBack<Double> {
                override fun onSuccess(response: Double) {
                    updateEstimatedAmount(response)
                    if (mBinding.llTenurePeriod.visibility == VISIBLE)
                        mBinding.edtEstimatedAmount.setText("${formatWithPrecision(response)}")
                    mListener?.dismissDialog()
                }

                override fun onFailure(message: String) {
                    mListener?.dismissDialog()
                    if (mBinding.llTenurePeriod.visibility == VISIBLE)
                        mBinding.edtEstimatedAmount.setText("${formatWithPrecision(0.0)}")
                    updateEstimatedAmount()
                }

            })
        }
    }

    private fun fetchTenureBookingAdvance() {
      /*  if(mCode==Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING){
          return
        }*/
        mListener?.showProgressDialog()

        // region AssetCategory
        var assetCategoryID: Int? = null
        if (mBinding.spnAssetCategory.selectedItem != null) {
            val category = mBinding.spnAssetCategory.selectedItem as AssetCategory?
            category?.assetCategoryID?.let {
                if (it != -1)
                    assetCategoryID = it
            }
        }
        // endregion

        // region Asset
        var assetID: Int? = null
        if (mBinding.llAsset.visibility == VISIBLE) {
            if (mBinding.spnAsset.selectedItem != null) {
                val asset = mBinding.spnAsset.selectedItem as Asset?
                asset?.assetID?.let {
                    if (it != -1)
                        assetID = it
                }
            }
        }
        // endregion

        val assetBookingTenureBookingAdvance = GetAssetBookingTenureBookingAdvance()
        assetBookingTenureBookingAdvance.assetID = assetID
        assetBookingTenureBookingAdvance.assetCategoryID = assetCategoryID
        APICall.getAssetBookingTenureBookingAdvance(assetBookingTenureBookingAdvance, object : ConnectionCallBack<BookingTenureBookingAdvanceResponse> {
            override fun onSuccess(response: BookingTenureBookingAdvanceResponse) {

                mBookingAdvance = response.bookingAdvance ?: BigDecimal.ZERO
                updateBookingAdvance()

                mSecurityDeposit = response.securityDeposit ?: BigDecimal.ZERO
                updateSecurityDeposit()


                if (response.tenures.isNullOrEmpty()) {
                    mBinding.spnTenurePeriod.adapter = null
                    mTenurePeriod = arrayListOf()
                    mBinding.llTenurePeriod.visibility = GONE

                   /* mBinding.tilLength.visibility = GONE
                    mBinding.tilWidth.visibility = GONE
                    mBinding.tilArea.visibility = GONE
                    mBinding.estimatedAmount.visibility = GONE
                    mBinding.tilEstimatedRentAmount.visibility = VISIBLE*/
                } else {
                    response.tenures?.let {
                        mTenurePeriod = it
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mTenurePeriod)
                        mBinding.spnTenurePeriod.adapter = adapter
                        mBinding.llTenurePeriod.visibility = VISIBLE

                       /* mBinding.tilLength.visibility = VISIBLE
                        mBinding.tilWidth.visibility = VISIBLE
                        mBinding.tilArea.visibility = VISIBLE
                        mBinding.estimatedAmount.visibility = VISIBLE
                        mBinding.tilEstimatedRentAmount.visibility = GONE*/
                    }
                    response.tenures = arrayListOf()
                }

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnTenurePeriod.adapter = null
                mTenurePeriod = arrayListOf()
                mBinding.llTenurePeriod.visibility = GONE
                mBookingAdvance = BigDecimal.ZERO
                updateBookingAdvance()
                mSecurityDeposit = BigDecimal.ZERO
                updateSecurityDeposit()
                mListener?.dismissDialog()
            }

        })
    }

    private fun getDistanceDurationRate() {
      /*  if(mCode==Constant.QuickMenu.QUICK_MENU_UPDATE_ASSET_BOOKING){
            return
        }*/
        // region AssetCategory
        var assetCategoryID: Int? = null
        if (mBinding.spnAssetCategory.selectedItem != null) {
            val category = mBinding.spnAssetCategory.selectedItem as AssetCategory?
            category?.assetCategoryID?.let {
                if (it != -1)
                    assetCategoryID = it
            }
        }
        // endregion

        // region Asset
        var assetID: Int? = null
        if (mBinding.llAsset.visibility == VISIBLE) {
            if (mBinding.spnAsset.selectedItem != null) {
                val asset = mBinding.spnAsset.selectedItem as Asset?
                asset?.assetID?.let {
                    if (it != -1) {
                        assetID = it
                    }
                }
                if(!asset?.unit.isNullOrBlank()){
                    mBinding.tilLength.visibility = VISIBLE
                    mBinding.tilWidth.visibility = VISIBLE
                    mBinding.tilArea.visibility = VISIBLE
                    mBinding.estimatedAmount.visibility = VISIBLE
                    mBinding.tilEstimatedRentAmount.visibility = GONE
                }else{
                    mBinding.tilLength.visibility = GONE
                    mBinding.tilWidth.visibility = GONE
                    mBinding.tilArea.visibility = GONE
                    mBinding.estimatedAmount.visibility = GONE
                    mBinding.tilEstimatedRentAmount.visibility = VISIBLE
                }
            }
        }
        // endregion

        // region Rent Type
        var rentTypeID: Int? = null
        if (mBinding.llAssetRentType.visibility == VISIBLE) {
            if (mBinding.spnAssetRentType.selectedItem != null) {
                val rentType = mBinding.spnAssetRentType.selectedItem as AssetRentType?
                rentType?.assetRentTypeID?.let {
                    rentTypeID = it
                }
            }
        }
        // endregion

        val assetDurationDistancePrice = GetAssetDurationDistancePrice()
        assetDurationDistancePrice.assetCategoryId = assetCategoryID
        assetDurationDistancePrice.assetId = assetID
        assetDurationDistancePrice.assetRentTypId = rentTypeID
        assetDurationDistancePrice.tenurePeriod=newTenurePeriod
        APICall.getAssetDurationAndDistancePrice(assetDurationDistancePrice, object : ConnectionCallBack<AssetDistanceAndDurationRateResponse> {
            override fun onSuccess(response: AssetDistanceAndDurationRateResponse) {
                response.paymentCycles?.let {
                    for (item in it) {
                        mPaymentCycle = item.paymentCycle ?: ""
                        mDurationRate = item.durationRate ?: ""
                        mDistanceRate = item.distanceRate ?: ""
                        break
                    }
                }

                updateDurationPrice()
                updateDistancePrice()
                updatePaymentCycle()
            }

            override fun onFailure(message: String) {
                mPaymentCycle = ""
                updatePaymentCycle()
                mDistanceRate = ""
                updateDistancePrice()
                mDurationRate = ""
                updateDurationPrice()
            }
        })
    }

    private fun getAssetRentType() {
        mListener?.showProgressDialog()

        // region AssetCategory
        var assetCategoryID: Int? = null
        if (mBinding.spnAssetCategory.selectedItem != null) {
            val category = mBinding.spnAssetCategory.selectedItem as AssetCategory?
            category?.assetCategoryID?.let {
                if (it != -1)
                    assetCategoryID = it
            }
        }
        // endregion

        // region Asset
        var assetID: Int? = null
        if (mBinding.llAsset.visibility == VISIBLE) {
            if (mBinding.spnAsset.selectedItem != null) {
                val asset = mBinding.spnAsset.selectedItem as Asset?
                asset?.assetID?.let {
                    if (it != -1)
                        assetID = it
                }
            }
        }
        // endregion

        val getAssetRentType = GetAssetRentType()
        getAssetRentType.assetID = assetID
        getAssetRentType.assetCategoryID = assetCategoryID
        APICall.getAssetRentTypeList(getAssetRentType, object : ConnectionCallBack<DataResponse> {
            override fun onSuccess(response: DataResponse) {

                mRentType = response.assetRentTypes

                if (mRentType.isNullOrEmpty()) {
                    mBinding.spnAssetRentType.adapter = null
                    mRentType = arrayListOf()
                    mBinding.llAssetRentType.visibility = GONE
                } else {
                    response.assetRentTypes.let {
                        mRentType = it
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mRentType)
                        mBinding.spnAssetRentType.adapter = adapter
                        mBinding.llAssetRentType.visibility = VISIBLE
                    }
                    response.assetRentTypes = arrayListOf()
                }

                mListener?.dismissDialog()
            }

            override fun onFailure(message: String) {
                mBinding.spnAssetRentType.adapter = null
                mRentType = arrayListOf()
                mBinding.llAssetRentType.visibility = GONE
                mListener?.dismissDialog()
            }

        })
    }

    private fun setEvents() {
        mBinding.edtLength.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var width = 0
                var area = 0
                if (mBinding.edtWidth.text != null && !TextUtils.isEmpty(mBinding.edtWidth.text.toString()))
                    width = mBinding.edtWidth.text.toString().toInt()
                if (s != null && !TextUtils.isEmpty(s))
                    area = (mBinding.edtLength.text.toString().toInt() * width)
                mBinding.edtArea.setText(area.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        mBinding.edtWidth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var length = 0
                var area = 0
                if (mBinding.edtLength.text != null && !TextUtils.isEmpty(mBinding.edtLength.text.toString()))
                    length = mBinding.edtLength.text.toString().toInt()
                if (s != null && !TextUtils.isEmpty(s))
                    area = (mBinding.edtWidth.text.toString().toInt() * length)
                mBinding.edtArea.setText(area.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        mBinding.btnGet.setOnClickListener {
            if ((mBinding.tilLength.visibility==View.VISIBLE) && mBinding.edtLength.text?.toString()?.trim().isNullOrEmpty()) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.length_)}")
            } else if ((mBinding.tilWidth.visibility==View.VISIBLE) &&mBinding.edtWidth.text?.toString()?.trim().isNullOrEmpty()) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.width)}")
            } else {
                fetchEstimatedPrice()
            }
        }



        mBinding.spnAssetCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                updateBookingStartAndEndDate()

                var category: AssetCategory? = AssetCategory()
                if (p0 != null && p0.selectedItem != null)
                    category = p0.selectedItem as AssetCategory

                if (isMovable(category)) {
                    mBinding.tilDistance.visibility = VISIBLE
                    mBinding.llAsset.visibility = GONE
                    mBinding.crdPickUpAddress.visibility = VISIBLE
                    mBinding.crdDestinationAddress.visibility = VISIBLE
                    mBinding.edtQuantity.isEnabled = true
                } else {
                    mBinding.tilDistance.visibility = GONE
                    mBinding.llAsset.visibility = VISIBLE
                    mBinding.crdPickUpAddress.visibility = GONE
                    mBinding.crdDestinationAddress.visibility = GONE
                    filterAssets(category?.assetCategoryID, mRequestLine?.assetID ?: 0)
                    mBinding.edtQuantity.setText("1")
                    mBinding.edtQuantity.isEnabled = false
                }
                category?.assetCategoryID?.let {
                    fetchEstimatedPrice()
                    fetchTenureBookingAdvance()
                    getAssetRentType()
                    getDistanceDurationRate()
                }
            }
        }

        mBinding.spnAsset.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                updateBookingStartAndEndDate()

                var asset: Asset? = Asset()
                if (p0 != null && p0.selectedItem != null)
                    asset = p0.selectedItem as Asset

                asset?.assetID?.let {
                    fetchEstimatedPrice()
                    fetchTenureBookingAdvance()
                    getAssetRentType()
                    getDistanceDurationRate()
                }
            }
        }

        mBinding.spnTenurePeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var tenure: Tenure? = Tenure()
                if (p0 != null && p0.selectedItem != null)
                    tenure = p0.selectedItem as Tenure

                tenure?.tenurePeriod?.let {
                    fetchEstimatedPrice()
                    getDistanceDurationRate()
                }
            }
        }

        mBinding.spnAssetRentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var rentType: AssetRentType? = AssetRentType()
                if (p0 != null && p0.selectedItem != null)
                    rentType = p0.selectedItem as AssetRentType

                rentType?.assetRentTypeID?.let {
                    fetchEstimatedPrice()
                    getDistanceDurationRate()
                }
            }
        }

        mBinding.edtBusinessStartDate.setOnClickListener {
            navigateToCalendar()
        }

        mBinding.edtBusinessEndDate.setOnClickListener {
            navigateToCalendar()
        }

        mBinding.spnPickUpCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var country: COMCountryMaster? = COMCountryMaster()
                if (p0 != null && p0.selectedItem != null)
                    country = p0.selectedItem as COMCountryMaster
                filterPickUpStates(country?.countryCode)
            }
        }

        mBinding.spnDestinationCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var country: COMCountryMaster? = COMCountryMaster()
                if (p0 != null && p0.selectedItem != null)
                    country = p0.selectedItem as COMCountryMaster
                filterDestinationStates(country?.countryCode)
            }
        }

        mBinding.spnPickUpState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var state: COMStateMaster? = COMStateMaster()
                if (p0 != null && p0.selectedItem != null)
                    state = p0.selectedItem as COMStateMaster
                filterPickUpCities(state?.stateID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnDestinationState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var state: COMStateMaster? = COMStateMaster()
                if (p0 != null && p0.selectedItem != null)
                    state = p0.selectedItem as COMStateMaster
                filterDestinationCities(state?.stateID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnPickUpCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var city: VUCOMCityMaster? = VUCOMCityMaster()
                if (p0 != null && p0.selectedItem != null)
                    city = p0.selectedItem as VUCOMCityMaster
                filterPickUpZones(city?.cityID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnDestinationCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var city: VUCOMCityMaster? = VUCOMCityMaster()
                if (p0 != null && p0.selectedItem != null)
                    city = p0.selectedItem as VUCOMCityMaster
                filterDestinationZones(city?.cityID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnPickUpZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterPickUpSectors(zone?.zoneID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.spnDestinationZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                filterDestinationSectors(zone?.zoneID!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        mBinding.edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && !TextUtils.isEmpty(s)) {
                    fetchEstimatedPrice()
                }
                updateBookingAdvance()
                updateSecurityDeposit()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        mBinding.edtDistance.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && !TextUtils.isEmpty(s)) {
                    fetchEstimatedPrice()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        mBinding.btnSave.setOnClickListener {
            if (isValid())
                save()
        }
    }

    private fun navigateToCalendar() {
        val fragment = AssetBookingDateSelectionFragment()
        val bundle = Bundle()
        bundle.putInt(Constant.KEY_BRANCH_ID, mBranchID)
        if (mBinding.spnAssetCategory.selectedItem != null) {
            val category = mBinding.spnAssetCategory.selectedItem as AssetCategory?
            category?.assetCategoryID?.let {
                bundle.putInt(Constant.KEY_ASSET_CATEGORY_ID, it)
            }
        }
        if (mBinding.llAsset.isVisible && mBinding.spnAsset.selectedItem != null) {
            val asset = mBinding.spnAsset.selectedItem as Asset?
            asset?.assetID?.let {
                bundle.putInt(Constant.KEY_ASSET_ID, it)
            }
        }
        mBinding.edtQuantity.text?.toString()?.trim()?.let {
            if (it.isNotEmpty() && it.toInt() != 0)
                bundle.putString(Constant.KEY_QUANTITY, it)
        }
        mRequestLine?.bookingRequestLineID?.let {
            bundle.putInt(Constant.KEY_BOOKING_REQUEST_LINE_ID, it)
        }
        newTenurePeriod?.let {
            if (mBinding.llTenurePeriod.visibility == VISIBLE) {
                bundle.putInt(Constant.KEY_TENURE_REQUEST_LINE_ID, it)
            } else {
                bundle.putInt(Constant.KEY_TENURE_REQUEST_LINE_ID, 0)
            }

        }

        fragment.arguments = bundle
        fragment.setTargetFragment(this, REQUEST_CODE_DATE_SELECTION)
        mListener?.addFragment(fragment, true)
    }

    private fun isValid(): Boolean {
        var exists = false
        if (mBinding.spnAsset.selectedItem != null) {
            val asset: Asset = mBinding.spnAsset.selectedItem as Asset
            mSelectedAssets?.let {
                if (it.contains(asset.assetID)) exists = true
            }
            val selectedAssetID = mRequestLine?.assetID
            selectedAssetID?.let {
                if (it == asset.assetID)
                    exists = false
            }
        } else if (mBinding.spnAssetCategory.selectedItem != null) {
            val category: AssetCategory = mBinding.spnAssetCategory.selectedItem as AssetCategory
            mSelectedAssetCategories?.let {
                if (it.contains(category.assetCategoryID)) exists = true
            }
            val selectedCategoryID = mRequestLine?.assetCategoryID
            selectedCategoryID?.let {
                if (it == category.assetCategoryID)
                    exists = false
            }
        }
        if (mBinding.llTenurePeriod.visibility == VISIBLE) {
            if ((mBinding.tilLength.visibility==View.VISIBLE) && mBinding.edtLength.text?.toString()?.trim().isNullOrEmpty()) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.length_)}")
                return false
            }
            if ((mBinding.tilWidth.visibility==View.VISIBLE) && mBinding.edtWidth.text?.toString()?.trim().isNullOrEmpty()) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.width)}")
                return false
            }
            if ((mBinding.tilArea.visibility==View.VISIBLE) && mBinding.edtArea.text?.toString()?.trim().isNullOrEmpty()) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.area)}")
                return false
            }
        }
        if (exists) {
            mListener?.showSnackbarMsg(getString(R.string.selected_category_assets_are_already_assigned_for_others_for_selected_booking_period))
            return false
        }
        if (mBinding.edtQuantity.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.booking_quantity)}")
            return false
        }

        if (mBinding.edtBusinessStartDate.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.booking_start_date)}")
            return false
        }
        if (mBinding.edtBusinessEndDate.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.booking_end_date)}")
            return false
        }
        if (mBinding.edtStartDate.text?.toString()?.trim().isNullOrEmpty()) {
            mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.start_date)}")
            return false
        }

        if (mBinding.crdPickUpAddress.visibility == VISIBLE) {
            if (mBinding.spnPickUpCountry.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.pickup)} ${getString(R.string.country)}")
                return false
            }
            if (mBinding.spnPickUpState.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.pickup)} ${getString(R.string.state)}")
                return false
            }
            if (mBinding.spnPickUpCity.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.pickup)} ${getString(R.string.city)}")
                return false
            }
            if (mBinding.spnPickUpZone.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.pickup)} ${getString(R.string.zone)}")
                return false
            }
            if (mBinding.spnPickUpSector.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.pickup)} ${getString(R.string.sector)}")
                return false
            }
            if (mBinding.spnDestinationCountry.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.destination)} ${getString(R.string.country)}")
                return false
            }
            if (mBinding.spnDestinationState.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.destination)} ${getString(R.string.state)}")
                return false
            }
            if (mBinding.spnDestinationCity.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.destination)} ${getString(R.string.city)}")
                return false
            }
            if (mBinding.spnDestinationZone.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.destination)} ${getString(R.string.zone)}")
                return false
            }
            if (mBinding.spnDestinationSector.selectedItem == null) {
                mListener?.showSnackbarMsg("${getString(R.string.msg_provide)} ${getString(R.string.destination)} ${getString(R.string.sector)}")
                return false
            }
        }
        return true
    }

    private fun save() {
        val requestLine = AssetBookingRequestLine()
        val validateAssetBooking = ValidateAssetBooking()

        validateAssetBooking.userOrgBranchID = mBranchID

        mRequestLine?.bookingRequestLineID?.let {
            requestLine.bookingRequestLineID = it
            validateAssetBooking.bookingRequestLineID = it
        }
        mRequestLine?.bookingRequestID?.let {
            requestLine.bookingRequestID = it
        }

        requestLine.uniqueID = UUID.randomUUID()
        mRequestLine?.uniqueID?.let {
            requestLine.uniqueID = it
        }

        if (mBinding.edtDistance.text != null && !TextUtils.isEmpty(mBinding.edtDistance.text.toString()))
            requestLine.distance = mBinding.edtDistance.text.toString().trim().toInt()
        if (mBinding.edtQuantity.text != null && !TextUtils.isEmpty(mBinding.edtQuantity.text.toString())) {
            requestLine.bookingQuantity = mBinding.edtQuantity.text.toString().trim().toInt()
            validateAssetBooking.bookingQuantity = mBinding.edtQuantity.text.toString().trim().toInt()
        }
        if (mBinding.edtStartDate.text != null && !TextUtils.isEmpty(mBinding.edtStartDate.text.toString()))
            requestLine.startDate = formatDate(mBinding.edtStartDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyy, Constant.DateFormat.DFyyyyMMdd)
        if (mBinding.edtBusinessStartDate.text != null && !TextUtils.isEmpty(mBinding.edtBusinessStartDate.text.toString())) {
            val startDate = formatDate(mBinding.edtBusinessStartDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyyhhmmssaa, Constant.DateFormat.DFyyyyMMddHHmmss)
            requestLine.bookingStartDate = startDate
            validateAssetBooking.bookingStartDate = startDate
        }
        if (mBinding.edtBusinessEndDate.text != null && !TextUtils.isEmpty(mBinding.edtBusinessEndDate.text.toString())) {
            val endDate = formatDate(mBinding.edtBusinessEndDate.text.toString().trim(), Constant.DateFormat.DFddMMyyyyhhmmssaa, Constant.DateFormat.DFyyyyMMddHHmmss)
            requestLine.bookingEndDate = endDate
            validateAssetBooking.bookingEndDate = endDate
        }
        if (mBinding.edtBookingAdvance.text != null && !TextUtils.isEmpty(mBinding.edtBookingAdvance.text.toString()))
            requestLine.bookingAdvance = BigDecimal(currencyToDouble(mBinding.edtBookingAdvance.text.toString()) as Long)
        if (mBinding.edtEstimatedRentAmount.text != null && !TextUtils.isEmpty(mBinding.edtEstimatedRentAmount.text.toString()))
            requestLine.estimatedRentAmount = BigDecimal(currencyToDouble(mBinding.edtEstimatedRentAmount.text.toString()) as Long)
        //requestLine.estimatedRentAmount = BigDecimal(currencyToDouble(mBinding.edtEstimatedRentAmount.text.toString().trim()))
        if (mBinding.edtBookingSecurityDeposit.text != null && !TextUtils.isEmpty(mBinding.edtBookingSecurityDeposit.text.toString()))
            requestLine.securityDeposit = BigDecimal(currencyToDouble(mBinding.edtBookingSecurityDeposit.text.toString()) as Long)



        if (mBinding.spnAssetCategory.selectedItem != null) {
            val category: AssetCategory = mBinding.spnAssetCategory.selectedItem as AssetCategory
            if (category.assetCategoryID != -1) {
                requestLine.assetCategoryID = category.assetCategoryID
                validateAssetBooking.assetCategoryID = category.assetCategoryID
                requestLine.assetCategory = category.assetCategory
                requestLine.allowPeriodicInvoice = category.allowPeriodicInvoice
            }
        }
        if (mBinding.spnAsset.selectedItem != null && mBinding.llAsset.visibility == VISIBLE) {
            val asset: Asset = mBinding.spnAsset.selectedItem as Asset
            asset.assetID?.let {
                if (it != -1) {
                    requestLine.assetID = it
                    validateAssetBooking.assetID = it
                }
            }
            requestLine.allowPeriodicInvoice = asset.allowPeriodicInvoice
        }
        if (mBinding.spnTenurePeriod.selectedItem != null && mBinding.llTenurePeriod.visibility == VISIBLE) {
            val tenure: Tenure = mBinding.spnTenurePeriod.selectedItem as Tenure
            tenure.tenurePeriod?.let {
                if (it != -1)
                    requestLine.tenurePeriod = it
            }
            if(mBinding.tilLength.visibility==VISIBLE && mBinding.tilWidth.visibility== VISIBLE && mBinding.tilArea.visibility== VISIBLE) {
                requestLine.length = mBinding.edtLength.text.toString().toInt()
                requestLine.width = mBinding.edtWidth.text.toString().toInt()
                requestLine.area = mBinding.edtArea.text.toString().toInt()
            }
            requestLine.estimatedRentAmount = BigDecimal(currencyToDouble(mBinding.edtEstimatedAmount.text.toString()) as Long)
        }
        if (mBinding.spnAssetRentType.selectedItem != null && mBinding.llAssetRentType.visibility == VISIBLE) {
            val rentType: AssetRentType = mBinding.spnAssetRentType.selectedItem as AssetRentType
            rentType.assetRentTypeID?.let {
                if (it != -1)
                    requestLine.rentTypeID = it
            }
        }

        // region Address
        if (mBinding.crdPickUpAddress.visibility == VISIBLE) {
            val pickupGeoAddress = GeoAddress()
            requestLine.pickupGeoAddress = pickupGeoAddress
            if (mBinding.spnPickUpCountry.selectedItem != null) {
                val country: COMCountryMaster = mBinding.spnPickUpCountry.selectedItem as COMCountryMaster
                country.countryCode?.let {
                    requestLine.countryCode = it
                    requestLine.pickupGeoAddress?.countryCode = it
                }
            }
            if (mBinding.spnPickUpState.selectedItem != null) {
                val state: COMStateMaster = mBinding.spnPickUpState.selectedItem as COMStateMaster
                state.stateID?.let {
                    requestLine.stateID = it
                    requestLine.state=state.state
                    requestLine.pickupGeoAddress?.state = state.state
                    requestLine.pickupGeoAddress?.stateID = it
                }
            }
            if (mBinding.spnPickUpCity.selectedItem != null) {
                val city: VUCOMCityMaster = mBinding.spnPickUpCity.selectedItem as VUCOMCityMaster
                city.cityID?.let {
                    requestLine.cityID = it
                    requestLine.city=city.city
                    requestLine.pickupGeoAddress?.city = city.city
                    requestLine.pickupGeoAddress?.cityID = it
                }
            }
            if (mBinding.spnPickUpZone.selectedItem != null) {
                val zone: COMZoneMaster = mBinding.spnPickUpZone.selectedItem as COMZoneMaster
                zone.zone?.let {
                    requestLine.zone = it
                    requestLine.pickupGeoAddress?.zone = it
                }
            }
            if (mBinding.spnPickUpSector.selectedItem != null) {
                val sector: COMSectors = mBinding.spnPickUpSector.selectedItem as COMSectors
                sector.sectorId?.let {
                    requestLine.sectorID = it
                    requestLine.pickupGeoAddress?.sectorID = it
                }
            }
            if (mBinding.edtPickUpStreet.text != null && !TextUtils.isEmpty(mBinding.edtPickUpStreet.text.toString())) {
                val street = mBinding.edtPickUpStreet.text.toString().trim()
                requestLine.street = street
                requestLine.pickupGeoAddress?.street = street
            }
            if (mBinding.edtPickUpZipCode.text != null && !TextUtils.isEmpty(mBinding.edtPickUpZipCode.text.toString())) {
                val zipCode = mBinding.edtPickUpZipCode.text.toString().trim()
                requestLine.zipCode = zipCode
                requestLine.pickupGeoAddress?.zipCode = zipCode
            }
            if (mBinding.edtPickUpSection.text != null && !TextUtils.isEmpty(mBinding.edtPickUpSection.text.toString())) {
                val plot = mBinding.edtPickUpSection.text.toString().trim()
                requestLine.plot = plot
                requestLine.pickupGeoAddress?.plot = plot
            }
            if (mBinding.edtPickUpLot.text != null && !TextUtils.isEmpty(mBinding.edtPickUpLot.text.toString())) {
                val block = mBinding.edtPickUpLot.text.toString().trim()
                requestLine.block = block
                requestLine.pickupGeoAddress?.block = block
            }
            if (mBinding.edtPickUpParcel.text != null && !TextUtils.isEmpty(mBinding.edtPickUpParcel.text.toString())) {
                val doorNo = mBinding.edtPickUpParcel.text.toString().trim()
                requestLine.doorNo = doorNo
                requestLine.pickupGeoAddress?.doorNo = doorNo
            }
            val geoAddress = GeoAddress()
            requestLine.geoAddress = geoAddress
            if (mBinding.spnDestinationCountry.selectedItem != null) {
                val country: COMCountryMaster = mBinding.spnDestinationCountry.selectedItem as COMCountryMaster
                country.countryCode?.let {
                    requestLine.countryCode = it
                    requestLine.geoAddress?.countryCode = it
                }
            }
            if (mBinding.spnDestinationState.selectedItem != null) {
                val state: COMStateMaster = mBinding.spnDestinationState.selectedItem as COMStateMaster
                state.stateID?.let {
                    requestLine.stateID = it
                    requestLine.state=state.state
                    requestLine.geoAddress?.stateID = it
                    requestLine.geoAddress?.state = state.state
                }
            }
            if (mBinding.spnDestinationCity.selectedItem != null) {
                val city: VUCOMCityMaster = mBinding.spnDestinationCity.selectedItem as VUCOMCityMaster
                city.cityID?.let {
                    requestLine.cityID = it
                    requestLine.city=city.city
                    requestLine.geoAddress?.cityID = it
                    requestLine.geoAddress?.city = city.city
                }
            }
            if (mBinding.spnDestinationZone.selectedItem != null) {
                val zone: COMZoneMaster = mBinding.spnDestinationZone.selectedItem as COMZoneMaster
                zone.zone?.let {
                    requestLine.zone = it
                    requestLine.geoAddress?.zone = it
                }
            }
            if (mBinding.spnDestinationSector.selectedItem != null) {
                val sector: COMSectors = mBinding.spnDestinationSector.selectedItem as COMSectors
                sector.sectorId?.let {
                    requestLine.sectorID = it
                    requestLine.geoAddress?.sectorID = it
                }
            }
            if (mBinding.edtDestinationStreet.text != null && !TextUtils.isEmpty(mBinding.edtDestinationStreet.text.toString())) {
                val street = mBinding.edtDestinationStreet.text.toString().trim()
                requestLine.street = street
                requestLine.geoAddress?.street = street
            }
            if (mBinding.edtDestinationZipCode.text != null && !TextUtils.isEmpty(mBinding.edtDestinationZipCode.text.toString())) {
                val zipCode = mBinding.edtDestinationZipCode.text.toString().trim()
                requestLine.zipCode = zipCode
                requestLine.geoAddress?.zipCode = zipCode
            }
            if (mBinding.edtDestinationSection.text != null && !TextUtils.isEmpty(mBinding.edtDestinationSection.text.toString())) {
                val plot = mBinding.edtDestinationSection.text.toString().trim()
                requestLine.plot = plot
                requestLine.geoAddress?.plot = plot
            }
            if (mBinding.edtDestinationLot.text != null && !TextUtils.isEmpty(mBinding.edtDestinationLot.text.toString())) {
                val block = mBinding.edtDestinationLot.text.toString().trim()
                requestLine.block = block
                requestLine.geoAddress?.block = block
            }
            if (mBinding.edtDestinationParcel.text != null && !TextUtils.isEmpty(mBinding.edtDestinationParcel.text.toString())) {
                val doorNo = mBinding.edtDestinationParcel.text.toString().trim()
                requestLine.doorNo = doorNo
                requestLine.geoAddress?.doorNo = doorNo
            }
        }
        // endregion

        validateAndSave(requestLine, validateAssetBooking)
    }

    private fun validateAndSave(requestLine: AssetBookingRequestLine, validateAssetBooking: ValidateAssetBooking) {
        mListener?.showProgressDialog()
        APICall.validateAssetBooking(validateAssetBooking, object : ConnectionCallBack<Boolean> {
            override fun onSuccess(response: Boolean) {
                mListener?.dismissDialog()
                if (response) {
                    mListener?.showSnackbarMsg(getString(R.string.asset_added_success))
                    Handler().postDelayed({
                        val data = Intent()
                        data.putExtra(Constant.KEY_ASSET_BOOKING_LINE, requestLine)
                        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, data)
                        mListener?.popBackStack()
                    }, 500)
                }
            }

            override fun onFailure(message: String) {
                mListener?.dismissDialog()
                mListener?.showAlertDialog(message)
            }

        })
    }

    private fun isMovable(category: AssetCategory?): Boolean {
        if (mAssetTypes.isNullOrEmpty())
            return false
        var isMovable = false
        for (assetType in mAssetTypes) {
            if (assetType.assetTypeCode == category?.assetTypeCode) {
                assetType.isMovable?.let {
                    isMovable = "Y".contentEquals(it)
                }
                break
            }
        }
        return isMovable
    }

    private fun filterAssets(assetCategoryID: Int?, assetID: Int? = 0) {
        var assets: MutableList<Asset> = ArrayList()
        var index = -1
        if (assetCategoryID == 0) assets = ArrayList() else {
            for (asset in mAssets) {
                if (assetCategoryID == asset.assetCategoryID) assets.add(asset)
                if (index <= -1 && assetID != 0 && asset.assetID != 0 && assetID == asset.assetID) index = assets.indexOf(asset)
            }
        }
        if (index <= -1) index = 0
        if (assets.size > 0) {
            val stateArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, assets)
            mBinding.spnAsset.adapter = stateArrayAdapter
            mBinding.spnAsset.setSelection(index)
        } else {
            mBinding.spnAsset.adapter = null
        }
    }

    // region Existing Address
    private fun filterPickUpCountries() {
        val countries: MutableList<COMCountryMaster> = java.util.ArrayList()
        var index = -1
        var countryCode: String? = "BFA"
        mRequestLine?.pickupGeoAddress?.countryCode?.let {
            countryCode = it
        }
        for (country in pickUpCountriesList) {
            countries.add(country)
            if (index <= -1 && countryCode != null && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode) index = countries.indexOf(country)
        }
        if (index <= -1) index = 0
        if (countries.size > 0) {
            val countryMasterArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, countries)
            mBinding.spnPickUpCountry.adapter = countryMasterArrayAdapter
            mBinding.spnPickUpCountry.setSelection(index)
            filterPickUpStates(countries[index].countryCode)
        } else {
            mBinding.spnPickUpCountry.adapter = null
            filterPickUpStates(countryCode)
        }
    }

    private fun filterDestinationCountries() {
        val countries: MutableList<COMCountryMaster> = java.util.ArrayList()
        var index = -1
        var countryCode: String? = "BFA"
        mRequestLine?.geoAddress?.countryCode?.let {
            countryCode = it
        }
        for (country in destinationCountriesList) {
            countries.add(country)
            if (index <= -1 && countryCode != null && !TextUtils.isEmpty(countryCode) && countryCode == country.countryCode) index = countries.indexOf(country)
        }
        if (index <= -1) index = 0
        if (countries.size > 0) {
            val countryMasterArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, countries)
            mBinding.spnDestinationCountry.adapter = countryMasterArrayAdapter
            mBinding.spnDestinationCountry.setSelection(index)
            filterDestinationStates(countries[index].countryCode)
        } else {
            mBinding.spnDestinationCountry.adapter = null
            filterDestinationStates(countryCode)
        }
    }

    private fun filterDestinationStates(countryCode: String?) {
        var states: MutableList<COMStateMaster> = java.util.ArrayList()
        var index = -1
        var stateID = 100497
        mRequestLine?.geoAddress?.stateID?.let {
            stateID = it
        }
        if (TextUtils.isEmpty(countryCode)) states = java.util.ArrayList() else {
            for (state in destinationStatesList) {
                if (countryCode == state.countryCode) states.add(state)
                if (index <= -1 && stateID != 0 && state.stateID != null && stateID == state.stateID) index = states.indexOf(state)
            }
        }
        if (index <= -1) index = 0
        if (states.size > 0) {
            val stateArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, states)
            mBinding.spnDestinationState.adapter = stateArrayAdapter
            mBinding.spnDestinationState.setSelection(index)
            filterDestinationCities(states[index].stateID!!)
        } else {
            mBinding.spnDestinationState.adapter = null
            filterDestinationCities(stateID)
        }
    }

    private fun filterPickUpStates(countryCode: String?) {
        var states: MutableList<COMStateMaster> = java.util.ArrayList()
        var index = -1
        var stateID = 100497
        mRequestLine?.pickupGeoAddress?.stateID?.let {
            stateID = it
        }
        if (TextUtils.isEmpty(countryCode)) states = java.util.ArrayList() else {
            for (state in pickUpStatesList) {
                if (countryCode == state.countryCode) states.add(state)
                if (index <= -1 && stateID != 0 && state.stateID != null && stateID == state.stateID) index = states.indexOf(state)
            }
        }
        if (index <= -1) index = 0
        if (states.size > 0) {
            val stateArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, states)
            mBinding.spnPickUpState.adapter = stateArrayAdapter
            mBinding.spnPickUpState.setSelection(index)
            filterPickUpCities(states[index].stateID!!)
        } else {
            mBinding.spnPickUpState.adapter = null
            filterPickUpCities(stateID)
        }
    }

    private fun filterDestinationCities(stateID: Int) {
        var cities: MutableList<VUCOMCityMaster> = java.util.ArrayList()
        var index = -1
        var cityID = 100312093
        mRequestLine?.geoAddress?.cityID?.let {
            cityID = it
        }
        if (stateID <= 0) cities = java.util.ArrayList() else {
            for (city in destinationCitiesList) {
                if (city.stateID != null && stateID == city.stateID) cities.add(city)
                if (index <= 0 && cityID != 0 && city.cityID != null && cityID == city.cityID) index = cities.indexOf(city)
            }
        }
        if (index <= -1) index = 0
        if (cities.size > 0) {
            val cityArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)
            mBinding.spnDestinationCity.adapter = cityArrayAdapter
            mBinding.spnDestinationCity.setSelection(index)
            filterDestinationZones(cities[index].cityID!!)
        } else {
            mBinding.spnDestinationCity.adapter = null
            filterDestinationZones(cityID)
        }
    }

    private fun filterPickUpCities(stateID: Int) {
        var cities: MutableList<VUCOMCityMaster> = java.util.ArrayList()
        var index = -1
        var cityID = 100312093
        mRequestLine?.pickupGeoAddress?.cityID?.let {
            cityID = it
        }
        if (stateID <= 0) cities = java.util.ArrayList() else {
            for (city in pickUpCitiesList) {
                if (city.stateID != null && stateID == city.stateID) cities.add(city)
                if (index <= 0 && cityID != 0 && city.cityID != null && cityID == city.cityID) index = cities.indexOf(city)
            }
        }
        if (index <= -1) index = 0
        if (cities.size > 0) {
            val cityArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)
            mBinding.spnPickUpCity.adapter = cityArrayAdapter
            mBinding.spnPickUpCity.setSelection(index)
            filterPickUpZones(cities[index].cityID!!)
        } else {
            mBinding.spnPickUpCity.adapter = null
            filterPickUpZones(cityID)
        }
    }

    private fun filterDestinationZones(cityID: Int) {
        var zones: MutableList<COMZoneMaster> = java.util.ArrayList()
        var index = 0
        var zoneName: String? = ""
        mRequestLine?.geoAddress?.zone?.let {
            zoneName = it
        }
        if (cityID <= 0) zones = java.util.ArrayList() else {
            for (zone in destinationZonesList) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone) index = zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnDestinationZone.adapter = zoneArrayAdapter
            mBinding.spnDestinationZone.setSelection(index)
            filterDestinationSectors(zones[index].zoneID!!)
        } else {
            mBinding.spnDestinationZone.adapter = null
            filterDestinationSectors(index)
        }
    }

    private fun filterPickUpZones(cityID: Int) {
        var zones: MutableList<COMZoneMaster> = java.util.ArrayList()
        var index = 0
        var zoneName: String? = ""
        mRequestLine?.pickupGeoAddress?.zone?.let {
            zoneName = it
        }
        if (cityID <= 0) zones = java.util.ArrayList() else {
            for (zone in pickUpZonesList) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone) index = zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            mBinding.spnPickUpZone.adapter = zoneArrayAdapter
            mBinding.spnPickUpZone.setSelection(index)
            filterPickUpSectors(zones[index].zoneID!!)
        } else {
            mBinding.spnPickUpZone.adapter = null
            filterPickUpSectors(index)
        }
    }

    private fun filterDestinationSectors(zoneID: Int) {
        var sectors: MutableList<COMSectors?> = java.util.ArrayList()
        var index = 0
        var sectorID = 0
        mRequestLine?.geoAddress?.sectorID?.let {
            sectorID = it
        }
        if (zoneID <= 0) sectors = java.util.ArrayList() else {
            for (sector in destinationSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId)
                    sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId)
                    index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnDestinationSector.adapter = sectorArrayAdapter
            mBinding.spnDestinationSector.setSelection(index)
        } else mBinding.spnDestinationSector.adapter = null
    }

    private fun filterPickUpSectors(zoneID: Int) {
        var sectors: MutableList<COMSectors?> = java.util.ArrayList()
        var index = 0
        var sectorID = 0
        mRequestLine?.pickupGeoAddress?.sectorID?.let {
            sectorID = it
        }
        if (zoneID <= 0) sectors = java.util.ArrayList() else {
            for (sector in pickUpSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId)
                    sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId)
                    index = sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            val sectorArrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sectors)
            mBinding.spnPickUpSector.adapter = sectorArrayAdapter
            mBinding.spnPickUpSector.setSelection(index)
        } else mBinding.spnPickUpSector.adapter = null
    }
    // endregion

    private fun updateBookingStartAndEndDate() {
        mBinding.edtBusinessStartDate.setText("")
        mBinding.edtBusinessEndDate.setText("")
        mBinding.edtStartDate.setText("")
        mRequestLine?.bookingStartDate?.let {
            mBinding.edtBusinessStartDate.setText(if (it.length == 23) formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyhhmmssaa) else formatDate(it, Constant.DateFormat.DFyyyyMMddHHmmss, Constant.DateFormat.DFddMMyyyyhhmmssaa))
        }
        mRequestLine?.bookingEndDate?.let {
            mBinding.edtBusinessEndDate.setText(if (it.length == 23) formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyyhhmmssaa) else formatDate(it, Constant.DateFormat.DFyyyyMMddHHmmss, Constant.DateFormat.DFddMMyyyyhhmmssaa))
        }
        mRequestLine?.startDate?.let {
            setLimitsForStartDate()
            mBinding.edtStartDate.isEnabled = true
            mBinding.edtStartDate.setText(if (it.length == 23) formatDate(it, Constant.DateFormat.SERVER, Constant.DateFormat.DFddMMyyyy) else formatDate(it, Constant.DateFormat.DFyyyyMMdd, Constant.DateFormat.DFddMMyyyy))
        }
    }

    private fun bindData() {
        mRequestLine?.let { it ->
            it.startDate?.let {
                mBinding.edtStartDate.setText(if (it.contains("T")) formatDate(it) else it)
            }
            it.distance?.let {
                mBinding.edtDistance.setText("$it")
            }
            it.bookingQuantity?.let {
                mBinding.edtQuantity.setText("$it")
            }
            it.length?.let {
                mBinding.edtLength.setText("$it")
            }
            it.width?.let {
                mBinding.edtWidth.setText("$it")
            }
            it.area?.let {
                mBinding.edtArea.setText("$it")
            }
            mBookingAdvance = it.bookingAdvance ?: BigDecimal.ZERO
            updateBookingAdvance()
            mSecurityDeposit = it.securityDeposit ?: BigDecimal.ZERO
            updateSecurityDeposit()
            it.estimatedRentAmount?.let {
                updateEstimatedAmount(it.toDouble())
                if (mBinding.llTenurePeriod.visibility == VISIBLE)
                    mBinding.edtEstimatedAmount.setText("${formatWithPrecision(it)}")
            }
            for ((index, category) in mAssetCategories.withIndex()) {
                if (it.assetCategoryID == category.assetCategoryID) {
                    mBinding.spnAssetCategory.setSelection(index)
                    break
                }
            }
            for ((index, asset) in mAssets.withIndex()) {
                if (it.assetID == asset.assetID) {
                    mBinding.spnAsset.setSelection(index)
                    break
                }
            }
            for ((index, rentType) in mRentType.withIndex()) {
                if (it.rentTypeID == rentType.assetRentTypeID) {
                    mBinding.spnAssetRentType.setSelection(index)
                    break
                }
            }
            filterAssets(it.assetCategoryID, it.assetID)
            filterPickUpCountries()

            it.geoAddress?.street?.let {
                mBinding.edtDestinationStreet.setText(it)
            }
            it.geoAddress?.zipCode?.let {
                mBinding.edtDestinationZipCode.setText(it)
            }
            it.geoAddress?.plot?.let {
                mBinding.edtDestinationSection.setText(it)
            }
            it.geoAddress?.block?.let {
                mBinding.edtDestinationLot.setText(it)
            }
            it.geoAddress?.doorNo?.let {
                mBinding.edtDestinationParcel.setText(it)
            }

            it.pickupGeoAddress?.street?.let {
                mBinding.edtPickUpStreet.setText(it)
            }
            it.pickupGeoAddress?.zipCode?.let {
                mBinding.edtPickUpZipCode.setText(it)
            }
            it.pickupGeoAddress?.plot?.let {
                mBinding.edtPickUpSection.setText(it)
            }
            it.pickupGeoAddress?.block?.let {
                mBinding.edtPickUpLot.setText(it)
            }
            it.pickupGeoAddress?.doorNo?.let {
                mBinding.edtPickUpParcel.setText(it)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_DATE_SELECTION) {
            data?.let { it ->
                if (it.hasExtra(KEY_TO_DATE))
                    it.getStringExtra(KEY_TO_DATE)?.let {
                        mBinding.edtBusinessEndDate.setText(formatDate(it, Constant.DateFormat.DFyyyyMMddhhmmaa, Constant.DateFormat.DFddMMyyyyhhmmssaa))
                    }
                if (it.hasExtra(KEY_FROM_DATE))
                    it.getStringExtra(KEY_FROM_DATE)?.let {
                        mBinding.edtBusinessStartDate.setText(formatDate(it, Constant.DateFormat.DFyyyyMMddhhmmaa, Constant.DateFormat.DFddMMyyyyhhmmssaa))
                    }
                fetchEstimatedPrice()
                setLimitsForStartDate()
            }
        }
    }

    private fun setLimitsForStartDate() {
        val startDate = mBinding.edtBusinessStartDate.text.toString()
        val endDate = mBinding.edtBusinessEndDate.text.toString()
        if (TextUtils.isEmpty(startDate))
            return
        if (TextUtils.isEmpty(endDate))
            return
        mBinding.edtStartDate.isEnabled = true
        mBinding.edtStartDate.setText("")
        mBinding.edtStartDate.setDisplayDateFormat(Constant.DateFormat.DFddMMyyyy.value)

        mBinding.edtStartDate.setMinDate(getTimeStamp(startDate, Constant.DateFormat.DFddMMyyyyhhmmssaa))
        mBinding.edtStartDate.setMaxDate(getTimeStamp(endDate, Constant.DateFormat.DFddMMyyyyhhmmssaa))
    }

    interface Listener {
        fun showToolbarBackButton(title: Int)
        fun addFragment(fragment: Fragment, addToBackStack: Boolean)
        fun showProgressDialog()
        fun dismissDialog()
        fun showAlertDialog(message: String)
        fun showSnackbarMsg(message: String?)
        fun popBackStack()
    }

}