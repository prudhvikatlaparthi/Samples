package cloud.mariapps.chatapp.model.internal

data class ToolbarItem(
    var title: String? = null,
    var enableDrawer: Boolean = false,
    var hideActionBar: Boolean = false,
    var hideBackArrow: Boolean = false
)
