package cloud.justbilling.composesideeffects.models

data class NotesItem(
    val id: Int,
    val title: String,
    val description: String? = null,
    val lastModifiedDate: String
)
