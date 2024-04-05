package com.example.treestructure

import com.google.gson.annotations.SerializedName
import com.sgs.citytax.api.response.PropertyTreeData
import java.util.*

class PropTreeDataResponse (
        @SerializedName("ReturnValue")
        var propTreeDataObj: PropertyTreeData
)


