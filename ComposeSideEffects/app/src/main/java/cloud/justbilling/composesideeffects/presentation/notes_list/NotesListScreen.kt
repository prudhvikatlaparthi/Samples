package cloud.justbilling.composesideeffects.presentation.notes_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cloud.justbilling.composesideeffects.models.NotesItem
import cloud.justbilling.composesideeffects.ui.theme.ScreenRoute
import cloud.justbilling.composesideeffects.utils.ParameterConstants

@Composable
fun NotesListScreen(
    navController: NavController,
    notesListViewModel: NotesListViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = "Notes")
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            navController.navigate(ScreenRoute.NotesCreateScreen.route)
        }) {
            Icon(Icons.Rounded.Add, contentDescription = "Localized description")
        }
    }) {
        LazyColumn {
            items(notesListViewModel.notesList.size) { index ->
                val note = notesListViewModel.notesList[index]
                NotesItemView(note) {
                    navController.navigate(ScreenRoute.NotesCreateScreen.route.plus("?${ParameterConstants.kIDArg}=${note.id}"))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NotesItemView(note: NotesItem, onItemClick: (note: NotesItem) -> Unit) {
    Card(elevation = 10.dp, modifier = Modifier.padding(10.dp), onClick = {
        onItemClick.invoke(note)
    }) {
        Row(modifier = Modifier.padding(10.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(text = note.title, style = MaterialTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = note.description ?: "", style = MaterialTheme.typography.subtitle2)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = note.lastModifiedDate, style = MaterialTheme.typography.caption)
        }
    }
}