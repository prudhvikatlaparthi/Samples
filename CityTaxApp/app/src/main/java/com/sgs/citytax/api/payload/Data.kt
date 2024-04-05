package com.sgs.citytax.api.payload


import com.google.gson.annotations.SerializedName
import com.sgs.citytax.model.GeoAddress
import java.math.BigDecimal

data class Data(
    @SerializedName("acctid")
    val acctid: Int? = null,
    @SerializedName("FinalPrice")
    val finalPrice: BigDecimal? = null,
    @SerializedName("GeoAddress")
    val geoAddress: GeoAddress? = null,
    @SerializedName("IsPaymentByCash")
    var isPaymentByCash: Boolean? = null,
    @SerializedName("IsPaymentByWallet")
    var isPaymentByWallet: Boolean? = null,
    @SerializedName("IsPaymentByCheque")
    var isPaymentByCheque: Boolean? = null,
    @SerializedName("walletPaymentDetails")
    var walletPaymentDetails: SALWalletPaymentDetails? = null,
    @SerializedName("wallet")
    var wallet: PaymentByWallet? = null,
    @SerializedName("lstSalesItems")
    val lstSalesItems: List<LstSalesItem>? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("ph")
    val ph: String? = null,
    @SerializedName("usrorgbrid")
    val usrorgbrid: Int? = null,
    @SerializedName("telcode")
    var telephoneCode: Int? = null,
    @SerializedName("ChequeDetails")
    var chequeDetails: ChequeDetails ?= null,
    @SerializedName("filenameWithExt")
    var filenameWithExt: String? = null,
    @SerializedName("fileData")
    var fileData: String? = null,
    @SerializedName("sono")
    var sono: Int? = null,
)