package cloud.justbilling.composesideeffects.presentation.notes_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cloud.justbilling.composesideeffects.repository.RepositorySDK
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val repositorySDK: RepositorySDK
) : ViewModel() {
    val notesList by mutableStateOf(repositorySDK.getAllNotes())



}
