package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName


data class StoreAndParkingTicketData(
        @SerializedName("objParkingTicket")
        var parkingTicket: NewTicketCreationData? = null,
        @SerializedName("IsFromApp")
        var isFromApp: Boolean? = true,
        @SerializedName("objTktPaymentModel")
        var ticketPaymentModel: TicketPaymentData? = null,
        @SerializedName("IsPaymentByCash")
        var isPaymentByCash: Boolean? = false,
        @SerializedName("IsPaymentByWallet")
        var isPaymentByWallet: Boolean? = false,
        @SerializedName("walletPaymentDetails")
        var walletPaymentDetails: SALWalletPaymentDetails? = null,
        @SerializedName("wallet")
        var wallet: PaymentByWallet? = null
)