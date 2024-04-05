package com.pru.singleactivity.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.pru.singleactivity.listeners.OnResumeListener
import com.pru.singleactivity.utils.CommonUtils.prepareFragment

abstract class BaseFragment<VB : ViewBinding>(private val bindingInflater: (inflater: LayoutInflater) -> VB) :
    Fragment(), OnResumeListener {
    private var showHomeAsUp = false

    private var _binding: VB? = null

    protected val binding: VB
        get() = _binding as VB

    fun setBack(value: Boolean) {
        showHomeAsUp = value
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        if (_binding == null) {
            throw IllegalArgumentException("Binding cannot be null")
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        resume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareFragment()
        setup()
        observers()
        listeners()
    }

    abstract fun setup()

    abstract fun observers()

    abstract fun listeners()

    override fun resume() {
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}