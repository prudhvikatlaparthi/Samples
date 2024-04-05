package com.sgs.citytax.model

import com.sgs.citytax.api.payload.ChequeDetails
import com.sgs.citytax.api.payload.GenerateSalesTaxAndPaymentPayload
import com.sgs.citytax.api.payload.LstSalesItem
import com.sgs.citytax.api.payload.SubscriptionRenewal
import com.sgs.citytax.util.Constant
import java.math.BigDecimal

class Payment(var voucherNo: Int? = 0,
              var currentTaxInvoiceNo: Int = 0,
              var productCode: String? = "",
              var customerID: Int = 0,
              var customerMobileNo: String = "",
              var telcode: String = "",
              var otp: String = "",
              var amountDue: BigDecimal = BigDecimal.ZERO,
              var amountPaid: BigDecimal = BigDecimal.ZERO,
              var amountTotal: BigDecimal = BigDecimal.ZERO,
              var minimumPayAmount:BigDecimal = BigDecimal.ZERO,
              var paymentMode: Constant.PaymentMode = Constant.PaymentMode.CASH,
              var paymentType: Constant.PaymentType = Constant.PaymentType.TAX,
              var paymentBreakUps: List<PaymentBreakup> = arrayListOf(),
              var customer: BusinessOwnership? = null,
              var cartItem: CartItem? = null,
              var userID: String? = null,
              var subscriptionRenewal: SubscriptionRenewal? = null,
              var chequeDetails : ChequeDetails?= null,
              var filenameWithExt: String? = "",
              var fileData: String? = "",
              var TransactionTypeCode: String? = "",
              var TransactionNo: Int = 0,
              var SearchType: String? = "",
              var SearchValue: String? = "",
              var serviceRequestNo: Int? = 0,
              var vehicleNo: String? = "",
              var parkingPlaceID: Int = 0,
              var extraCharges:BigDecimal = BigDecimal.ZERO,
              var serviceEstimatedAmount:BigDecimal = BigDecimal.ZERO,
              var qty:Int = 0,
              var generateSalesTaxAndPayment : GenerateSalesTaxAndPaymentPayload? = null,
              var taxRuleBookCode: String? = "",
              var commissionPercentage : BigDecimal? = BigDecimal.ZERO,
              var commissionAmount: BigDecimal? = BigDecimal.ZERO,
              var penaltyPercentage : BigDecimal ?= null,
              var prosecutionFees : BigDecimal ?= null

)