package com.pru.offlineapp.ui.prop_entry

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.pru.offlineapp.database.AppDatabase
import com.pru.offlineapp.entities.PropertyE
import kotlinx.coroutines.launch

class PropEntryViewModel : ScreenModel {
    fun upsert(propertyE: PropertyE?) {
        val propertyENew = PropertyE(
            propertyID = propertyE?.propertyID,
            propertyName = propName.value,
            propertyArea = propArea.value.toDoubleOrNull() ?: 0.0,
            webPropertyID = propertyE?.webPropertyID ?: 0,
            modifiedFrom = "A"
        )

        screenModelScope.launch {
            AppDatabase.getDatabase().propertyDao().upsert(propertyENew)
            propName.value = ""
            propArea.value = "0.0"
        }
    }

    fun delete(propertyE: PropertyE) {
        screenModelScope.launch {
            AppDatabase.getDatabase().propertyDao().deleteProperty(propertyE)
        }
    }

    val propName = mutableStateOf("")
    val propArea = mutableStateOf("0.0")
}
