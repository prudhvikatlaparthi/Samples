package cloud.justbilling.composesideeffects.repository

import cloud.justbilling.composesideeffects.models.NotesItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositorySDK @Inject constructor() {
    private val notesList = mutableListOf<NotesItem>()

    fun createNote(notesItem: NotesItem) {
        getNote(notesItem.id)?.let {
            val item = it.copy(
                title = notesItem.title,
                description = notesItem.description,
                lastModifiedDate = notesItem.lastModifiedDate
            )
            notesList.remove(it)
            notesList.add(item)
        } ?: notesList.add(notesItem)
    }

    fun getAllNotes(): List<NotesItem> = notesList

    fun getNote(id: Int): NotesItem? {
        return notesList.filter { it.id == id }.getOrNull(0)
    }

    fun clearNotes() {
        notesList.clear()
    }
}