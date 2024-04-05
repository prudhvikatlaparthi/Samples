package cloud.justbilling.composesideeffects.ui.theme

sealed class ScreenRoute(val route: String) {
    object DecisionScreen : ScreenRoute("DecisionScreen")
    object MainScreen : ScreenRoute("MainScreen")
    object SecondScreen : ScreenRoute("SecondScreen")

    object NotesListScreen : ScreenRoute("NotesListScreen")
    object NotesCreateScreen : ScreenRoute("NotesCreateScreen")
}
