package cloud.mariapps.chatapp.model.internal

data class User(
    var userId: Int?,
    var userName: String?,
    var isSelected : Boolean = false
)
