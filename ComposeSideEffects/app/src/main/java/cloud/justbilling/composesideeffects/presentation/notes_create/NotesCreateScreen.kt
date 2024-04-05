package cloud.justbilling.composesideeffects.presentation.notes_create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cloud.justbilling.composesideeffects.models.NotesItem

@Composable
fun NotesCreateScreen(
    navController: NavController,
    id: Int,
    noteCreateViewModel: NoteCreateViewModel = hiltViewModel()
) {
    val isForUpdate = id != Int.MIN_VALUE
    LaunchedEffect(key1 = true) {
        if (isForUpdate) {
            val noteItem = noteCreateViewModel.getNotesItem(id)
            noteItem?.let {
                noteCreateViewModel.titleState = it.title
                noteCreateViewModel.descriptionState = it.description ?: ""
                noteCreateViewModel.lastModifiedDateState = it.lastModifiedDate ?: ""
            }
        }
    }
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = if (!isForUpdate) "Add Note" else "Update Note")
        }, actions = {
            Icon(
                Icons.Default.Save,
                contentDescription = Icons.Default.Save.toString(),
                modifier = Modifier.clickable {
                    noteCreateViewModel.createOrUpdateNote(id = if (!isForUpdate) null else id)
                    navController.popBackStack()
                }.padding(8.dp))
        })
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            OutlinedTextField(value = noteCreateViewModel.titleState, onValueChange = {
                noteCreateViewModel.titleState = it
            }, label = {
                Text("Title")
            }, modifier = Modifier.fillMaxWidth(), maxLines = 1)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = noteCreateViewModel.descriptionState, onValueChange = {
                noteCreateViewModel.descriptionState = it
            }, label = {
                Text("Description")
            }, modifier = Modifier.fillMaxWidth(), maxLines = 5)
            Spacer(modifier = Modifier.weight(1f))
            if (isForUpdate)
                Text("Last Modified ${noteCreateViewModel.lastModifiedDateState}")
        }
    }
}