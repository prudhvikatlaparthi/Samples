package com.sgs.citytax.api.response

import com.example.treestructure.PropTreeData
import com.google.gson.annotations.SerializedName

class PropertyTreeData (
        @SerializedName("PropertyTreeData")
        var propTreeData: ArrayList<PropTreeData>
)