package cloud.justbilling.composesideeffects.presentation.notes_create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cloud.justbilling.composesideeffects.models.NotesItem
import cloud.justbilling.composesideeffects.repository.RepositorySDK
import cloud.justbilling.composesideeffects.utils.CommonUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteCreateViewModel @Inject constructor(private val repositorySDK: RepositorySDK) :
    ViewModel() {
    var titleState by mutableStateOf("")
    var descriptionState by mutableStateOf("")
    var lastModifiedDateState by mutableStateOf("")

    fun createOrUpdateNote(id: Int? = null) {
        val notesItem = NotesItem(
            id = id ?: CommonUtils.rand(),
            title = titleState,
            description = descriptionState,
            lastModifiedDate = SimpleDateFormat("MMM dd yyyy, H:mm", Locale.getDefault()).format(
                Date()
            )
        )
        repositorySDK.createNote(notesItem)
    }

    fun getNotesItem(id: Int): NotesItem? {
        return repositorySDK.getNote(id)
    }
}