@file:OptIn(ExperimentalMaterial3Api::class)

package com.pru.offlineapp.ui.prop_entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.pru.offlineapp.entities.PropertyE

class PropEntryScreen(private val propertyE: PropertyE? = null) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { PropEntryViewModel() }
        PropEntryContent(screenModel, propertyE)
    }
}

@Composable
fun PropEntryContent(screenModel: PropEntryViewModel, propertyE: PropertyE?) {
    LaunchedEffect(Unit) {
        propertyE?.let {
            screenModel.propName.value = it.propertyName
            screenModel.propArea.value = it.propertyArea.toString()
        }
    }
    val navigator = LocalNavigator.current
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text(text = "Property Entry") }, navigationIcon = {
            androidx.compose.material3.IconButton(onClick = { navigator?.pop() }) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = "Back"
                )
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = screenModel.propName.value, onValueChange = {
                screenModel.propName.value = it
            }, label = {
                Text(text = "Property Name")
            })
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = screenModel.propArea.value, onValueChange = {
                screenModel.propArea.value = it
            }, label = {
                Text(text = "Property Area")
            }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                screenModel.upsert(propertyE)
                navigator?.pop()
            }) {
                Text(text = "Save")
            }
            propertyE?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = {
                    screenModel.delete(propertyE)
                    navigator?.pop()
                }) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PropEntryContent(PropEntryViewModel(), propertyE = null)
}