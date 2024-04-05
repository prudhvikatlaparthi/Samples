package com.pru.responsiveapp.ui.alpha

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pru.responsiveapp.data.models.DataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LDViewModel : ViewModel() {
    var number = MutableLiveData(0)

    private val _obxData = MutableLiveData<MutableList<DataItem>>()
    val obxData: LiveData<MutableList<DataItem>> = _obxData

    var isNewDataAdded = false

    init {
        Log.d(LDViewModel::class.java.simpleName, "null() called")
        viewModelScope.launch(Dispatchers.IO) {
            delay(800)
            _obxData.postValue(dataList)
        }
    }

    private val dataList =
        mutableListOf(
            DataItem(
                title = "Apples",
                description = "Apples are among the most popular fruits, and also happen to be incredibly nutritious."
            ),
            DataItem(
                title = "Grapefruit",
                description = "Grapefruit is one of the healthiest citrus fruits."
            ),
            DataItem(
                title = "Pineapple",
                description = "Pineapple also contains bromelain, a mixture of enzymes known for its anti-inflammatory"
            ),
            DataItem(
                title = "Avocado",
                description = "Avocado is different from most other fruits."
            ),
            DataItem(
                title = "Blueberries",
                description = "Blueberries have powerful health benefits."
            ),
            DataItem(
                title = "Pomegranate",
                description = "Pomegranates are among the healthiest fruits you can eat."
            ),
            DataItem(
                title = "Mango",
                description = "Mangoes are an excellent source of vitamin C."
            ),
            DataItem(title = "Strawberries", description = "Strawberries are highly nutritious."),
            DataItem(
                title = "Cranberries",
                description = "Cranberries have impressive health benefits."
            ),
            DataItem(
                title = "Lemons",
                description = "Lemons are a very healthy citrus fruit known for their high vitamin C content."
            ),
            DataItem(title = "Durian", description = "Durian is nicknamed the “king of fruits.”"),
            DataItem(
                title = "Watermelon",
                description = "Watermelon is high in vitamins A and C. It’s also rich in some important antioxidants, including lycopene, carotenoids and cucurbitacin E."
            ),
            DataItem(
                title = "Olives",
                description = "Olives are a good source of vitamin E, iron, copper and calcium."
            ),
            DataItem(
                title = "Blackberries",
                description = "Blackberries are another incredibly healthy fruit, packed with vitamins, minerals, fiber and antioxidants."
            ),
            DataItem(
                title = "Oranges",
                description = "Oranges are one of the most popular and nutritious fruits in the world."
            ),
            DataItem(
                title = "Bananas",
                description = "Bananas are rich in vitamins and minerals and have quite a few health benefits to offer."
            ),
            DataItem(
                title = "Red and Purple Grapes",
                description = "Grapes are very healthy. Their high antioxidant content is what makes them stand out."
            ),
            DataItem(title = "Guava", description = "Guava has a remarkable nutrition profile."),
            DataItem(
                title = "Papaya",
                description = "Papaya is a very healthy fruit that is high in vitamin C, vitamin A, potassium and folate."
            ),
            DataItem(
                title = "Cherries",
                description = "Cherries are rich in nutrients, especially potassium, fiber and vitamin C."
            ),
        )

    fun updateData(dataItem: DataItem) {
        isNewDataAdded = true
        dataList.add(0, dataItem)
        _obxData.value = dataList
    }
}