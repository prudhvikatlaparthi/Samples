package com.sgs.citytax.model

import com.sgs.citytax.api.payload.NewTicketCreationData
import java.math.BigDecimal

object ObjectHolder {
    var registerBusiness: RegisterBusiness = RegisterBusiness()
    var payment: Payment? = null
    var taxes: ArrayList<Any> = arrayListOf()
    var documents: ArrayList<COMDocumentReference> = arrayListOf()
    var notes: ArrayList<COMNotes> = arrayListOf()
    var violations: ArrayList<MultipleViolationTypes> = arrayListOf()
    var impoundments: ArrayList<MultipleImpoundmentTypes> = arrayListOf()
    var docCount: Int = 0
    var notesCount: Int = 0
    var ticketCreationData: NewTicketCreationData? = null
    var minAmount: BigDecimal? = BigDecimal.ZERO
    var remarks: String? = ""
    var dueNoticeID: Int? = null
    var dueNoticeReportingDate: Int? = null
    var dueDocumentCount: Int? = null
    var comDocumentReferences: ArrayList<COMDocumentReference> = arrayListOf()

    fun clearTax() {
        taxes = arrayListOf()
    }

    fun clearAll() {
        taxes = arrayListOf()
        registerBusiness = RegisterBusiness()
        payment = null
    }

    fun clearDueNoticeID()
    {
        dueNoticeID = null
        dueDocumentCount = null
    }
}