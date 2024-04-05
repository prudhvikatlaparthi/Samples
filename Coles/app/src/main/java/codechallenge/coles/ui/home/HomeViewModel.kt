package codechallenge.coles.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {

        }
    }
}