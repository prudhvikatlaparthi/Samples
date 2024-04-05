package com.sgs.citytax.ui.fragments

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sgs.citytax.base.MyApplication

abstract class BaseFragment : Fragment() {
    companion object {
        @JvmField
        var sDisableFragmentAnimations: Boolean = false
    }

    abstract fun initComponents()
    override fun onAttach(context: Context) {
        super.onAttach(MyApplication.updateLanguage(context))
    }

    fun checkPermission(context: Context, permission: String): Int {
        return ContextCompat.checkSelfPermission(context, permission)
    }

    fun requestForPermission(permission: String, requestCode: Int) {
        requestPermissions(arrayOf(permission), requestCode)
    }

    fun requestForPermission(permission: Array<String>, requestCode: Int) {
        requestPermissions(permission, requestCode)
    }

    override fun onResume() {
        super.onResume()
        MyApplication.updateLanguage(context)
    }



}