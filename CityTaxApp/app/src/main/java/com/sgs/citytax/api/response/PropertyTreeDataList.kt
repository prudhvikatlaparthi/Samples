package com.sgs.citytax.api.response

import com.example.treestructure.PropTreeData

class PropertyTreeDataList (
        var propTreeData: PropTreeData? = null,
        var childList: ArrayList<PropertyTreeDataList> = arrayListOf()
)