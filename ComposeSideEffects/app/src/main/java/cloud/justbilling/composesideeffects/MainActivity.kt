package cloud.justbilling.composesideeffects

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cloud.justbilling.composesideeffects.presentation.notes_create.NotesCreateScreen
import cloud.justbilling.composesideeffects.presentation.notes_list.NotesListScreen
import cloud.justbilling.composesideeffects.ui.theme.*
import cloud.justbilling.composesideeffects.utils.ParameterConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSideEffectsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = ScreenRoute.NotesListScreen.route
                    ) {
                        composable(ScreenRoute.NotesListScreen.route) {
                            NotesListScreen(navController)
                        }
                        composable(ScreenRoute.NotesCreateScreen.route.plus("?${ParameterConstants.kIDArg}={${ParameterConstants.kIDArg}}"),
                        arguments = listOf(navArgument(ParameterConstants.kIDArg){
                            type = NavType.IntType
                            defaultValue = Int.MIN_VALUE
                        })) {
                            val id = it.arguments?.getInt(ParameterConstants.kIDArg) ?: Int.MIN_VALUE
                            NotesCreateScreen(navController, id = id)
                        }
                        composable(ScreenRoute.DecisionScreen.route) {
                            DecisionScreen(navController)
                        }
                        composable(ScreenRoute.MainScreen.route) {
                            MainScreen(navController)
                        }
                        composable(ScreenRoute.SecondScreen.route) {
                            SecondScreen(navController)
                        }
                    }
                }
            }
        }
    }
}