package com.pru.hiltarchi.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Header(
    @SerialName("Adjustment")
    val adjustment: Int? = null,
    @SerialName("BillOrgBranchID")
    val billOrgBranchID: Int? = null,
    @SerialName("BillingAddress")
    val billingAddress: String? = null,
    @SerialName("BillingAddressSameAsShipping")
    val billingAddressSameAsShipping: String? = null,
    @SerialName("BillingCity")
    val billingCity: String? = null,
    @SerialName("BillingCountryCode")
    val billingCountryCode: String? = null,
    @SerialName("BillingState")
    val billingState: String? = null,
    @SerialName("BillingZipCode")
    val billingZipCode: String? = null,
    @SerialName("CreatedByDeviceID")
    val createdByDeviceID: String? = null,
    @SerialName("CustomerID")
    val customerID: Int? = null,
    @SerialName("DeliveryAddress")
    val deliveryAddress: String? = null,
    @SerialName("DeliveryArea")
    val deliveryArea: String? = null,
    @SerialName("DeliveryCharges")
    val deliveryCharges: Int? = null,
    @SerialName("DiscountOnTotal")
    val discountOnTotal: Int? = null,
    @SerialName("DiscountRuleID")
    val discountRuleID: Int? = null,
    @SerialName("EWayBillNo")
    val eWayBillNo: String? = null,
    @SerialName("HeaderStateIDOfSupply")
    val headerStateIDOfSupply: Int? = null,
    @SerialName("ID")
    val iD: Int? = null,
    @SerialName("ItemDiscount")
    val itemDiscount: Int? = null,
    @SerialName("ItemTax")
    val itemTax: Int? = null,
    @SerialName("Latitude")
    val latitude: String? = null,
    @SerialName("Longitude")
    val longitude: String? = null,
    @SerialName("ModifiedByDeviceID")
    val modifiedByDeviceID: String? = null,
    @SerialName("NetReceivable")
    val netReceivable: Int? = null,
    @SerialName("OrgID")
    val orgID: Int? = null,
    @SerialName("OutSideState")
    val outSideState: String? = null,
    @SerialName("PackagingCharges")
    val packagingCharges: Int? = null,
    @SerialName("PriceListID")
    val priceListID: Int? = null,
    @SerialName("RedeemAmountValue")
    val redeemAmountValue: Int? = null,
    @SerialName("Remarks")
    val remarks: String? = null,
    @SerialName("Rounding")
    val rounding: Int? = null,
    @SerialName("RoundingMethodID")
    val roundingMethodID: Int? = null,
    @SerialName("SOTypeCode")
    val sOTypeCode: String? = null,
    @SerialName("SalesHeaderID")
    val salesHeaderID: Int? = null,
    @SerialName("ShipOrgBranchID")
    val shipOrgBranchID: Int? = null,
    @SerialName("ShippingAddress")
    val shippingAddress: String? = null,
    @SerialName("ShippingCity")
    val shippingCity: String? = null,
    @SerialName("ShippingCountryCode")
    val shippingCountryCode: String? = null,
    @SerialName("ShippingDate")
    val shippingDate: String? = null,
    @SerialName("ShippingState")
    val shippingState: String? = null,
    @SerialName("ShippingZipCode")
    val shippingZipCode: String? = null,
    @SerialName("SubTotal")
    val subTotal: Int? = null,
    @SerialName("TCSAmount")
    val tCSAmount: Int? = null,
    @SerialName("TCSApplicableAmount")
    val tCSApplicableAmount: Int? = null,
    @SerialName("TCSGLCode")
    val tCSGLCode: Int? = null,
    @SerialName("TCSTaxID")
    val tCSTaxID: Int? = null,
    @SerialName("TCSTaxRate")
    val tCSTaxRate: Int? = null,
    @SerialName("Tax1")
    val tax1: Int? = null,
    @SerialName("Tax1GLCode")
    val tax1GLCode: Int? = null,
    @SerialName("Tax2")
    val tax2: Int? = null,
    @SerialName("Tax2GLCode")
    val tax2GLCode: Int? = null,
    @SerialName("Tax3")
    val tax3: Int? = null,
    @SerialName("Tax3GLCode")
    val tax3GLCode: Int? = null,
    @SerialName("TaxID1")
    val taxID1: Int? = null,
    @SerialName("TaxID2")
    val taxID2: Int? = null,
    @SerialName("TaxID3")
    val taxID3: Int? = null,
    @SerialName("TaxRate1")
    val taxRate1: Int? = null,
    @SerialName("TaxRate2")
    val taxRate2: Int? = null,
    @SerialName("TaxRate3")
    val taxRate3: Int? = null,
    @SerialName("TotalTaxable")
    val totalTaxable: Int? = null,
    @SerialName("UserOrgBranchID")
    val userOrgBranchID: Int? = null
)