package com.sgs.citytax.api.payload;

import com.google.gson.annotations.SerializedName;

public class FilterColumn {
    @SerializedName("colname")
    public String columnName;
    @SerializedName("ColumnValue")
    public String columnValue;
    @SerializedName("SrchType")
    public String srchType;


}
