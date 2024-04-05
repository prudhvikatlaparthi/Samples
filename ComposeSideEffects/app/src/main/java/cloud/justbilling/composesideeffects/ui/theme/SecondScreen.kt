package cloud.justbilling.composesideeffects.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun SecondScreen(navController: NavHostController, mainViewModel: MainViewModel = viewModel()) {
    Box {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Second Screen ${mainViewModel.position}")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                mainViewModel.position++
            }, modifier = Modifier.fillMaxWidth(0.5f)) {
                Text(text = "Increment")
            }
        }
    }
}