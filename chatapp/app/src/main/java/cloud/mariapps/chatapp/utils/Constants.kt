package cloud.mariapps.chatapp.utils

object Constants {
    const val kBaseUrl = "https://jsonplaceholder.typicode.com"
    const val kPreferenceName = "app-preferences"
    const val kInitialPage = 1
    const val kPageSize = 20
    const val DEFAULT_QUALITY_IDX = 0
    const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    enum class FromScreen(val value: String) {
        None("None"),
        CreateChat("CreateChat"),
        AddUsers("AddUsers"),
        SelectUsers("SelectUsers"),
    }
}