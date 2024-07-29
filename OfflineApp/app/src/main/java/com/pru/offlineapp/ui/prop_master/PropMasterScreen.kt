package com.pru.offlineapp.ui.prop_master

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.pru.offlineapp.ui.prop_entry.PropEntryScreen
import com.pru.offlineapp.utils.singleClick
import kotlinx.coroutines.FlowPreview

class PropMasterScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { PropMasterViewModel() }
        PropMasterContent(screenModel = screenModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun PropMasterContent(screenModel: PropMasterViewModel) {
    val state = screenModel.propMasterData.collectAsState()
    val navigator = LocalNavigator.current
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Properties") })
    }, floatingActionButton = {
        Column(modifier = Modifier) {
            FloatingActionButton(onClick = {
                screenModel.syncData()
            }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(10.dp))
            singleClick { manager ->
                FloatingActionButton(onClick = {
                    manager.processEvent {
                        navigator?.push(PropEntryScreen())
                    }
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
        }
    }) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            items(state.value) {
                Column(modifier = Modifier
                    .singleClick {
                        navigator?.push(PropEntryScreen(it))
                    }
                    .padding(10.dp), verticalArrangement = Arrangement.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Property Name: ", style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (it.modifiedFrom == "A") Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = it.propertyName, style = TextStyle(fontSize = 16.sp))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Property Area: ", style = TextStyle(fontSize = 14.sp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = it.propertyArea.toString(), style = TextStyle(fontSize = 16.sp))
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(top = 4.dp)
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PropMasterContentPrev() {
    PropMasterContent(screenModel = PropMasterViewModel())
}