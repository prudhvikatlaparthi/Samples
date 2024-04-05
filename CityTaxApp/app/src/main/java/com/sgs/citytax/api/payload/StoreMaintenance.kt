package com.sgs.citytax.api.payload

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class StoreMaintenance(
        @SerializedName("MaintenanceID")
        var maintenanceID: Int? = 0,
        @SerializedName("MaintenanceTypeID")
        var maintenanceTypeID: Int? = 0,
        @SerializedName("AssetID")
        var assetID: Int? = 0,
        @SerializedName("MaintenanceDate")
        var maintenanceDate: String? = null,
        @SerializedName("Vendor")
        var vendor: String? = null,
        @SerializedName("DistanceTravelled")
        var distanceTravelled: Int? = 0,
        @SerializedName("MaintenanceDetails")
        var maintenanceDetails: String? = "",
        @SerializedName("TotalCost")
        var totalCost: BigDecimal? = BigDecimal.ZERO,
        @SerializedName("InvoiceReference")
        var invoiceReference: String? = null,
        @SerializedName("docid")
        var docid: Int? = 0
)