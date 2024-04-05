package com.sgs.citytax.api.payload;

import com.google.gson.annotations.SerializedName;

public class TableDetails {
    @SerializedName("TableOrViewName")
    public String tableOrViewName;
    @SerializedName("PrimaryKeyColumnName")
    public String primaryKeyColumnName;
    @SerializedName("SelectColoumns")
    public String selectColoumns;
    @SerializedName("TableCondition")
    public String TableCondition;
    @SerializedName("SendCount")
    public boolean sendCount = false;
    @SerializedName("InitialTableCondition")
    public String initialTableCondition;

}
