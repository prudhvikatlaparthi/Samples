package com.pru.mar21_2024.presentation.ui.main

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class MainViewModel : ViewModel() {

    private val _dataState = MutableStateFlow<List<DataItem>>(emptyList())
    val dataState: StateFlow<List<DataItem>> get() = _dataState

    private val _imageDataState = MutableStateFlow<List<DataItem>>(emptyList())
    val imageState: StateFlow<List<DataItem>> get() = _imageDataState

    fun extractText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


        recognizer.process(image).addOnSuccessListener { visionText ->
            val dataList = visionText.textBlocks.asSequence().map { it.text
            }.flatMap { it.split(" ") }.flatMap { it.chunked(2) }.map { it.toIntOrNull() }.sortedBy { it }.map {

                DataItem(number = it) }.toList()
            _imageDataState.value = dataList
            Log.i("Prudhvi Log", "extractText: $dataList")

            for (block in visionText.textBlocks) {
                val blockText = block.text
                Log.i("Prudhvi Log", "extractText: $blockText")

            }
        }.addOnFailureListener {
            Log.i("Prudhvi Log", "extractText: ${it.message}")

        }
    }

    fun reset() {
        _dataState.value = emptyList()
        _imageDataState.value = emptyList()
    }
}

data class DataItem(var number: Int? = null, var isDrawn: Boolean = false)