package com.sgs.citytax.ui.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sgs.citytax.R
import com.sgs.citytax.api.APICall
import com.sgs.citytax.api.ConnectionCallBack
import com.sgs.citytax.api.SecurityContext
import com.sgs.citytax.api.payload.*
import com.sgs.citytax.api.response.*
import com.sgs.citytax.base.MyApplication
import com.sgs.citytax.databinding.FragmentSalesTaxBinding
import com.sgs.citytax.model.COMSectors
import com.sgs.citytax.model.COMZoneMaster
import com.sgs.citytax.model.GeoAddress
import com.sgs.citytax.model.ProductItem
import com.sgs.citytax.ui.AllTaxNoticesActivity
import com.sgs.citytax.ui.FragmentCommunicator
import com.sgs.citytax.ui.PaymentActivity
import com.sgs.citytax.ui.adapter.CitizenAdapter
import com.sgs.citytax.ui.adapter.SalesTaxProductsAdapter
import com.sgs.citytax.ui.adapter.SecurityTaxAdapter
import com.sgs.citytax.util.*
import kotlinx.android.synthetic.main.fragment_sales_tax.view.*
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.*


class SalesTaxFragment : BaseFragment(), IClickListener,
    SalesTaxProductsAdapter.NotifyQuantityChangeListener,
    SecurityTaxAdapter.NotifyQuantityChangeListener {

    private var finalPrice: BigDecimal = BigDecimal.ZERO
    private var roundedAmount: BigDecimal = BigDecimal.ZERO
    private var selectedCitizen: CitizenDataTable? = null
    private lateinit var binding: FragmentSalesTaxBinding
    private var listener: FragmentCommunicator? = null
    private lateinit var salesTaxProductsAdapter: SalesTaxProductsAdapter
    private lateinit var securityTaxProductsAdapter: SecurityTaxAdapter

    private var roundingMethod: RoundingMethod? = null
    private val productItemList: ArrayList<ProductItem> = arrayListOf()
    private val citizenDataList: MutableList<CitizenDataTable> = mutableListOf()

    private val mResponseZonesList: MutableList<COMZoneMaster> = mutableListOf()
    private val mResponseSectorsList: MutableList<COMSectors> = mutableListOf()
    private var adminOfficeAdress: AdminOfficeAdress? = null
    private lateinit var citizenTextListener: DebouncingTextListener
    private val TAG = "SalesTaxFragment"
    private var isActionEnabled: Boolean = true

    private var fromScreen: Constant.QuickMenu = Constant.QuickMenu.QUICK_MENU_SALES_TAX
    private val citizenAdapter: CitizenAdapter by lazy {
        CitizenAdapter(citizenDataList) {
            binding.rcPhone.isVisible = false
            selectedCitizen = it
            binding.edtCustomerNameLayout.isVisible = true
            disableEnableViews(false)
            getCitizensForMobileNumber(
                toFetchMobileNum = selectedCitizen?.Number,
                toFetchAccId = selectedCitizen?.acctid,
                replaceData = true
            )
        }
    }

    companion object {
        fun newInstance() = SalesTaxFragment()
    }

    override fun initComponents() {
        //region getArguments
        arguments?.let {
            fromScreen = arguments?.getSerializable(Constant.KEY_QUICK_MENU) as Constant.QuickMenu
        }
        setProductViews()
        initListeners()
        getAdressData()
        loadProducts()
        getDefaultRoundingMethod()
        getAdminOfficeAddressData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = if (parentFragment != null)
                parentFragment as FragmentCommunicator
            else context as FragmentCommunicator
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement Listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_tax, container, false)
        initComponents()
        return binding.root
    }

    private fun setProductViews() {
        binding.tvSalesTotal.text = formatWithPrecision(0.0)
        binding.tvSalesRounding.text = formatWithPrecision(0.0)
        binding.tvFinalPrice.text = formatWithPrecision(0.0)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())
        binding.rcPhone.layoutManager = layoutManager
        binding.rcPhone.adapter = citizenAdapter
    }

    private fun performSearch() {
        binding.edtCustomerNameLayout.isVisible = true
        if (binding.edtPhoneNumber.text.toString().length > 2) {
            getCitizensForMobileNumber(
                toFetchMobileNum = binding.edtPhoneNumber.text.toString(),
                toFetchAccId = 0,
                replaceData = false
            )
        } else {
            binding.edtCustomerName.setText("")
            binding.edtCustomerNameLayout.isVisible = false
            bindInitialCitizenDetails()
        }
    }

    private fun showMoreProductViews() {
        binding.edtCountryLayout.visibility = View.VISIBLE
        binding.edtStateLayout.visibility = View.VISIBLE
        binding.edtCityLayout.visibility = View.VISIBLE
        binding.edtStreetLayout.visibility = View.VISIBLE
        binding.edtSectionLayout.visibility = View.VISIBLE
        binding.edtLotLayout.visibility = View.VISIBLE
        binding.edtParcelLayout.visibility = View.VISIBLE
        binding.edtZipLayout.visibility = View.VISIBLE

        showUnderLineText(binding.btnShowMoreOrLess, getString(R.string.show_less))
    }

    private fun hideProductViews() {
        binding.edtCountryLayout.visibility = View.GONE
        binding.edtStateLayout.visibility = View.GONE
        binding.edtCityLayout.visibility = View.GONE
        binding.edtStreetLayout.visibility = View.GONE
        binding.edtSectionLayout.visibility = View.GONE
        binding.edtLotLayout.visibility = View.GONE
        binding.edtParcelLayout.visibility = View.GONE
        binding.edtZipLayout.visibility = View.GONE

        showUnderLineText(binding.btnShowMoreOrLess, getString(R.string.show_more))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (fromScreen == Constant.QuickMenu.QUICK_MENU_SALES_TAX) {
                    if (::salesTaxProductsAdapter.isInitialized) {
                        salesTaxProductsAdapter.filter.filter(newText)
                    }
                } else {
                    if (::securityTaxProductsAdapter.isInitialized) {
                        securityTaxProductsAdapter.filter.filter(newText)
                    }
                }
                return false
            }

        })
        binding.btnShowMoreOrLess.setOnClickListener {
            if (it.btnShowMoreOrLess.text.toString().equals(getString(R.string.show_more), true)) {
                showMoreProductViews()
            } else {
                hideProductViews()
            }
        }

        citizenTextListener =
            DebouncingTextListener(viewLifecycleOwner.lifecycle) { _, newText ->
                newText?.let {
                    performSearch()
                }
            }

        binding.rcPhone.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        binding.edtPhoneNumber.addTextChangedListener(citizenTextListener)

        binding.edtPhoneNumber.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })

        binding.btnSalesProceed.setOnClickListener {
            //commented below -- now to allow even if Citizen isn't provided
            /*if (selectedCitizen == null) {
                if (binding.edtPhoneNumber.text.toString().isBlank()) {
                    listener?.showAlertDialog(getString(R.string.msg_provide_valid_telephone))
                    binding.edtPhoneNumber.requestFocus()
                    return@setOnClickListener
                }
                if (binding.edtCustomerName.text?.isBlank() == true) {
                    listener?.showAlertDialog(getString(R.string.msg_provide_valid_name))
                    binding.edtCustomerName.requestFocus()
                    return@setOnClickListener
                }
            }*/
            //--END--

            if (binding.edtPhoneNumber.text.toString()
                    .isNotBlank() && binding.edtCustomerName.text.toString().isBlank()
            ) {
                binding.edtCustomerNameLayout.isVisible = true
                listener?.showAlertDialog(getString(R.string.msg_provide_valid_name))
                binding.edtCustomerName.requestFocus()
                return@setOnClickListener
            }

            if (binding.spnSector.adapter == null) {
                listener?.showAlertDialog(getString(R.string.validation_choose_sector))
                return@setOnClickListener
            }
            if (binding.spnZone.adapter == null) {
                listener?.showAlertDialog(getString(R.string.validation_choose_zone))
                return@setOnClickListener
            }
            if (finalPrice.compareTo(BigDecimal.ZERO) == 0) {
                listener?.showAlertDialog(getString(R.string.no_prod_added))
                return@setOnClickListener
            }
//            val roundedAmount = getRoundValue(
//                amount = finalPrice,
//                roundingPlace = roundingMethod?.roundingPlace ?: 0
//            )
            val lstSalesItems = mutableListOf<LstSalesItem>()
            if (fromScreen == Constant.QuickMenu.QUICK_MENU_SALES_TAX) {
                productItemList.forEach {
                    if (it.quantity > BigDecimal.ZERO) {
                        var exprydt: Date? =
                            if (it.product?.validityApplicable == "N") null else Date()
                        if (it.product?.validityApplicable == "Y" && it.product?.validForMonths ?: 0 > 0) {
                            exprydt = addMoths(
                                Date(),
                                it.product?.validForMonths!!
                            )
                        }
                        val lstSalesItem = LstSalesItem(
                            exprydt = exprydt,
                            itemCode = it.product?.itemCode,
                            lnprc = (it.product?.unitPrice
                                ?: BigDecimal.ZERO).multiply(it.quantity),
                            qty = it.quantity,
                            unitprc = it.product?.unitPrice
                        )
                        lstSalesItems.add(lstSalesItem)
                    }
                }
            } else {  //Security Tax
                productItemList.forEach {
                    if (it.quantity > BigDecimal.ZERO) {
                        val lstSalesItem = LstSalesItem(
                            exprydt = null,
                            itemCode = it.product?.itemCode,
                            lnprc = (it.product?.unitPrice
                                ?: BigDecimal.ZERO).multiply(it.quantity),
                            qty = it.quantity,
                            unitprc = it.product?.unitPrice,
                            prodCode = "Securitytax",
                            daysCnt = it.no_of_days,
                            noOfPerns = it.no_of_persons
                        )
                        lstSalesItems.add(lstSalesItem)
                    }
                }
            }

            val data = Data(
                acctid = selectedCitizen?.acctid ?: 0,
                finalPrice = roundedAmount,//finalPrice,
                geoAddress = getGeoAddress(),
                lstSalesItems = lstSalesItems,
                name = if (selectedCitizen == null) binding.edtCustomerName.text.toString() else null,
                ph = if (selectedCitizen == null) binding.edtPhoneNumber.text.toString() else null,
                telephoneCode = if (selectedCitizen == null) adminOfficeAdress?.telephoneCode else null,
                usrorgbrid = MyApplication.getPrefHelper().userOrgBranchID
            )
            val generateSalesTaxAndPaymentPayload =
                GenerateSalesTaxAndPaymentPayload(context = SecurityContext(), data = data)

            val payment = MyApplication.resetPayment()
            payment.amountDue = roundedAmount //finalPrice
            payment.amountTotal = roundedAmount //finalPrice
            payment.minimumPayAmount = roundedAmount //finalPrice
            payment.customerID = selectedCitizen?.acctid ?: 0
            payment.paymentType = Constant.PaymentType.SALES_TAX
            payment.generateSalesTaxAndPayment = generateSalesTaxAndPaymentPayload
            payment.prosecutionFees = adminOfficeAdress?.prosecutionFees
            payment.penaltyPercentage = adminOfficeAdress?.penaltyPercentage

            val intent = Intent(requireContext(), PaymentActivity::class.java)
            intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
            startActivityForResult(intent, Constant.REQUEST_CODE_PAYMENT_SUCCESS)

        }

        binding.spnZone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var zone: COMZoneMaster? = COMZoneMaster()
                if (p0 != null && p0.selectedItem != null)
                    zone = p0.selectedItem as COMZoneMaster
                if (selectedCitizen == null) {
                    filterSectors(zone?.zoneID!!)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun getGeoAddress(): GeoAddress? {
        if (selectedCitizen == null) {
            return GeoAddress(
                addressID = null,
                geoAddressID = null,
                accountId = 0,
                geoAddressType = null,
                countryCode = adminOfficeAdress?.cntrycode,
                country = adminOfficeAdress?.cntry,
                stateID = adminOfficeAdress?.stid,
                state = adminOfficeAdress?.st,
                cityID = adminOfficeAdress?.ctyid,
                city = adminOfficeAdress?.cty,
                zone = (binding.spnZone.selectedItem as COMZoneMaster?)?.zone,
                sectorID = (binding.spnSector.selectedItem as COMSectors?)?.sectorId,
                street = binding.edtStreet.text.toString(),
                zipCode = binding.edtZip.text.toString(),
                plot = binding.edtSection.text.toString(),
                block = binding.edtLot.text.toString(),
                doorNo = binding.edtParcel.text.toString(),
                latitude = null,
                longitude = null,
                description = null,
                sector = (binding.spnSector.selectedItem as COMSectors?)?.sector
            )
        }
        return null
    }

    private fun getCitizensForMobileNumber(
        toFetchMobileNum: String?,
        toFetchAccId: Int?,
        replaceData: Boolean
    ) {
        binding.citizenProgressView.isVisible = true
        val getCitizenForMobileNumberPayload = GetCitizenForMobileNumberPayload()
        getCitizenForMobileNumberPayload.mobile = toFetchMobileNum
        getCitizenForMobileNumberPayload.acctid = toFetchAccId
        APICall.getCitizenForMobileNumber(getCitizenForMobileNumberPayload,
            object : ConnectionCallBack<CitizenDataForMobileNumber> {
                override fun onSuccess(response: CitizenDataForMobileNumber) {
                    binding.citizenProgressView.isVisible = false
                    citizenDataList.clear()
                    response.Table?.let {
                        citizenDataList.addAll(it)
                        citizenAdapter.notifyDataSetChanged()
                    }
                    if (!replaceData) {
                        binding.rcPhone.isVisible = true
//                        filterZones(adminOfficeAdress?.ctyid ?: 0)
                        setZones(mResponseZonesList.getOrNull(0)?.zoneID ?: 0)
                        binding.rcPhone.scrollToPosition(0)
                    } else {
                        binding.rcPhone.isVisible = false
                        val citizen = citizenDataList[0]
                        binding.edtPhoneNumber.removeTextChangedListener(citizenTextListener)
                        binding.edtPhoneNumber.setText(citizen.Number)
                        binding.edtPhoneNumber.setSelection(binding.edtPhoneNumber.text.toString().length)
                        binding.edtPhoneNumber.addTextChangedListener(citizenTextListener)
                        binding.edtCustomerName.setText(citizen.acctname)
                        binding.edtCountry.setText(citizen.cntry)
                        binding.edtState.setText(citizen.st)
                        binding.edtCity.setText(citizen.cty)
                        binding.edtStreet.setText(citizen.Street)
                        binding.edtSection.setText(citizen.Section)
                        binding.edtLot.setText(citizen.lot)
                        binding.edtParcel.setText(citizen.Parcel)
                        binding.edtZip.setText(citizen.zip)
                        binding.spnZone.adapter = null
                        binding.spnSector.adapter = null
                        setZones(citizen.znid ?: 0)
                        setSector(citizen.SectorID ?: 0)
                    }

                }

                override fun onFailure(message: String) {
                    binding.citizenProgressView.isVisible = false
                    bindInitialCitizenDetails()
                }
            })

    }

    private fun disableEnableViews(enableView: Boolean) {
        isActionEnabled = enableView
        binding.spnZone.isEnabled = enableView
        binding.edtCustomerName.isEnabled = enableView
        binding.spnSector.isEnabled = enableView
        binding.edtStreet.isEnabled = enableView
        binding.edtZip.isEnabled = enableView
        binding.edtSection.isEnabled = enableView
        binding.edtLot.isEnabled = enableView
        binding.edtParcel.isEnabled = enableView
    }

    private fun setZones(zoneID: Int) {
        if (zoneID == 0) {
            binding.spnZone.adapter = null
        } else {
            val zoneArrayAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    mResponseZonesList
                )
            binding.spnZone.adapter = zoneArrayAdapter
        }
        mResponseZonesList.forEachIndexed { index, comZoneMaster ->
            if (comZoneMaster.zoneID == zoneID) {
                binding.spnZone.setSelection(index)
            }
        }
    }

    private fun setSector(sectorId: Int) {
        if (sectorId == 0) {
            binding.spnSector.adapter = null
        } else {
            val sectorArrayAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    mResponseSectorsList
                )
            binding.spnSector.adapter = sectorArrayAdapter
        }
        mResponseSectorsList.forEachIndexed { index, comZoneMaster ->
            if (comZoneMaster.sectorId == sectorId) {
                binding.spnSector.setSelection(index)
            }
        }
    }

    /*private fun filterZones(cityID: Int) {
        var zones: MutableList<COMZoneMaster> = ArrayList()
        var index = 0
        val zoneName = ""
        if (cityID <= 0) zones = ArrayList() else {
            for (zone in mResponseZonesList) {
                if (zone.cityID != null && cityID == zone.cityID) zones.add(zone)
                if (index <= 0 && !TextUtils.isEmpty(zoneName) && zone.zone != null && zoneName == zone.zone) index =
                    zones.indexOf(zone)
            }
        }
        if (index <= -1) index = 0
        if (zones.size > 0) {
            val zoneArrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, zones)
            binding.spnZone.adapter = zoneArrayAdapter
            binding.spnZone.setSelection(index)
        } else {
            binding.spnZone.adapter = null
        }
    }
*/

    private fun filterSectors(zoneID: Int) {
        var sectors: MutableList<COMSectors?> = ArrayList()
        var index = 0
        val sectorID = 0
        if (zoneID <= 0) sectors = ArrayList() else {
            for (sector in mResponseSectorsList) {
                if (sector.zoneId != null && zoneID == sector.zoneId) sectors.add(sector)
                if (index <= 0 && sectorID != 0 && sector.sectorId != null && sectorID == sector.sectorId) index =
                    sectors.indexOf(sector)
            }
        }
        if (sectors.size > 0) {
            if(isActionEnabled) binding.spnSector.isEnabled = true
            val sectorArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                sectors
            )
            binding.spnSector.adapter = sectorArrayAdapter
            binding.spnSector.setSelection(index)
        } else{
            binding.spnSector.adapter = null
           binding.spnSector.isEnabled = false
        }
    }

    private fun getAdressData() {
        APICall.getCorporateOfficeLOVValues(
            "CRM_AccountContacts",
            object : ConnectionCallBack<DataResponse> {
                override fun onSuccess(response: DataResponse) {
                    mResponseZonesList.clear()
                    mResponseZonesList.addAll(response.zoneMaster)
                    mResponseSectorsList.clear()
                    mResponseSectorsList.addAll(response.sectors)
//                    filterZones(cityID)
                    setZones(mResponseZonesList.getOrNull(0)?.zoneID ?: 0)
                }

                override fun onFailure(message: String) {
                }
            })
    }

    //to fetch all products list for Sales and Security Tax
    private fun loadProducts() {
        listener?.showProgressDialog()

        val getProductByType = GetProductByType()
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_SECURITY_TAX) {
            getProductByType.securitysal = "Y"
        }
        APICall.getProductsByType(
            getProductByType,
            object : ConnectionCallBack<List<SalesProductData>> {
                override fun onSuccess(response: List<SalesProductData>) {
                    Log.d(TAG, "onSuccess: $response")
                    listener?.dismissDialog()
                    addProductsToList(response as ArrayList<SalesProductData>)
                }

                override fun onFailure(message: String) {
                    listener?.dismissDialog()
                    if (message.isNotEmpty())
                        listener?.showAlertDialog(message)
                }
            })
    }

    private fun addProductsToList(salesProductsList: List<SalesProductData>) {

        if (salesProductsList.isNotEmpty()) {
            for (salesProductItem in salesProductsList) {
                productItemList.add(ProductItem(salesProductItem, BigDecimal.ZERO))
            }
        }

        binding.rcvSalesProducts.layoutManager = LinearLayoutManager(requireContext())
        if (fromScreen == Constant.QuickMenu.QUICK_MENU_SALES_TAX) {
            salesTaxProductsAdapter =
                SalesTaxProductsAdapter(
                    productItemList,
                    this@SalesTaxFragment,
                    this@SalesTaxFragment
                )
            binding.rcvSalesProducts.adapter = salesTaxProductsAdapter
        } else {
            securityTaxProductsAdapter = SecurityTaxAdapter(
                productItemList, this@SalesTaxFragment,
                this@SalesTaxFragment
            )
            binding.rcvSalesProducts.adapter = securityTaxProductsAdapter
        }
    }

    private fun getDefaultRoundingMethod() {
        listener?.showProgressDialog()
        APICall.getTableOrViewData(
            getDefaultRoundingMethodPayload(),
            object : ConnectionCallBack<GenericServiceResponse> {
                override fun onSuccess(response: GenericServiceResponse) {
                    listener?.dismissDialog()
                    if (response.result?.roundingMethods?.size ?: 0 > 0)
                        roundingMethod = response.result?.roundingMethods?.get(0)
                }

                override fun onFailure(message: String) {
                    listener?.dismissDialog()
                    if (message.isNotEmpty())
                        listener?.showAlertDialog(message)
                }
            })
    }

    private fun getDefaultRoundingMethodPayload(): AdvanceSearchFilter {
        val searchFilter = AdvanceSearchFilter()

        //region Apply Filters
        val filterList: ArrayList<FilterColumn> = arrayListOf()

        val filterColumn = FilterColumn()
        filterColumn.columnName = "[Default]"
        filterColumn.columnValue = "Y"
        filterColumn.srchType = "equal"

        filterList.add(filterColumn)
        searchFilter.filterColumns = filterList
        //endregion

        //region Apply Table/View details
        val tableDetails = TableDetails()
        tableDetails.tableOrViewName = "COM_RoundingMethods"
        tableDetails.primaryKeyColumnName = "RoundingMethodID"
        tableDetails.selectColoumns = "RoundingMethodID, RoundingMethod, RoundingPlace, [Default]"
        tableDetails.TableCondition = "OR"

        searchFilter.tableDetails = tableDetails
        //endregion

        return searchFilter
    }


    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    override fun onClick(view: View, position: Int, obj: Any) {
        val currentProductItem = obj as ProductItem
        view.let {
            when (it.id) {
                //Sales Tax
                R.id.imbPrdctMinusQty -> {

                    if (currentProductItem.product?.allwfrctnlqty == "Y") {
                        if (currentProductItem.quantity >= BigDecimal.valueOf(0.1)) {
                            decrementQty(currentProductItem)
                        }
                    } else {
                        if (currentProductItem.quantity >= BigDecimal.valueOf(1)) {
                            currentProductItem.quantity--
                        }
                    }
                    /* if (currentProductItem.quantity >= 1) {
                         currentProductItem.quantity--
                     }else{
                         return
                     }*/
                }
                R.id.imbPrdctPlusQty -> {
                    if (currentProductItem.product?.inventoryAllowed == "Y") {
                        val stockInHand =
                            currentProductItem.product?.stockInHand?.setScale(1, RoundingMode.DOWN)
                        if (stockInHand ?: BigDecimal.ZERO > currentProductItem.quantity) {
                            incrementQty(currentProductItem)
                        } else {
                            return
                        }
                    } else {
                        incrementQty(currentProductItem)
                    }
                }

                //Security Tax
                R.id.imbMinusDays -> {
                    if (currentProductItem.no_of_days >= BigInteger.ONE)
                        currentProductItem.no_of_days--
                    return@let
                }
                R.id.imbPlusDays -> {
                    currentProductItem.no_of_days++
                    return@let
                }
                R.id.imbPlusPersons -> {
                    currentProductItem.no_of_persons++
                    return@let
                }
                R.id.imbMinusPersons -> {
                    if (currentProductItem.no_of_persons >= BigInteger.ONE)
                        currentProductItem.no_of_persons--
                    return@let
                }

                else -> {
                }
            }
        }
    }

    private fun incrementQty(currentProductItem: ProductItem) {
        if (currentProductItem.product?.allwfrctnlqty == "Y") {
            val incrementNum = currentProductItem.quantity + BigDecimal(0.1)
            val incRoundVal: BigDecimal =
                incrementNum.setScale(2, RoundingMode.HALF_EVEN).stripTrailingZeros()
            currentProductItem.quantity = incRoundVal
        } else {
            currentProductItem.quantity++
        }
    }

    private fun decrementQty(currentProductItem: ProductItem) {
        val decrementNum = currentProductItem.quantity - BigDecimal(0.1)
        val decRoundVal: BigDecimal =
            decrementNum.setScale(2, RoundingMode.HALF_EVEN).stripTrailingZeros()
        currentProductItem.quantity = decRoundVal
    }

    override fun onQuantityUpdated(cartItem: ProductItem) {
        var t: BigDecimal? = BigDecimal.ZERO
//        var r: BigDecimal? = BigDecimal.ZERO
        cartItem.total = cartItem.getRoundedFinalPrice(
            roundingMethod?.roundingPlace
                ?: 0
        )
        cartItem.total = cartItem.getFinalPrice()

        cartItem.rounding = cartItem.getRounding(roundingMethod?.roundingPlace ?: 0)

        for (item in productItemList) {
            t = t?.plus(item.total)
//            r = r?.plus(item.rounding)
        }
        finalPrice = t ?: BigDecimal.ZERO

        roundedAmount = getRoundValue(
            amount = finalPrice,
            roundingPlace = roundingMethod?.roundingPlace ?: 0
        )

        binding.tvSalesTotal.text = formatWithPrecision(value = finalPrice)
        binding.tvSalesRounding.text = formatWithPrecision(value = finalPrice - roundedAmount)
        binding.tvFinalPrice.text = formatWithPrecision(value = roundedAmount)

    }

    override fun onLongClick(view: View, position: Int, obj: Any) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_CODE_PAYMENT_SUCCESS) {
            data?.let {
                if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID) && it.getIntExtra(
                        Constant.KEY_ADVANCE_RECEIVED_ID,
                        0
                    ) > 0
                ) {
                    val intent = Intent(requireContext(), AllTaxNoticesActivity::class.java)
                    // sending the SalesOrderNo
                    if (it.hasExtra(Constant.KEY_ADVANCE_RECEIVED_ID))
                        intent.putExtra(
                            Constant.KEY_ADVANCE_RECEIVED_ID,
                            it.getIntExtra(Constant.KEY_ADVANCE_RECEIVED_ID, 0)
                        )
                    intent.putExtra(
                        Constant.KEY_TAX_RULE_BOOK_CODE,
                        Constant.TaxRuleBook.SALES.Code
                    )
                    intent.putExtra(Constant.KEY_QUICK_MENU, fromScreen)
                    requireActivity().finish()
                    startActivity(intent)
                }
            }
        }
    }

    private fun getAdminOfficeAddressData() {
        val adminAdressPayload = GetAdminOfficeAddressPayload()
        adminAdressPayload.accountID = MyApplication.getPrefHelper().accountId
        APICall.getAdminOfficeAddress(
            adminAdressPayload,
            object : ConnectionCallBack<AdminOfficeAdress> {
                override fun onSuccess(response: AdminOfficeAdress) {
                    adminOfficeAdress = response
//                    val cityID = adminOfficeAdress?.ctyid ?: 0
//                    filterZones(cityID)

                    binding.edtCountry.setText(adminOfficeAdress?.cntry)
                    binding.edtState.setText(adminOfficeAdress?.st)
                    binding.edtCity.setText(adminOfficeAdress?.cty)
                    setZones(mResponseZonesList.getOrNull(0)?.zoneID ?: 0)
                    bindInitialCitizenDetails()
                }

                override fun onFailure(message: String) {
                }
            })
    }

    fun isPopUpVisible(): Boolean {
        return binding.rcPhone.isVisible
    }

    fun onBackPressed() {
        if (isPopUpVisible()) {
            binding.rcPhone.isVisible = false
        }
    }

    private fun bindInitialCitizenDetails() {
        binding.rcPhone.isVisible = false
        if (selectedCitizen != null) {
            binding.edtCustomerName.setText("")
            binding.edtCountry.setText(adminOfficeAdress?.cntry)
            binding.edtState.setText(adminOfficeAdress?.st)
            binding.edtCity.setText(adminOfficeAdress?.cty)
            binding.edtStreet.setText("")
            binding.edtSection.setText("")
            binding.edtLot.setText("")
            binding.edtParcel.setText("")
            binding.edtZip.setText("")
//        filterZones(adminOfficeAdress?.ctyid ?: 0)
            setZones(mResponseZonesList.getOrNull(0)?.zoneID ?: 0)
        }
        selectedCitizen = null
        disableEnableViews(true)
    }
}