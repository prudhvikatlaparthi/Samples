package com.pru.roomdbsample.model
import com.google.gson.annotations.SerializedName


data class Report(
    @SerializedName("Report")
    val report: List<ReportX> = listOf()
)

data class ReportX(
    @SerializedName("ID")
    val iD: Int ,
    @SerializedName("Layouts")
    val layouts: List<Layout> ,
    @SerializedName("Name")
    val name: String 
)

data class Layout(
    @SerializedName("Controls")
    val controls: List<Control> ,
    @SerializedName("Mergewithabovelayout")
    val mergewithabovelayout: Int ,
    @SerializedName("SortOrder")
    val sortOrder: Int 
)

data class Control(
    @SerializedName("Colour")
    val colour: String ,
    @SerializedName("DataType")
    val dataType: String ,
    @SerializedName("Hint")
    val hint: String ,
    @SerializedName("InfoText")
    val infoText: String ,
    @SerializedName("InputData")
    val inputData: String ,
    @SerializedName("InputType")
    val inputType: String ,
    @SerializedName("IsInfoIcon")
    val isInfoIcon: Int ,
    @SerializedName("NoOfColumns")
    val noOfColumns: Int ,
    @SerializedName("Value")
    val value: String 
)