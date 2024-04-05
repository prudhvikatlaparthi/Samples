package cloud.justbilling.composesideeffects.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun DecisionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            navController.navigate(ScreenRoute.MainScreen.route)
        }, modifier = Modifier.fillMaxWidth(0.5f)) {
            Text(text = "Main Screen")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            navController.navigate(ScreenRoute.SecondScreen.route)
        }, modifier = Modifier.fillMaxWidth(0.5f)) {
            Text(text = "Second Screen")
        }
    }
}