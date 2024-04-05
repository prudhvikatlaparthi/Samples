package com.pru.findplaces

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.pru.findplaces.ui.theme.FindPlacesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FindPlacesTheme {
                var placesText by remember {
                    mutableStateOf("")
                }
                val context = LocalContext.current
                val placesResultLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            result.data?.let {
                                val place = Autocomplete.getPlaceFromIntent(it)
                                placesText = place.name ?: ""
                            }
                        } else if (result.resultCode == 2) {
                            val error = result.data?.let { Autocomplete.getStatusFromIntent(it) }
                            Toast.makeText(context, error?.statusMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier, contentAlignment = Alignment.TopStart) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .clickable {
                                    val fields = listOf(Place.Field.ID, Place.Field.NAME)
                                    val intent = Autocomplete
                                        .IntentBuilder(
                                            AutocompleteActivityMode.OVERLAY,
                                            fields
                                        )
                                        .setCountry("IN")
                                        .build(context)
                                    placesResultLauncher.launch(intent)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = placesText.ifEmpty { "Search Places" },
                                modifier = Modifier.alpha(0.6f)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search",modifier = Modifier.alpha(0.6f))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FindPlacesTheme {
        Greeting("Android")
    }
}