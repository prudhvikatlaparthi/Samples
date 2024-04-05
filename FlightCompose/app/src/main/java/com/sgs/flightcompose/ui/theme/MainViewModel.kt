package com.sgs.flightcompose.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class TextFiledData(
    var hint: String, var icon: ImageVector,
    var id: Int
)

class MainViewModel : ViewModel() {
    val formData = MutableStateFlow<List<TextFiledData>>(emptyList())
    val registerMap = mutableMapOf<Int, MutableState<String>>()
    val progressState = MutableStateFlow(0f)

    init {
        val listOfTextFiledData = listOf(
            TextFiledData(
                "First Name",
                Icons.Outlined.Person, 1
            ),
            TextFiledData(
                "Last Name",
                Icons.Outlined.Person, 2
            ),
            TextFiledData("Email", Icons.Outlined.Email, 3),
            TextFiledData("PassWord", Icons.Outlined.Lock, 4),
            TextFiledData("Confirm PassWord", Icons.Outlined.CheckCircle, 5)
        )
        listOfTextFiledData.forEach {
            registerMap[it.id] = mutableStateOf("")
        }
        formData.value = listOfTextFiledData
    }

    fun updateProgress() {
        val itemProgress = 100 / registerMap.size
        var totalProgress = 0f
        for (item in registerMap) {
            if (item.value.value.isNotEmpty()) {
                totalProgress += itemProgress
            }
        }
        progressState.value = totalProgress
    }
}