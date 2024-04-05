package com.sgs.citytax.util

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DebouncingTextListener(
    lifecycle: Lifecycle,
    private val onDebouncingQueryTextChange: (before :String?, after : String?) -> Unit
) : TextWatcher {
    var debouncePeriod: Long = 500

    private val coroutineScope = lifecycle.coroutineScope

    private var beforeText : CharSequence? = null

    private var searchJob: Job? = null

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        beforeText = p0
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(newText: Editable?) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            newText?.let {
                delay(debouncePeriod)
                onDebouncingQueryTextChange(beforeText?.toString(),newText.toString())
            }
        }
    }
}