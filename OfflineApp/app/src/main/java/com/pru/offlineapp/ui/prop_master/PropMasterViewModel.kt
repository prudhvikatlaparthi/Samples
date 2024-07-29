package com.pru.offlineapp.ui.prop_master

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.pru.offlineapp.database.AppDatabase
import com.pru.offlineapp.entities.PropertyE
import com.pru.offlineapp.sync_server.AppSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropMasterViewModel : ScreenModel {
    private val _propMasterData = MutableStateFlow<List<PropertyE>>(emptyList())
    val propMasterData = _propMasterData.asStateFlow()
    init {
        screenModelScope.launch {
            AppDatabase.getDatabase().propertyDao().getProperties().observeForever {
                _propMasterData.value = it
            }
        }
    }

    fun syncData() {
        screenModelScope.launch {
            AppSync.getInstance().uploadData()
        }
    }
}