package com.pru.navigationcomponentapp

import android.os.Bundle
import android.view.View

class FixFragment() : BaseFragment(R.layout.fix_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBar("In detail page",enableDrawer = false)
    }
}