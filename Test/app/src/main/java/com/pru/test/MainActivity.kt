package com.pru.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pru.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //    private lateinit var loader: LoaderDialog
    private lateinit var binding: ActivityMainBinding
    private var showDialog by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loader.setContent {
            MyDialog(showDialog = showDialog) {
                showDialog = !showDialog
            }
        }
        showDialog = true
//        loader = LoaderDialog(this)
//        loader.show()
    }
}

@Composable
fun MyDialog(showDialog: Boolean, onDismissRequest: () -> Unit) {
    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .background(White, shape = RoundedCornerShape(8.dp))
            ) {
                Column {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Loading")
                }
            }
        }
    }
}