package com.sgs.citytax.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VuComPropertyMaster(
        @SerializedName("PropertyName")
        var propertyName: String = "",
        @SerializedName("PropertyType")
        var propertyType: String = "",
        @SerializedName("SurveyNo")
        var surveyNo: String = "",
        @Expose(serialize = false, deserialize = false)
        var isLoading: Boolean = false
)